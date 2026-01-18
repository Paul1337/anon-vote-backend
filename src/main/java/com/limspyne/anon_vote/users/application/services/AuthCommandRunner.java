package com.limspyne.anon_vote.users.application.services;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import com.limspyne.anon_vote.shared.application.telegram.services.CommandRunner;
import com.limspyne.anon_vote.shared.inftrastrucure.repositories.UserTelegramSessionRepository;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import com.limspyne.anon_vote.users.application.exceptions.CodeSendLimitException;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandRunner extends CommandRunner {
    private final UserService userService;

    private final SendCodeService sendCodeService;

    private final UserRepository userRepository;

    private final UserTelegramSessionRepository userTelegramSessionRepository;

    private String newCodeButtonText = "Отправить новый код";
    private String changeMailButtonText = "Изменить почту";

    private String[] actionButtonTexts = { newCodeButtonText, changeMailButtonText };

    @Override
    protected boolean canRun(BotCommand botCommand) {
        return botCommand == BotCommand.AUTH;
    }

    @Override
    @Transactional(noRollbackFor = { CodeSendLimitException.class })
    public TelegramDto.Response handleCommand(TelegramDto.Request request, UserTelegramSession session) {
        if (session.getContext() == null) session.setContext(new AuthCommandContext());
        if (!(session.getContext() instanceof AuthCommandContext authCommandContext)) throw new RuntimeException("context expected to be of type AuthCommandContext");

        Long telegramId = request.getTelegramId();

        TelegramDto.Response response = switch (authCommandContext.getState()) {
            case NONE -> handleStart(authCommandContext, telegramId);
            case WAIT_EMAIL -> handleEmail(authCommandContext, telegramId, request.getText());
            case WAIT_CODE -> handleCode(authCommandContext, telegramId, request);
        };

        if (authCommandContext.getState() == null) {
            userTelegramSessionRepository.clear(telegramId);
        } else {
            userTelegramSessionRepository.save(session);
        }

        return response;
    }

    private TelegramDto.Response handleStart(AuthCommandContext authCommandContext, Long chatId) {
        authCommandContext.setState(AuthCommandContext.RegistrationState.WAIT_EMAIL);
        return TelegramDto.Response.forChat(chatId).text("Привет! Для регистрации введите, пожалуйста, ваш email").build();
    }

    private TelegramDto.Response handleEmail(AuthCommandContext authCommandContext, Long chatId, String email) {
        if (!isValidEmail(email)) {
            return TelegramDto.Response.forChat(chatId).text("Пожалуйста, введите корректный email").build();
        }

        var userOptional = userRepository.findByEmail(email);
        String responseText = "";
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            user.setTelegramId(chatId);
            userRepository.save(user);
            responseText = "Отлично, вижу вы уже регистрировались в нашем сервисе. Введите, пожалуйста, код подтверждения, отправленный на эту почту";
        } else {
            userService.createUser(email, chatId);
            responseText = "Введите, пожалуйста, код подтверждения, отправленный на эту почту";
        }

        try {
            sendCodeService.sendCode(new SendCode.Request(email));
        } catch (CodeSendLimitException exception) {
            return TelegramDto.Response.forChat(chatId).text("Слишком частый запрос кода, попробуйте через минуту").inlineButtons(actionButtonTexts).build();
        }

        authCommandContext.setState(AuthCommandContext.RegistrationState.WAIT_CODE);

        return TelegramDto.Response.forChat(chatId).text(responseText).build();
    }

    private TelegramDto.Response handleCode(AuthCommandContext authCommandContext, Long chatId, TelegramDto.Request request) {
        var user = userService.getUserByTelegramId(chatId);



        if (request.getText().equalsIgnoreCase(newCodeButtonText)) {
            try {
                sendCodeService.sendCode(new SendCode.Request(user.getEmail()));
                return TelegramDto.Response.forChat(chatId).text("Ок, новый код был отправлен на вашу почту, введите его, пожалуйста").build();
            } catch (CodeSendLimitException exception) {
                return TelegramDto.Response.forChat(chatId).text("Слишком частый запрос кода, попробуйте через минуту").inlineButtons(actionButtonTexts).build();
            }
        }

        if (request.getText().equalsIgnoreCase(changeMailButtonText)) {
            authCommandContext.setState(AuthCommandContext.RegistrationState.WAIT_EMAIL);
            return TelegramDto.Response.forChat(chatId).text("Ок, введите пожалуйста ваш новый email").build();
        }

        boolean confirmationSuccess = user.tryConfirmCodeValue(request.getText());
        if (confirmationSuccess) {
            user.setConfirmedTelegram(true);
            userRepository.save(user);
            authCommandContext.setState(null);
            return TelegramDto.Response.forChat(chatId).text("🎉 Поздравляю, вы успешно зарегистрированы!").withMenu().build();
        } else {
            return TelegramDto.Response.forChat(chatId).text("❌ Код неверный или истёк").inlineButtons(actionButtonTexts).build();
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
