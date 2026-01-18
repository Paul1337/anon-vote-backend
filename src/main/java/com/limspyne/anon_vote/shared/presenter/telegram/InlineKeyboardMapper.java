package com.limspyne.anon_vote.shared.presenter.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;

@Component
public class InlineKeyboardMapper {

    public static InlineKeyboardMarkup from(String[] buttons) {
        if (buttons == null || buttons.length == 0) {
            return null;
        }

        List<List<InlineKeyboardButton>> keyboard = Arrays.stream(buttons)
                .map(buttonText ->
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(buttonText)
                                        .callbackData(buttonText)
                                        .build()
                        )
                )
                .toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}
