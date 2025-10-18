package com.limspyne.anon_vote.users.domain.services;

import com.limspyne.anon_vote.email.dto.SimpleTextMail;
import com.limspyne.anon_vote.email.services.TextMailSender;
import com.limspyne.anon_vote.users.web.dto.SendCode;
import com.limspyne.anon_vote.users.domain.entities.User;
import com.limspyne.anon_vote.users.domain.entities.UserActiveCode;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class SendCodeService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TextMailSender mailService;

    @Autowired
    private UserService userService;

    @Transactional
    public void sendCode(SendCode.Request request) {
        String generatedCode = generateCode();

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        var user = userOptional.orElseGet(() -> userService.createUserByEmail(request.getEmail()));

        var code = new UserActiveCode(generatedCode);
        user.addActiveCode(code);
        userRepository.save(user);

        mailService.sendSimpleTextMail(new SimpleTextMail(request.getEmail(), "Подтверждение почты на AnonVote", "Ваш код подтверждения почты: %s".formatted(code.getValue())));
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
