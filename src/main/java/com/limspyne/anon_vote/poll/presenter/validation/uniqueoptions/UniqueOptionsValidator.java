package com.limspyne.anon_vote.poll.presenter.validation.uniqueoptions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueOptionsValidator implements ConstraintValidator<UniqueOptions, List<String>> {
    @Override
    public boolean isValid(List<String> options, ConstraintValidatorContext context) {
        if (options == null || options.isEmpty()) {
            return true;
        }
        
        Set<String> uniqueOptions = new HashSet<>();
        for (String option : options) {
            if (option != null && !uniqueOptions.add(option.trim().toLowerCase())) {
                return false;
            }
        }

        return true;
    }
}