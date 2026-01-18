package com.limspyne.anon_vote.shared.inftrastrucure.telegram;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public final class TelegramKeyboards {
    private TelegramKeyboards() {}

    public static ReplyKeyboardMarkup mainMenu() {
        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboard(List.of(
                        new KeyboardRow(
                                List.of(
                                        KeyboardButton.builder().text(BotCommand.SEARCH_POLLS.getButtonText()).build()
                                )
                        ),
                        new KeyboardRow(
                                List.of(
                                        KeyboardButton.builder().text(BotCommand.MY_POLLS.getButtonText()).build()
                                )
                        ),
                        new KeyboardRow(
                                List.of(
                                        KeyboardButton.builder().text(BotCommand.CREATE_POLL.getButtonText()).build()
                                )
                        )
                ))
                .build();
    }
}
