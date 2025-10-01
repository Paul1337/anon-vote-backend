package com.limspyne.anon_vote.poll.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SearchPolls {
    @Data
    @NoArgsConstructor
    public static class Request {
        private String title = "";

        @Min(value = 0, message = "Page должен быть >= 0")
        private int page = 0;

        @Min(value = 1, message = "Size должен быть >= 1")
        private int size = 10;

        private Set<String> tags = new HashSet<>();

        private UUID categoryId;
    }
}
