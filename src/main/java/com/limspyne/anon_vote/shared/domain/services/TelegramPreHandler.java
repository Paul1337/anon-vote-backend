package com.limspyne.anon_vote.shared.domain.services;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class TelegramPreHandler extends TelegramHandler {
    abstract public boolean handle(Update update, DefaultAbsSender sender);
}
