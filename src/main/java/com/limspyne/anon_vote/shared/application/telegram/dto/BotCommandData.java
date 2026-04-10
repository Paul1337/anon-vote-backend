package com.limspyne.anon_vote.shared.application.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class BotCommandData {
    @NonNull
    private final BotCommand botCommand;

    @NonNull
    private final BotCommandContext context;

    public static BotCommandData forCommand(BotCommand botCommand) {
        return new BotCommandData(botCommand, botCommand.getInitialContext());
    }
}
