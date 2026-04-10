package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultBotCommandRegistry implements BotCommandRegistry {
    @Override
    public List<BotCommand> globalCommands() {
        return List.of(BotCommand.ANSWER_POLL, BotCommand.TO_MAIN_MENU);
    }
}
