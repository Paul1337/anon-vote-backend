package com.limspyne.anon_vote.users.domain.services;

import com.limspyne.anon_vote.shared.inftrastrucure.email.dto.HtmlMail;
import com.limspyne.anon_vote.shared.inftrastrucure.email.services.HtmlMailSender;
import com.limspyne.anon_vote.users.domain.exceptions.CouldNotSendCodeException;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.domain.entities.User;
import com.limspyne.anon_vote.users.domain.entities.UserActiveCode;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SendCodeService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HtmlMailSender mailService;

    @Autowired
    private UserService userService;

    private String CONFIRM_EMAIL_TEMPLATE = "templates/emails/confirm-email.html";

    @Transactional
    public void sendCode(SendCode.Request request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        var user = userOptional.orElseGet(() -> userService.createUserByEmail(request.getEmail()));
        var code = new UserActiveCode(generateCode());
        user.addActiveCode(code);
        Hibernate.initialize(user.getActiveCodes());
        userRepository.save(user);

        try {
            String description = "Мы не привязываем вашу почту к вашим ответам, храним анонимность. Почта нужна только для: <p style='font-weight: 600; color: black;'>- гарантии уникальных ответов</p> <p style='font-weight: 600; color: black;'>- авторства созданных опросов (чтобы вы могли посмотреть список своих опросов)</p>";

            String htmlContent = loadTemplate(CONFIRM_EMAIL_TEMPLATE, description, code.getValue());
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

    private static String loadTemplate(String path, String... placeholders) throws IOException {
        try (InputStream inputStream = SendCodeService.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Шаблон не найден: " + path);
            }
            String template = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            return String.format(template, placeholders);
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
