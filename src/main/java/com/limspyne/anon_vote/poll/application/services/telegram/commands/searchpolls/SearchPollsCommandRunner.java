package com.limspyne.anon_vote.poll.application.services.telegram.commands.searchpolls;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import com.limspyne.anon_vote.shared.application.telegram.services.CommandRunner;
import com.limspyne.anon_vote.shared.inftrastrucure.repositories.UserTelegramSessionRepository;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchPollsCommandRunner extends CommandRunner {
    private final UserTelegramSessionRepository telegramSessionRepository;

    @Override
    protected boolean canRun(BotCommand botCommand) {
        return botCommand == BotCommand.SEARCH_POLLS;
    }

    @Override
    public TelegramDto.Response handleCommand(TelegramDto.Request request, UserTelegramSession session) {
        return null;
    }
}
