package com.limspyne.anon_vote.poll.infrastructure.dto;

import java.time.Instant;
import java.util.UUID;

public interface StatProjection {
    UUID getQuestionId();
    String getAnswerText();
    Long getAnswerCount();
}
