package com.limspyne.anon_vote.poll.presenter.validation.uniqueoptions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueOptionsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueOptions {
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}