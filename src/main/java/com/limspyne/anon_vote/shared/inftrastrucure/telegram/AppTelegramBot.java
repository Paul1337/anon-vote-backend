package com.limspyne.anon_vote.shared.inftrastrucure.telegram;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.services.BotCommandRegistry;
import com.limspyne.anon_vote.shared.application.telegram.services.TelegramInteractionService;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class AppTelegramBot extends TelegramLongPollingBot {
    private final String botUsername;

    private final String botToken;

    private final TelegramInteractionService interactionService;

    private final TelegramResponseProvider telegramResponseProvider;

    private final BotCommandRegistry botCommandRegistry;

    private static final Logger logger = LoggerFactory.getLogger(AppTelegramBot.class);

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

        TelegramDto.Response response = interactionService.handle(request);
        if (response == null) return;
        executeMessage(telegramResponseProvider.getResponseMessage(response));

        if (response.isCommandFinished()) {
            var nextCommandResponse = interactionService.handleNextCommand(new TelegramDto.Request("", request.getTelegramId()));
            if (nextCommandResponse != null) {
                executeMessage(telegramResponseProvider.getResponseMessage(nextCommandResponse));
            }
        }
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
