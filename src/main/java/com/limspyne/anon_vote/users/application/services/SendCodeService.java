package com.limspyne.anon_vote.users.application.services;

import com.limspyne.anon_vote.shared.inftrastrucure.email.dto.HtmlMail;
import com.limspyne.anon_vote.shared.application.mail.services.HtmlMailSender;
import com.limspyne.anon_vote.shared.inftrastrucure.util.TemplateLoader;
import com.limspyne.anon_vote.users.application.exceptions.CodeSendLimitException;
import com.limspyne.anon_vote.users.application.exceptions.CouldNotSendCodeException;
import com.limspyne.anon_vote.users.application.exceptions.UserNotFoundException;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.application.entities.UserActiveCode;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Service
public class SendCodeService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HtmlMailSender mailService;

    @Autowired
    private UserService userService;

    private static final String CONFIRM_EMAIL_TEMPLATE = "templates/emails/confirm-email.html";

    @Transactional(noRollbackFor = { CodeSendLimitException.class })
    public void sendCode(SendCode.Request request) {
        User user = userRepository.findWithActiveCodesByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);
        var code = new UserActiveCode(generateCode());

        if (!user.canRequestNewCode()) {
            throw new CodeSendLimitException(request.getEmail());
        }

        user.addActiveCode(code);
        userRepository.save(user);

        try {
            String description = "Мы не привязываем вашу почту к вашим ответам, храним анонимность. Почта нужна только для: <p style='font-weight: 600; color: black;'>- гарантии уникальных ответов</p> <p style='font-weight: 600; color: black;'>- авторства созданных опросов (чтобы вы могли посмотреть список своих опросов)</p>";

            String htmlContent = TemplateLoader.loadTemplate(CONFIRM_EMAIL_TEMPLATE, description, code.getValue());
            String textContent = String.format(
                    "Подтверждение почты для anon-vote\n\n%s\n\nВаш код подтверждения: %s\n\nВведите этот код в приложении.",
                    description,
                    code.getValue()
            );
            mailService.sendHtmlMail(new HtmlMail(request.getEmail(), "Подтверждение почты на AnonVote", textContent, htmlContent));
        } catch (MessagingException exp) {
            throw new CouldNotSendCodeException(request.getEmail());
        } catch (IOException exp) {
            throw new RuntimeException("Could not find email template");
        }
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
