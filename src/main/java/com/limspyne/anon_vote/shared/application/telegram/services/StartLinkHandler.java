package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.poll.application.services.botcommands.answerpoll.AnswerPollContext;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandData;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import com.limspyne.anon_vote.users.application.services.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StartLinkHandler {
    private final CommandRouter commandRouter;

    private final UserAuthService userAuthService;

    public TelegramDto.Response tryHandleStartLink(TelegramDto.Request request, UserTelegramSession session) {
        String text = request.getText();
        if (!text.startsWith("/start")) {
            return null;
        }
        String requestPart = text.substring("/start".length()).trim();

        if (requestPart.startsWith("poll_")) {
            var pollId = UUID.fromString(requestPart.substring("poll_".length()));

            if (session.isAuthed()) {
                return commandRouter.startNewCommand(request,
                        new BotCommandData(BotCommand.ANSWER_POLL, new AnswerPollContext(pollId)), session);
            } else {
                session.clearCommandsQueue();
                session.addCommand(BotCommandData.forCommand(BotCommand.AUTH));
                session.addCommand(new BotCommandData(BotCommand.ANSWER_POLL, new AnswerPollContext(pollId)));
                return commandRouter.handleActiveCommand(request, session);
            }
        }

        return null;
    }
}
