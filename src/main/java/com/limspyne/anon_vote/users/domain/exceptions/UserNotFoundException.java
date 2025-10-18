package com.limspyne.anon_vote.users.domain.exceptions;

import com.limspyne.anon_vote.shared.domain.exceptions.AppBasicException;

public class UserNotFoundException extends AppBasicException {
    public UserNotFoundException() {
        super("User not found!");
    }
}
