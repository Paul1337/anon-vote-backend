package com.limspyne.anon_vote.shared.inftrastrucure.telegram;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class TelegramKeyboardsProvider {
    public ReplyKeyboardMarkup mainMenu(List<BotCommand> globalCommands) {
        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboard(globalCommands.stream().map(botCommand -> new KeyboardRow(
                        List.of(
                                KeyboardButton.builder().text(botCommand.getButtonText()).build()
                        )
                )).toList())
                .build();
    }
}
