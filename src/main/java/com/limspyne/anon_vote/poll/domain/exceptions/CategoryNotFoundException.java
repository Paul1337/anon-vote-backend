package com.limspyne.anon_vote.poll.domain.exceptions;

import com.limspyne.anon_vote.shared.domain.exceptions.AppBasicException;

import java.util.UUID;

public class CategoryNotFoundException extends AppBasicException {
    public CategoryNotFoundException(UUID categoryId) {
        super("Category with id %s not found!".formatted(categoryId.toString()));
    }

    public CategoryNotFoundException() {
        super("Category not found!");
    }
}
