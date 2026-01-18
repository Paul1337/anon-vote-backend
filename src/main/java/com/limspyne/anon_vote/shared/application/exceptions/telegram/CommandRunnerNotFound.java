package com.limspyne.anon_vote.shared.application.exceptions.telegram;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;

public class CommandRunnerNotFound extends RuntimeException {
    public CommandRunnerNotFound(BotCommand botCommand) {
        super("Not found suitable runner for command %s".formatted(botCommand));
    }
}
