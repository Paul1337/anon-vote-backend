package com.limspyne.anon_vote.poll.exceptions;

import com.limspyne.anon_vote.shared.AppBasicException;

import java.util.UUID;

public class PollNotFoundException extends AppBasicException {
    public PollNotFoundException(UUID quizId) {
        super("Poll with id %s not found!".formatted(quizId.toString()));
    }
}
