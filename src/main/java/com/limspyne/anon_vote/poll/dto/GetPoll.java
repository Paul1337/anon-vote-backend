package com.limspyne.anon_vote.poll.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class GetPoll {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String title;
        private List<CreatePoll.QuestionDto> questions;
        private String categoryId;
    }
}
