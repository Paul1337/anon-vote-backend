package com.limspyne.anon_vote.shared.inftrastrucure.telegram;

import com.limspyne.anon_vote.shared.application.telegram.services.BotCommandRegistry;
import com.limspyne.anon_vote.shared.application.telegram.services.TelegramInteractionService;
import com.limspyne.anon_vote.shared.application.telegram.dto.TelegramDto;
import jakarta.annotation.PostConstruct;
import org.glassfish.jersey.innate.virtual.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AppTelegramBot extends TelegramLongPollingBot {
    private final String botUsername;

    private final String botToken;

    private final TelegramInteractionService interactionService;

    private final TelegramResponseProvider telegramResponseProvider;

    private final BotCommandRegistry botCommandRegistry;

    private static final Logger logger = LoggerFactory.getLogger(AppTelegramBot.class);

    private final ExecutorService executor =
            new ThreadPoolExecutor(
                    8,
                    8,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    private final ConcurrentHashMap<Long, UserQueue> userQueues = new ConcurrentHashMap<>();

    private static class UserQueue {
        final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
        final AtomicBoolean processing = new AtomicBoolean(false);
    }

    public AppTelegramBot(@Value("${telegram.bot.username}") String botUsername,
                          @Value("${telegram.bot.token}") String botToken,
                          TelegramInteractionService interactionService,
                          TelegramResponseProvider telegramResponseProvider,
                          BotCommandRegistry botCommandRegistry) {
        super(botToken);
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.interactionService = interactionService;
        this.telegramResponseProvider = telegramResponseProvider;
        this.botCommandRegistry = botCommandRegistry;
    }

    @Override
    public void onUpdateReceived(Update update) {
        TelegramDto.Request request = TelegramDto.Request.from(update);
        if (request == null) return;

        // User-bound threads для избежания race-condition при взаимодействии с телеграм сессией пользователя

        UserQueue userQueue = userQueues.computeIfAbsent(request.getTelegramId(), id -> new UserQueue());
        userQueue.tasks.add(() -> processRequest(request));

        tryStartProcessing(request.getTelegramId(), userQueue);
    }

    private void tryStartProcessing(long userId, UserQueue userQueue) {
        if (userQueue.processing.compareAndSet(false, true)) {
            executor.submit(() -> {
                try {
                    Runnable task;
                    while ((task = userQueue.tasks.poll()) != null) {
                        task.run();
                    }
                } finally {
                    userQueue.processing.set(false);

                    // если пока обрабатывали — прилетели новые задачи
                    if (!userQueue.tasks.isEmpty()) {
                        tryStartProcessing(userId, userQueue);
                    } else {
                        userQueues.remove(userId, userQueue);
                    }
                }
            });
        }
    }

    private void processRequest(TelegramDto.Request request) {
        String threadName = Thread.currentThread().getName();

        logger.info("Поток {}: начало обработки telegram request", threadName);

        TelegramDto.Response response = interactionService.handle(request);
        if (response == null) return;
        executeMessage(telegramResponseProvider.getResponseMessage(response));

        // возможно команда завершилась и в очереди команд есть следующая, тогда надо её выполнить сразу
        // это например используется, когда даётся ссылка на опрос неавторизованному пользователю
        // в этом случае вначале выполняется команда авторизации, затем ответ на опрос
        if (response.isCommandFinished()) {
            var nextCommandResponse = interactionService.handleNextCommand(new TelegramDto.Request("", request.getTelegramId()));
            if (nextCommandResponse != null) {
                executeMessage(telegramResponseProvider.getResponseMessage(nextCommandResponse));
            }
        }

        logger.info("Поток {}: конец обработки telegram request", threadName);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @PostConstruct
    void init() {
        registerCommands();
    }

    private void registerCommands() {
        try {
            List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commands =
                    botCommandRegistry.globalCommands()
                            .stream()
                            .map(cmd -> new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(
                                    cmd.getCommand(),
                                    cmd.getButtonText()
                            ))
                            .toList();

            SetMyCommands setMyCommands = SetMyCommands.builder()
                    .commands(commands)
                    .scope(new BotCommandScopeDefault())
                    .build();

            execute(setMyCommands);

            logger.info("Telegram bot commands registered: {}", commands);
        } catch (TelegramApiException e) {
            logger.error("Failed to register bot commands", e);
        }
    }

}
