package com.limspyne.anon_vote.shared.inftrastrucure.email.services;

import com.limspyne.anon_vote.shared.application.mail.services.HtmlMailSender;
import com.limspyne.anon_vote.shared.application.mail.services.TextMailSender;
import com.limspyne.anon_vote.shared.inftrastrucure.email.dto.HtmlMail;
import com.limspyne.anon_vote.shared.inftrastrucure.email.dto.SimpleTextMail;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class AppMailSender implements TextMailSender, HtmlMailSender {
    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.from}")
    private String emailFromAddress;

    private Properties properties = new Properties();

    private Authenticator mailAuthenticator;

    @PostConstruct
    void setUp() {
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        mailAuthenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }

    @Override
    @Async
    public void sendSimpleTextMail(SimpleTextMail dto) throws MessagingException {
        Session session = Session.getInstance(properties, mailAuthenticator);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getTo()));
        message.setSubject(dto.getSubject());
        message.setText(dto.getText());
        Transport.send(message);
    }

    @Override
    @Async
    public void sendHtmlMail(HtmlMail dto) throws MessagingException {
        Session session = Session.getInstance(properties, mailAuthenticator);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getTo()));
        message.setSubject(dto.getSubject());

        Multipart multipart = new MimeMultipart("alternative");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(dto.getText(), "utf-8");
        multipart.addBodyPart(textPart);

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(dto.getHtml(), "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
