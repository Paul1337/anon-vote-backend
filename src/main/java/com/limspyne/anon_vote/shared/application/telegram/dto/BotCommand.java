package com.limspyne.anon_vote.shared.application.telegram.dto;

import lombok.Getter;

import java.util.List;

@Getter
public enum BotCommand {
    AUTH,
    UNKNOWN_COMMAND,
    SEARCH_POLLS("/search_polls", "🔍 Поиск опросов", List.of()),
    MY_POLLS("/my_polls", "📋 Мои опросы", List.of()),
    CREATE_POLL("/create_poll", "➕ Создать опрос", List.of());

    private final String command;

    private final List<String> triggers;

    private final String buttonText;

    BotCommand(String command, String buttonText, List<String> triggers) {
        this.command = command;
        this.triggers = triggers;
        this.buttonText = buttonText;
    }

    BotCommand(String command, String buttonText) {
        this(command, buttonText, List.of());
    }

    BotCommand() {
        this(null, null, List.of());
    }

    public boolean matches(String text) {
        if (command != null && command.equalsIgnoreCase(text)) return true;
        if (buttonText != null && buttonText.equalsIgnoreCase(text)) return true;
        return triggers.stream().anyMatch(trigger -> trigger.equalsIgnoreCase(text));
    }

}
