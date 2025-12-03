package com.limspyne.anon_vote.poll.infrastructure.dto;

import java.sql.Date;
import java.time.LocalDate;

public interface AnswerStatProjection {
    LocalDate getDate();
    String getAnswerText();
    Long getAnswerCount();
}
