package com.limspyne.anon_vote.users.application.services.botcommands.auth;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthCommandContext extends BotCommandContext implements Cloneable {
    private RegistrationState state = RegistrationState.NONE;

    @Override
    public AuthCommandContext clone() {
        return (AuthCommandContext) super.clone();
    }

    public enum RegistrationState {
        NONE,
        WAIT_EMAIL,
        WAIT_CODE
    }
}
