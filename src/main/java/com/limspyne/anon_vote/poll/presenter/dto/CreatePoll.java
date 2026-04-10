package com.limspyne.anon_vote.poll.presenter.dto;

import com.limspyne.anon_vote.poll.presenter.validation.notemptylist.NotEmptyList;
import com.limspyne.anon_vote.poll.presenter.validation.uniqueoptions.UniqueOptions;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

public class CreatePoll {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        @NotBlank(message = "Текст вопроса обязателен")
        private String text;

        @Size(min = 2, message = "Должно быть минимум 2 варианта ответа")
        @NotEmptyList(message = "Все варианты ответа должны быть не пустыми")
        @UniqueOptions(message = "Варианты ответа не должны повторяться")
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
