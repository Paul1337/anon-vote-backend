package com.limspyne.anon_vote.shared.application.telegram.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;

import java.util.List;

public interface BotCommandRegistry {
    List<BotCommand> globalCommands();
}
