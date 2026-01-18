package com.limspyne.anon_vote.shared.application.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTelegramSession {
    private Long telegramId;

    private BotCommand activeCommand;

    private BotCommandContext context;

    public static UserTelegramSession empty(Long telegramId) {
        UserTelegramSession session = new UserTelegramSession();
        session.telegramId = telegramId;
        session.activeCommand = null;
        session.context = null;
        return session;
    }

    public static UserTelegramSession emptyForCommand(Long telegramId, BotCommand botCommand) {
        UserTelegramSession session = new UserTelegramSession();
        session.telegramId = telegramId;
        session.activeCommand = botCommand;
        session.context = null;
        return session;
    }
}
