package com.limspyne.anon_vote.shared.domain.dto.telegram;

import lombok.Getter;

import java.util.List;

public enum BotCommand {
    SEARCH_POLLS("/search_polls", "🔍 Поиск опросов", List.of()),
    MY_POLLS("/my_polls", "📋 Мои опросы", List.of()),
    CREATE_POLL("/create_poll", "➕ Создать опрос", List.of());

    @Getter
    private final String command;

    @Getter
    private final List<String> triggers;

    @Getter
    private final String buttonText;

    BotCommand(String command, String buttonText, List<String> triggers) {
        this.command = command;
        this.triggers = triggers;
        this.buttonText = buttonText;
    }

    BotCommand(String command, String buttonText) {
        this(command, buttonText, List.of());
    }

    public boolean matches(String text) {
        if (command.equalsIgnoreCase(text)) return true;
        if (buttonText.equalsIgnoreCase(text)) return true;
        return triggers.stream().anyMatch(trigger -> trigger.equalsIgnoreCase(text));
    }

}
