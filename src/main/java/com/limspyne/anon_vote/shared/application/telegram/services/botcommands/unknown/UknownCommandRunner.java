package com.limspyne.anon_vote.shared.application.telegram.services.botcommands.unknown;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import com.limspyne.anon_vote.shared.application.telegram.services.CommandRunner;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UknownCommandRunner extends CommandRunner {
    @Override
    protected boolean canRun(BotCommand botCommand) {
        return botCommand == BotCommand.UNKNOWN_COMMAND;
    }

    @Override
    public TelegramDto.Response handleCommand(TelegramDto.Request request, BotCommandContext context) {
        context.setFinished(true);
        return request.replyBuilder().text("Неизвестная команда").build();
    }
}
