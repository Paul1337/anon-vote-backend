package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandData;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import com.limspyne.anon_vote.shared.inftrastrucure.repositories.UserTelegramSessionRepository;
import com.limspyne.anon_vote.users.application.services.botcommands.auth.AuthCommandContext;
import com.limspyne.anon_vote.users.application.services.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TelegramInteractionService {
    private final UserTelegramSessionRepository telegramSessionRepository;

    private final CommandRouter commandRouter;

    private final UserAuthService userAuthService;

    private final StartLinkHandler startLinkHandler;

    public TelegramDto.Response handle(TelegramDto.Request request) {
        var session = telegramSessionRepository.getOrCreate(request.getTelegramId());

        if (!session.isAuthed()) {
            session.setAuthed(userAuthService.isAuthedByTelegramId(request.getTelegramId()));
        }

        var startLinkResult = startLinkHandler.tryHandleStartLink(request, session);
        if (startLinkResult != null) return startLinkResult;

        if (session.hasActiveCommand()) {
            return commandRouter.handleActiveCommand(request, session);
        }

        if (!session.isAuthed()) {
            return commandRouter.startNewCommand(request, new BotCommandData(BotCommand.AUTH, new AuthCommandContext()), session);
        }

        var matchedCommand = Arrays.stream(BotCommand.values())
                .filter(command -> command.matches(request.getText())).findFirst();
        if (matchedCommand.isPresent()) {
            return commandRouter.startNewCommand(request, matchedCommand.get(), session);
        }

        return commandRouter.startNewCommand(request, BotCommand.UNKNOWN_COMMAND, session);
    }

    public TelegramDto.Response handleNextCommand(TelegramDto.Request request) {
        var session = telegramSessionRepository.getOrCreate(request.getTelegramId());
        if (session.hasActiveCommand()) {
            return commandRouter.handleActiveCommand(request, session);
        } else {
            return null;
        }
    }

}
