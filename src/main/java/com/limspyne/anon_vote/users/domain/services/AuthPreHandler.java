package com.limspyne.anon_vote.users.domain.services;

import com.limspyne.anon_vote.shared.domain.services.TelegramPreHandler;
import com.limspyne.anon_vote.shared.inftrastrucure.telegram.TelegramKeyboards;
import com.limspyne.anon_vote.users.domain.exceptions.CodeSendLimitException;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

enum RegistrationState {
    NONE,
    WAIT_EMAIL,
    WAIT_CODE
}

@Service
@RequiredArgsConstructor
public class AuthPreHandler extends TelegramPreHandler {
    private final UserService userService;

    private final SendCodeService sendCodeService;

    private final UserRepository userRepository;

    private final Map<Long, RegistrationState> registrationStateMap = new HashMap<>();

    @Override
    @Transactional
    public boolean handle(Update update, DefaultAbsSender sender) {
        Long chatId = update.getMessage().getChatId();

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return true;
        }

        String text = update.getMessage().getText();

        var user = userRepository.findByTelegramId(chatId);

        if (user.isPresent()) {
            if (user.get().isConfirmedTelegram()) {
                return false;
            } else {
                registrationStateMap.put(chatId, RegistrationState.WAIT_CODE);
            }
        }

        switch (registrationStateMap.getOrDefault(chatId, RegistrationState.NONE)) {
            case NONE -> handleStart(sender, chatId);
            case WAIT_EMAIL -> handleEmail(sender, chatId, text);
            case WAIT_CODE -> handleCode(sender, chatId, text);
        }

        return true;
    }

    private void handleStart(DefaultAbsSender sender, Long chatId) {
        registrationStateMap.put(chatId, RegistrationState.WAIT_EMAIL);
        sendMessage(sender, chatId, "Привет! Для регистрации введите, пожалуйста, ваш email");
    }

    private void handleEmail(DefaultAbsSender sender, Long chatId, String email) {
        if (!isValidEmail(email)) {
            sendMessage(sender, chatId, "Пожалуйста, введите корректный email");
            return;
        }

        var userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            user.setTelegramId(chatId);
            userRepository.save(user);
            sendMessage(sender, chatId, "Отлично, вижу вы уже регистрировались в нашем сервисе. Введите, пожалуйста, код подтверждения, отправленный на почту");
        } else {
            userService.createUser(email, chatId);
            sendMessage(sender, chatId, "Введите, пожалуйста, код подтверждения, отправленный на почту");
        }

        sendCodeService.sendCode(new SendCode.Request(email));
        registrationStateMap.put(chatId, RegistrationState.WAIT_CODE);
    }

    private void handleCode(DefaultAbsSender sender, Long chatId, String code) {
        var user = userService.getUserByTelegramId(chatId);
        boolean confirmationSuccess = user.tryConfirmCodeValue(code);
        if (confirmationSuccess) {
            user.setConfirmedTelegram(true);
            userRepository.save(user);

            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("🎉 Поздравляю, вы успешно зарегистрированы!")
                    .replyMarkup(TelegramKeyboards.mainMenu())
                    .build();

            sendMessage(sender, message);
        } else {
            try {
                sendCodeService.sendCode(new SendCode.Request(user.getEmail()));
                sendMessage(sender, chatId, "❌ Код неверный или истёк, на почту сейчас должен прийти новый код, введи его, пожалуйста");
            } catch (CodeSendLimitException exception) {
                sendMessage(sender, chatId, "❌ Код неверный, попробуйте ещё раз");
            }
        }
    }


    private boolean isValidEmail(String email) {
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
