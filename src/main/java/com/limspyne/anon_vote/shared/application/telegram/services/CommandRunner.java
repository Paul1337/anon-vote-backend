package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;

public abstract class CommandRunner {
    protected abstract boolean canRun(BotCommand botCommand);

    public abstract TelegramDto.Response handleCommand(TelegramDto.Request request, BotCommandContext context);
}
