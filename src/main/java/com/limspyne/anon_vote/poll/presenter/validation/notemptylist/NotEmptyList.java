package com.limspyne.anon_vote.poll.presenter.validation.notemptylist;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyListValidator.class)
public @interface NotEmptyList {
    String message() default "Все элементы списка должны быть не пустыми";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
