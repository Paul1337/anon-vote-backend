package com.limspyne.anon_vote.shared.application.telegram.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayDeque;
import java.util.Queue;

@Data
public class UserTelegramSession {
    private Long telegramId;

    private Queue<BotCommandData> commandQueue = new ArrayDeque<>();

    private boolean isAuthed = false;

    public boolean hasActiveCommand() {
        return !commandQueue.isEmpty();
    }

    @JsonIgnore
    public BotCommandData getActiveCommandData() {
        if (commandQueue.isEmpty()) return null;
        return commandQueue.peek();
    }

    public void clearCommandsQueue() {
        commandQueue.clear();
    }

    public void finishActiveCommand() {
        commandQueue.poll();
    }

    public void addCommand(BotCommandData botCommandData) {
        commandQueue.add(botCommandData);
    }

    private UserTelegramSession() {}

    public static UserTelegramSession empty(Long telegramId) {
        UserTelegramSession session = new UserTelegramSession();
        session.telegramId = telegramId;
        return session;
    }
}
