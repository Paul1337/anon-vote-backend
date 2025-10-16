package com.limspyne.anon_vote.email.services;

import com.limspyne.anon_vote.email.dto.SimpleTextMail;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class MailService implements TextMailSender {
    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    private String EMAIL_FROM = "anon-vote@gmail.com";

    private Properties properties = new Properties();

    @PostConstruct
    void setUp() {
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
    }

    public void sendSimpleTextMail(SimpleTextMail dto) {
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getTo()));
            message.setSubject(dto.getSubject());
            message.setText(dto.getText());
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
