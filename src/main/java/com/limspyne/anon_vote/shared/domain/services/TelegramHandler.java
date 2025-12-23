package com.limspyne.anon_vote.shared.domain.services;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class TelegramHandler {
    protected void sendMessage(DefaultAbsSender sender, Long chatId, String text) {
        try {
            sender.execute(new SendMessage(chatId.toString(), text));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendMessage(DefaultAbsSender sender, SendMessage sendMessage) {
        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
