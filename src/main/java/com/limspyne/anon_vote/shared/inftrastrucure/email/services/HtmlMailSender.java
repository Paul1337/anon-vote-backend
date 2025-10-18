package com.limspyne.anon_vote.shared.inftrastrucure.email.services;


import com.limspyne.anon_vote.shared.inftrastrucure.email.dto.HtmlMail;
import jakarta.mail.MessagingException;

public interface HtmlMailSender {
    void sendHtmlMail(HtmlMail dto) throws MessagingException;
}
