package com.limspyne.anon_vote.shared.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramUpdateDispatcher implements TelegramUpdateHandler {
    private final List<TelegramUpdateHandler> handlers;

    private final List<TelegramPreHandler> preHandlers;

    @Override
    public boolean canHandle(Update update) {
        return true;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) {
        for (TelegramPreHandler preHandler : preHandlers) {
            if (preHandler.handle(update, sender)) {
                return;
            }
        }

        for (TelegramUpdateHandler handler : handlers) {
            if (handler.canHandle(update)) {
                handler.handle(update, sender);
                break;
            }
        }
    }
}
