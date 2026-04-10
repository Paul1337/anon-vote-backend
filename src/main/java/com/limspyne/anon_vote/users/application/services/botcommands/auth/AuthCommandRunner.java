package com.limspyne.anon_vote.users.application.services.botcommands.auth;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import com.limspyne.anon_vote.shared.application.telegram.services.CommandRunner;
import com.limspyne.anon_vote.shared.application.telegram.dto.TelegramDto;
import com.limspyne.anon_vote.users.application.exceptions.CodeSendLimitException;
import com.limspyne.anon_vote.users.application.exceptions.CouldNotSendCodeException;
import com.limspyne.anon_vote.users.application.services.SendCodeService;
import com.limspyne.anon_vote.users.application.services.UserService;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthCommandRunner extends CommandRunner {
    private final UserService userService;

    private final SendCodeService sendCodeService;

    private final UserRepository userRepository;

    public static class Buttons {
        public static final TelegramDto.Response.InlineButton NEW_CODE = new TelegramDto.Response.InlineButton("Новый код", "btn_new_code");

        public static final TelegramDto.Response.InlineButton CHANGE_MAIL = new TelegramDto.Response.InlineButton("Изменить почту", "btn_change_mail");
    }

    private static final List<TelegramDto.Response.InlineButton> ACTION_BUTTONS = List.of(Buttons.NEW_CODE, Buttons.CHANGE_MAIL);

    @Override
    protected boolean canRun(BotCommand botCommand) {
        return botCommand == BotCommand.AUTH;
    }

    @Override
    @Transactional(noRollbackFor = { CodeSendLimitException.class })
    public TelegramDto.Response handleCommand(TelegramDto.Request request, BotCommandContext context) {
        if (!(context instanceof AuthCommandContext authCommandContext)) throw new RuntimeException("context expected to be of type AuthCommandContext");

        Long telegramId = request.getTelegramId();

        return switch (authCommandContext.getState()) {
            case NONE -> handleStart(authCommandContext, telegramId);
            case WAIT_EMAIL -> handleEmail(authCommandContext, telegramId, request.getText());
            case WAIT_CODE -> handleCode(authCommandContext, telegramId, request);
        };
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
            if (!Objects.equals(user.getTelegramId(), chatId)) {
                userService.deleteByTelegramId(chatId);
                userRepository.flush();
                user.setTelegramId(chatId);
            }
            userRepository.save(user);
            responseText = "Отлично, вижу вы уже регистрировались в нашем сервисе. Введите, пожалуйста, код подтверждения, отправленный на эту почту";
        } else {
            userService.deleteByTelegramId(chatId);
            userRepository.flush();
            userService.createUser(email, chatId);
            responseText = "Введите, пожалуйста, код подтверждения, отправленный на эту почту";
        }

        try {
            sendCodeService.sendCode(new SendCode.Request(email));
        } catch (CodeSendLimitException exception) {
            return TelegramDto.Response.forChat(chatId).text("Слишком частый запрос кода, попробуйте через минуту").build();
        } catch (CouldNotSendCodeException exception) {
            return TelegramDto.Response.forChat(chatId).text("Ошибка отправки сообщения, неверный адрес, введите ещё раз").build();
        }

        authCommandContext.setState(AuthCommandContext.RegistrationState.WAIT_CODE);
        return TelegramDto.Response.forChat(chatId).text(responseText).build();
    }

    private TelegramDto.Response handleCode(AuthCommandContext authCommandContext, Long chatId, TelegramDto.Request request) {
        var user = userService.getUserByTelegramId(chatId);

        if (Buttons.NEW_CODE.match(request.getText()))  {
            try {
                sendCodeService.sendCode(new SendCode.Request(user.getEmail()));
                return TelegramDto.Response.forChat(chatId).text("Ок, новый код был отправлен на вашу почту, введите его, пожалуйста").build();
            } catch (CodeSendLimitException exception) {
                return TelegramDto.Response.forChat(chatId).text("Слишком частый запрос кода, попробуйте через минуту").inlineButtons(ACTION_BUTTONS).build();
            } catch (CouldNotSendCodeException exception) {
                return TelegramDto.Response.forChat(chatId).text("Ошибка отправки сообщения, возможно неверный email").inlineButtons(ACTION_BUTTONS).build();
            }
        }

        if (Buttons.CHANGE_MAIL.match(request.getText())) {
            authCommandContext.setState(AuthCommandContext.RegistrationState.WAIT_EMAIL);
            return TelegramDto.Response.forChat(chatId).text("Ок, введите пожалуйста ваш новый email").build();
        }

        boolean confirmationSuccess = user.tryConfirmCodeValue(request.getText());
        if (confirmationSuccess) {
            user.setConfirmedTelegram(true);
            userRepository.save(user);
            authCommandContext.setFinished(true);
            return TelegramDto.Response.forChat(chatId).text("🎉 Поздравляю, вы успешно зарегистрированы!").withMenu().build();
        } else {
            return TelegramDto.Response.forChat(chatId).text("❌ Код неверный или истёк").inlineButtons(ACTION_BUTTONS).build();
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
