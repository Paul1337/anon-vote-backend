package com.limspyne.anon_vote.shared.inftrastrucure.telegram;

import com.limspyne.anon_vote.shared.presenter.telegram.InlineKeyboardMapper;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramSender {
    private final DefaultAbsSender sender;

    public TelegramSender(DefaultAbsSender sender) {
        this.sender = sender;
    }

    public void send(TelegramDto.Response response) {
        try {
            InlineKeyboardMarkup keyboard =
                    InlineKeyboardMapper.from(response.getInlineButtons());

            SendMessage message = SendMessage.builder()
                    .chatId(response.getTelegramId().toString())
                    .text(response.getText())
                    .replyMarkup(response.isShowMenu() ? TelegramKeyboards.mainMenu() : keyboard)
                    .build();

            sender.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
