package com.limspyne.anon_vote.shared;

import lombok.Getter;

public class AppBasicException extends RuntimeException {
    public AppBasicException(String message) {
        super(message);
    }
}
