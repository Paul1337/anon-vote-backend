package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import com.limspyne.anon_vote.shared.inftrastrucure.repositories.UserTelegramSessionRepository;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UknownCommandRunner extends CommandRunner {
    private final UserTelegramSessionRepository userTelegramSessionRepository;

    @Override
    protected boolean canRun(BotCommand botCommand) {
        return botCommand == BotCommand.UNKNOWN_COMMAND;
    }

    @Override
    public TelegramDto.Response handleCommand(TelegramDto.Request request, UserTelegramSession session) {
        userTelegramSessionRepository.clear(request.getTelegramId());
        return TelegramDto.Response.forChat(request.getTelegramId()).text("Неизвестная команда").build();
    }
}
