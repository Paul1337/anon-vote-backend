package com.limspyne.anon_vote.shared.domain.services;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramMessageSender {
    void sendMessage(Long chatId, String text) throws TelegramApiException;
}
