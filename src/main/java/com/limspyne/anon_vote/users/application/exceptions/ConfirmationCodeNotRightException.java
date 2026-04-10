package com.limspyne.anon_vote.users.application.exceptions;

import com.limspyne.anon_vote.shared.application.exceptions.AppBasicException;

public class ConfirmationCodeNotRightException extends AppBasicException {
    public ConfirmationCodeNotRightException() {
        super("Confirmation code not right");
    }
}
