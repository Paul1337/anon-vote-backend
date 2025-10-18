package com.limspyne.anon_vote.poll.domain.exceptions;

import com.limspyne.anon_vote.shared.domain.exceptions.AppBasicException;

public class SubmitNotUniqueException extends AppBasicException {
    public SubmitNotUniqueException() {
        super("Submit is not unique!");
    }
}
