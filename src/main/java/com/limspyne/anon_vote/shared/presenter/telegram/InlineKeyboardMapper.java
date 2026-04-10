package com.limspyne.anon_vote.shared.presenter.telegram;

import com.limspyne.anon_vote.shared.application.telegram.dto.TelegramDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class InlineKeyboardMapper {

    public static InlineKeyboardMarkup from(List<TelegramDto.Response.InlineButton> buttons) {
        if (buttons == null || buttons.isEmpty()) {
            return null;
        }

        List<List<InlineKeyboardButton>> keyboard =
            buttons.stream().map(button ->
                    List.of(
                            InlineKeyboardButton.builder()
                                    .text(button.getName())
                                    .callbackData(button.getCallbackData())
                                    .build()
                    )).toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}
