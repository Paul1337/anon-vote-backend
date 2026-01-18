package com.limspyne.anon_vote.shared.inftrastrucure.telegram;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.services.TelegramInteractionService;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;

@Component
public class AppTelegramBot extends TelegramLongPollingBot {
    private final String botUsername;

    private final String botToken;

    private final TelegramInteractionService interactionService;

    private final TelegramSender telegramSender;

    private static final Logger logger = LoggerFactory.getLogger(AppTelegramBot.class);

    public AppTelegramBot(@Value("${telegram.bot.username}") String botUsername,
                          @Value("${telegram.bot.token}") String botToken,
                          TelegramInteractionService interactionService) {
        super(botToken);
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.interactionService = interactionService;
        this.telegramSender = new TelegramSender(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        var request = TelegramDto.Request.from(update);
        if (request == null) return;
        TelegramDto.Response response = interactionService.handle(request);
        telegramSender.send(response);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @PostConstruct
    void init() {
        registerCommands();
    }

    @PreDestroy
    public void shutdown() {
        this.onClosing();
    }

    private void registerCommands() {
        try {
             List<BotCommand> globalCommands = List.of(
                    BotCommand.SEARCH_POLLS,
                    BotCommand.MY_POLLS,
                    BotCommand.CREATE_POLL
             );

            List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commands =
                    globalCommands
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
