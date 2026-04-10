package com.limspyne.anon_vote.users.application.exceptions;

import com.limspyne.anon_vote.shared.application.exceptions.AppBasicException;

public class CouldNotSendCodeException extends AppBasicException {
    public CouldNotSendCodeException(String email) {
        super("Error sending message to email %s".formatted(email));
    }
}
