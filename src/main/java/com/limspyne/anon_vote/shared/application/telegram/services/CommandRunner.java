package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class CommandRunner {
    protected abstract boolean canRun(BotCommand botCommand);

    public abstract TelegramDto.Response handleCommand(TelegramDto.Request request, BotCommandContext context);
}
