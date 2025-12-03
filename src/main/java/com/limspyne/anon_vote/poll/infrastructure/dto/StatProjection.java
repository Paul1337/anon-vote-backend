package com.limspyne.anon_vote.poll.infrastructure.dto;

import java.time.Instant;

public interface StatProjection {
    String getAnswerText();
    Long getAnswerCount();
}
