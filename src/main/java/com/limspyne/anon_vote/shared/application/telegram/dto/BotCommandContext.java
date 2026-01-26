package com.limspyne.anon_vote.shared.application.telegram.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Getter
@Setter
public abstract class BotCommandContext implements Cloneable {
    private boolean isFinished;

    @Override
    public BotCommandContext clone() {
        try {
            return (BotCommandContext) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
