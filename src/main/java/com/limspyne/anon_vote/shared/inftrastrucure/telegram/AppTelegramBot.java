package com.limspyne.anon_vote.shared.inftrastrucure.telegram;
import com.limspyne.anon_vote.shared.domain.services.TelegramUpdateDispatcher;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

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
        logger.info("Recieved update");
        telegramUpdateDispatcher.handle(update, this);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
