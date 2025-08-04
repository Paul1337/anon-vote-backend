package com.limspyne.anon_vote.poll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePoll {
    public record Request(
            @NotBlank(message = "Название опроса обязательно")
            @Size(min = 5, max = 100, message = "Название должно быть от 5 до 100 символов")
            String title
    ) {}

    public record Response(
            String id,
            String title
    ) {}
}
