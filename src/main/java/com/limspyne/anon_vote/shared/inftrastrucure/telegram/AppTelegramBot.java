package com.limspyne.anon_vote.shared.inftrastrucure.telegram;
import com.limspyne.anon_vote.shared.domain.dto.telegram.BotCommand;
import com.limspyne.anon_vote.shared.domain.services.TelegramUpdateDispatcher;
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

    private final TelegramUpdateDispatcher telegramUpdateDispatcher;

    private static final Logger logger = LoggerFactory.getLogger(AppTelegramBot.class);

    public AppTelegramBot(@Value("${telegram.bot.username}") String botUsername,
                          @Value("${telegram.bot.token}") String botToken,
                          TelegramUpdateDispatcher telegramUpdateDispatcher) {
        super(botToken);
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.telegramUpdateDispatcher = telegramUpdateDispatcher;
    }

    @Override
    public void onUpdateReceived(Update update) {
        telegramUpdateDispatcher.handle(update, this);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @PostConstruct
    public void init() {
        registerCommands();
    }

    private void registerCommands() {
        try {
            List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commands =
                    Arrays.stream(BotCommand.values())
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
