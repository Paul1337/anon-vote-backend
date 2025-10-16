package com.limspyne.anon_vote.users.exceptions;

import com.limspyne.anon_vote.shared.AppBasicException;

public class ConfirmationCodeNotRightException extends AppBasicException {
    public ConfirmationCodeNotRightException() {
        super("Confirmation code not right");
    }
}
