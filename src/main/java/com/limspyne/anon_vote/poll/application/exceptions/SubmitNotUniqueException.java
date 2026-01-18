package com.limspyne.anon_vote.poll.application.exceptions;

import com.limspyne.anon_vote.shared.application.exceptions.AppBasicException;

public class SubmitNotUniqueException extends AppBasicException {
    public SubmitNotUniqueException() {
        super("Submit is not unique!");
    }
}
