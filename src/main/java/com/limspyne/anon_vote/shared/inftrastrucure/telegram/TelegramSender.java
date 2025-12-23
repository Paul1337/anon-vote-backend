package com.limspyne.anon_vote.shared.inftrastrucure.telegram;

import com.limspyne.anon_vote.shared.domain.services.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramSender implements TelegramMessageSender {
    private final AppTelegramBot bot;

    @Override
    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();

        bot.execute(message);
    }
}

