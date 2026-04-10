package com.limspyne.anon_vote.poll.application.exceptions;

import com.limspyne.anon_vote.shared.application.exceptions.AppBasicException;

import java.util.UUID;

public class DuplicateAnswerInPollException extends AppBasicException {
    public DuplicateAnswerInPollException() {
        super("Poll question has duplicate answers!");
    }
}
