package com.limspyne.anon_vote.shared.domain.services;

import com.limspyne.anon_vote.shared.inftrastrucure.email.dto.SimpleTextMail;
import jakarta.mail.MessagingException;

public interface TextMailSender {
    void sendSimpleTextMail(SimpleTextMail dto) throws MessagingException;
}
