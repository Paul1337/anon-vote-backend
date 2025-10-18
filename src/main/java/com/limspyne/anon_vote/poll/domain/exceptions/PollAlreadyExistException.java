package com.limspyne.anon_vote.poll.domain.exceptions;

import com.limspyne.anon_vote.shared.domain.exceptions.AppBasicException;

import java.util.UUID;

public class PollAlreadyExistException extends AppBasicException {
    public PollAlreadyExistException(UUID quizId) {
        super("Quiz with id %s already exists!".formatted(quizId.toString()));
    }
}
