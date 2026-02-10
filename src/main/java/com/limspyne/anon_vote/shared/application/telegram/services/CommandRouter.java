package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.exceptions.telegram.CommandRunnerNotFound;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandData;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import com.limspyne.anon_vote.shared.inftrastrucure.repositories.UserTelegramSessionRepository;
import com.limspyne.anon_vote.shared.application.telegram.dto.TelegramDto;
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

    public TelegramDto.Response startNewCommand(TelegramDto.Request request, BotCommandData data, UserTelegramSession session) {
        session.addCommand(data);
        telegramSessionRepository.save(session);
        return runCommand(request, session);
    }

    public TelegramDto.Response startNewCommand(TelegramDto.Request request, BotCommand botCommand, UserTelegramSession session) {
        session.addCommand(BotCommandData.forCommand(botCommand));
        telegramSessionRepository.save(session);
        return runCommand(request, session);
    }

    private TelegramDto.Response runCommand(TelegramDto.Request request, UserTelegramSession session) {
        var activeCommandData = session.getActiveCommandData();
        CommandRunner runner = findRunner(activeCommandData.getBotCommand());
        var response = runner.handleCommand(request, activeCommandData.getContext());
        if (activeCommandData.getContext().isFinished()) {
            session.finishActiveCommand();
            response.setCommandFinished(true);
        }

        telegramSessionRepository.save(session);
        return response;
    }

    private CommandRunner findRunner(BotCommand command) {
        return commandRunners.stream()
                .filter(r -> r.canRun(command))
                .findFirst()
                .orElseThrow(() -> new CommandRunnerNotFound(command));
    }
}
