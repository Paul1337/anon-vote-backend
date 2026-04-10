package com.limspyne.anon_vote.shared.inftrastrucure.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HtmlMail {
    private String to;

    private String subject;

    private String text;

    private String html;
}
