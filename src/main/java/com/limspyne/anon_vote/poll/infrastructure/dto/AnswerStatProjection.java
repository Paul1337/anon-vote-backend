package com.limspyne.anon_vote.poll.infrastructure.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public interface AnswerStatProjection {
    LocalDate getDate();
    UUID getQuestionId();
    String getAnswerText();
    Long getAnswerCount();
}
