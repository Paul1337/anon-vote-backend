package com.limspyne.anon_vote.poll.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.UUID;

public class SubmitPoll {
    @Data
    @NoArgsConstructor
    @Schema
    public static class Request {
        private Map<UUID, String> answers;
    }
}
