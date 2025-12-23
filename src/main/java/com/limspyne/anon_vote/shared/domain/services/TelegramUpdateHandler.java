package com.limspyne.anon_vote.shared.domain.services;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramUpdateHandler {
    boolean canHandle(Update update);

    void handle(Update update, DefaultAbsSender sender);
}