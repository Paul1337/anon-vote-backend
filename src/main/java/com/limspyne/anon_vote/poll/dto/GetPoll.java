package com.limspyne.anon_vote.poll.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

public class GetPoll {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        private String id;
        private String text;
        private List<String> options;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String title;
        private List<QuestionDto> questions;
        private String categoryId;
        private Set<String> tags;
    }
}
