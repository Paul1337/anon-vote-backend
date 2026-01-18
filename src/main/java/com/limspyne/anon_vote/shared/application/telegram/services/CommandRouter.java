package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.exceptions.telegram.CommandRunnerNotFound;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import com.limspyne.anon_vote.shared.inftrastrucure.repositories.UserTelegramSessionRepository;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandRouter {
    private final List<CommandRunner> commandRunners;

    private final UserTelegramSessionRepository telegramSessionRepository;

    public TelegramDto.Response handleActiveCommand(TelegramDto.Request request, UserTelegramSession session) {
        return runCommand(request, session);
    }

    public TelegramDto.Response startNewCommand(TelegramDto.Request request, BotCommand botCommand) {
        var newSession = UserTelegramSession.emptyForCommand(request.getTelegramId(), botCommand);
        telegramSessionRepository.save(newSession);

        return runCommand(request, newSession);
    }

    private TelegramDto.Response runCommand(TelegramDto.Request request, UserTelegramSession session) {
        CommandRunner runner = findRunner(session.getActiveCommand());
        return runner.handleCommand(request, session);
    }

    private CommandRunner findRunner(BotCommand command) {
        return commandRunners.stream()
                .filter(r -> r.canRun(command))
                .findFirst()
                .orElseThrow(() -> new CommandRunnerNotFound(command));
    }
}
