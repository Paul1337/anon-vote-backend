package com.limspyne.anon_vote.users.application.exceptions;

import com.limspyne.anon_vote.shared.application.exceptions.AppBasicException;

public class UserNotFoundException extends AppBasicException {
    public UserNotFoundException() {
        super("User not found!");
    }
}
