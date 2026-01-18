package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import com.limspyne.anon_vote.shared.inftrastrucure.repositories.UserTelegramSessionRepository;
import com.limspyne.anon_vote.users.application.services.AuthCommandContext;
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

    public TelegramDto.Response handle(TelegramDto.Request request) {
        var session = telegramSessionRepository.get(request.getTelegramId());

        if (session.isPresent() && session.get().getActiveCommand() != null) {
            return commandRouter.handleActiveCommand(request, session.get());
        }

        if (!userAuthService.isAuthedByTelegramId(request.getTelegramId())) {
            return commandRouter.startNewCommand(request, BotCommand.AUTH);
        }

        var matchedCommand = Arrays.stream(BotCommand.values()).filter(command -> command.matches(request.getText())).findFirst();
        if (matchedCommand.isPresent()) {
            return commandRouter.startNewCommand(request, matchedCommand.get());
        }

        return commandRouter.startNewCommand(request, BotCommand.UNKNOWN_COMMAND);
    }

}
