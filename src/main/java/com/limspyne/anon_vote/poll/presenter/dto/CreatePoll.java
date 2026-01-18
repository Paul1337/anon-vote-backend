package com.limspyne.anon_vote.poll.presenter.dto;

import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyListValidator.class)
 @interface NotEmptyList {
    String message() default "Все элементы списка должны быть не пустыми";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, List<String>> {
    @Override
    public boolean isValid(List<String> list, ConstraintValidatorContext context) {
        if (list == null) return true;

        return list.stream()
                .allMatch(option -> option != null && !option.trim().isEmpty());
    }
}

public class CreatePoll {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        @NotBlank(message = "Текст вопроса обязателен")
        private String text;

        @Size(min = 2, message = "Должно быть минимум 2 варианта ответа")
        @NotEmptyList(message = "Все варианты ответа должны быть не пустыми")
        private List<String> options;
    }

    public record Request(
            @NotBlank(message = "Название опроса обязательно")
            @Size(min = 5, max = 100, message = "Название должно быть от 5 до 100 символов")
            String title,

            @NotEmpty(message = "Хотя бы 1 вопрос должен присутствовать в массиве questions")
            @Valid
            List<QuestionDto> questions,

            @NotBlank(message = "Нужно передать categoryId (id категории)")
            String categoryId,

            String categoryName,

            Set<String> tags
    ) {}
}
