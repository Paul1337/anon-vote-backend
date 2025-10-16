package com.limspyne.anon_vote.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleTextMail {
    private String to;

    private String subject;

    private String text;
}
