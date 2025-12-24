package com.limspyne.anon_vote.shared.domain.dto.telegram;

import lombok.Data;

@Data
public class UserTelegramSession {
    private Long telegramId;

    private BotCommand activeCommand;

    private BotCommandContext context;

    public static UserTelegramSession empty(Long telegramId) {
        UserTelegramSession session = new UserTelegramSession();
        session.telegramId = telegramId;
        session.activeCommand = null;
        return session;
    }
}
