package com.limspyne.anon_vote.users.domain.exceptions;

import com.limspyne.anon_vote.shared.domain.exceptions.AppBasicException;

public class CouldNotSendCodeException extends AppBasicException {
    public CouldNotSendCodeException(String email) {
        super("Error sending message to email %s".formatted(email));
    }
}
