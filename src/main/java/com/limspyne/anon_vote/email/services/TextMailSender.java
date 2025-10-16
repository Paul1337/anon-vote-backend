package com.limspyne.anon_vote.email.services;

import com.limspyne.anon_vote.email.dto.SimpleTextMail;

public interface TextMailSender {
    void sendSimpleTextMail(SimpleTextMail dto);
}
