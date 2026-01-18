package com.limspyne.anon_vote.users.application.exceptions;

import com.limspyne.anon_vote.shared.application.exceptions.AppBasicException;

public class UserExistsException extends AppBasicException {
    public UserExistsException() {
        super("User already exists!");
    }
}
