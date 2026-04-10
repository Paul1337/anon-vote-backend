package com.limspyne.anon_vote.shared.inftrastrucure.telegram;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramBotLifecycle {
    private final AppTelegramBot bot;

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotLifecycle.class);

    // нужно для корректного завершения long-polling бота при перезапуске проекта
    @PreDestroy
    void shutdown() {
        logger.info("Shutting down..");
        bot.onClosing();
    }
}
