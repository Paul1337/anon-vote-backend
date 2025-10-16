package com.limspyne.anon_vote.poll.exceptions;

import com.limspyne.anon_vote.shared.AppBasicException;

import java.util.UUID;

public class SubmitNotUniqueException extends AppBasicException {
    public SubmitNotUniqueException() {
        super("Submit is not unique!");
    }
}
