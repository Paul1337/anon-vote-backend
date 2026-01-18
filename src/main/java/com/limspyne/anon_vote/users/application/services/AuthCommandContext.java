package com.limspyne.anon_vote.users.application.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthCommandContext implements BotCommandContext {
    private RegistrationState state = RegistrationState.NONE;

    @Override
    public BotCommand getCommand() {
        return BotCommand.AUTH;
    }

    public enum RegistrationState {
        NONE,
        WAIT_EMAIL,
        WAIT_CODE
    }
}
