package com.limspyne.anon_vote.shared.domain.services;

import com.limspyne.anon_vote.shared.domain.dto.telegram.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class TelegramUpdateCommandHandler extends TelegramUpdateHandler {
    private BotCommand command;

    public TelegramUpdateCommandHandler(BotCommand command) {
        super();
        this.command = command;
    }

    @Override
    public boolean canHandle(Update update) {
        return command.matches(update.getMessage().getText());
    }
}
