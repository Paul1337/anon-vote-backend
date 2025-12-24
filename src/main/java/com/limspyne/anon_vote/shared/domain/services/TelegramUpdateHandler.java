package com.limspyne.anon_vote.shared.domain.services;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class TelegramUpdateHandler extends TelegramHandler {
    public abstract boolean canHandle(Update update);

    public abstract void handle(Update update, DefaultAbsSender sender);
}