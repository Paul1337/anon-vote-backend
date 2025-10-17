package com.limspyne.anon_vote.users.domain.exceptions;

import com.limspyne.anon_vote.shared.AppBasicException;

public class UserExistsException extends AppBasicException {
    public UserExistsException() {
        super("User already exists!");
    }
}
