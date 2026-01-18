package com.limspyne.anon_vote.shared.application.telegram.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public interface BotCommandContext {
    BotCommand getCommand();
}
