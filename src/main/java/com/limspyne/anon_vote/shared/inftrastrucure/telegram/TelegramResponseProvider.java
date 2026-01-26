package com.limspyne.anon_vote.shared.inftrastrucure.telegram;

import com.limspyne.anon_vote.shared.application.telegram.services.BotCommandRegistry;
import com.limspyne.anon_vote.shared.presenter.telegram.InlineKeyboardMapper;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramResponseProvider {
    private final TelegramKeyboardsProvider telegramKeyboardsProvider;

    private final BotCommandRegistry botCommandRegistry;

    public SendMessage getResponseMessage(TelegramDto.Response response) {
        InlineKeyboardMarkup keyboard = InlineKeyboardMapper.from(response.getInlineButtons());

        return SendMessage.builder()
                .chatId(response.getTelegramId().toString())
                .text(response.getText())
                .replyMarkup(response.isShowMenu() ? telegramKeyboardsProvider.mainMenu(
                        botCommandRegistry.globalCommands()
                ) : keyboard)
                .build();
    }
}
