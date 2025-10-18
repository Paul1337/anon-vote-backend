package com.limspyne.anon_vote.poll.domain.exceptions;

import com.limspyne.anon_vote.shared.AppBasicException;

import java.util.UUID;

public class CategoryNotFoundException extends AppBasicException {
    public CategoryNotFoundException(UUID categoryId) {
        super("Category with id %s not found!".formatted(categoryId.toString()));
    }
}
