package com.limspyne.anon_vote.poll.application.exceptions;

import com.limspyne.anon_vote.shared.application.exceptions.AppBasicException;

import java.util.UUID;

public class PollNotFoundException extends AppBasicException {
    public PollNotFoundException(UUID pollId) {
        super("Poll with id %s not found!".formatted(pollId.toString()));
    }
}
