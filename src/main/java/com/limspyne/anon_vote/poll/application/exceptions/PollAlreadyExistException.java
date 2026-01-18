package com.limspyne.anon_vote.poll.application.exceptions;

import com.limspyne.anon_vote.shared.application.exceptions.AppBasicException;

import java.util.UUID;

public class PollAlreadyExistException extends AppBasicException {
    public PollAlreadyExistException(UUID quizId) {
        super("Quiz with id %s already exists!".formatted(quizId.toString()));
    }
}
