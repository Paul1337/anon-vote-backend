package com.limspyne.anon_vote.poll.presenter.validation.notemptylist;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, List<String>> {
    @Override
    public boolean isValid(List<String> list, ConstraintValidatorContext context) {
        if (list == null) return true;

        return list.stream()
                .allMatch(option -> option != null && !option.trim().isEmpty());
    }
}
