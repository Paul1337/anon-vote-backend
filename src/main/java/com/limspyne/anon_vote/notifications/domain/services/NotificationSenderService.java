package com.limspyne.anon_vote.notifications.domain.services;

import com.limspyne.anon_vote.shared.domain.services.TelegramMessageSender;
import com.limspyne.anon_vote.shared.domain.services.TextMailSender;
import com.limspyne.anon_vote.shared.inftrastrucure.email.dto.SimpleTextMail;
import com.limspyne.anon_vote.users.domain.services.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationSenderService implements NotificationSender {
    private final TextMailSender textMailSender;

    private final TelegramMessageSender telegramMessageSender;

    private UserService userService;

    @Override
    public void send(UUID userId, String text) {
        var user = userService.getUserById(userId);
        try {
            if (user.isTelegramConnected()) {
                telegramMessageSender.sendMessage(user.getTelegramId(), text);
            }
            var messageDto = new SimpleTextMail(user.getEmail(), "Anon Vote Notification", text);
            textMailSender.sendSimpleTextMail(messageDto);
        } catch (MessagingException | TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
