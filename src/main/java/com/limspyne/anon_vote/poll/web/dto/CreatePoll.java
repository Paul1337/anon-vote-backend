package com.limspyne.anon_vote.poll.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreatePoll {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        private String text;
        private List<String> options;
    }

    public record Request(
            @NotBlank(message = "Название опроса обязательно")
            @Size(min = 5, max = 100, message = "Название должно быть от 5 до 100 символов")
            String title,

            @NotEmpty(message = "Хотя бы 1 вопрос должен присутствовать в массиве questions")
            List<QuestionDto> questions,

            @NotBlank(message = "Нужно передать categoryId (id категории)")
            String categoryId,

            Set<String> tags
    ) {}
}
