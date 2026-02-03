package com.limspyne.anon_vote.shared.application.telegram.services.commands.mainmenu;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import com.limspyne.anon_vote.shared.application.telegram.services.CommandRunner;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import org.springframework.stereotype.Component;

@Component
public class MainMenuCommandRunner extends CommandRunner {
    @Override
    protected boolean canRun(BotCommand botCommand) {
        return botCommand == BotCommand.TO_MAIN_MENU;
    }

    @Override
    public TelegramDto.Response handleCommand(TelegramDto.Request request, BotCommandContext context) {
        context.setFinished(true);
        return request.replyBuilder().text("Текущие команды сброшены, вы в главном меню").build();
    }
}
