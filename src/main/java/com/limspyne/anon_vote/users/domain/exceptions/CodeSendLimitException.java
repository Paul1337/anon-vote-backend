package com.limspyne.anon_vote.users.domain.exceptions;

import com.limspyne.anon_vote.shared.domain.exceptions.AppBasicException;

public class CodeSendLimitException extends AppBasicException {
    public CodeSendLimitException(String email) {
        super("Attempt to send code too often for email %s!".formatted(email));
    }
}
