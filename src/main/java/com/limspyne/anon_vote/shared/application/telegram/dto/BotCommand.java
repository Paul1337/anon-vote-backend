package com.limspyne.anon_vote.shared.application.telegram.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.limspyne.anon_vote.poll.application.services.botcommands.answerpoll.AnswerPollContext;
import com.limspyne.anon_vote.users.application.services.botcommands.auth.AuthCommandContext;
import lombok.Getter;

import java.util.List;

@Getter
public enum BotCommand {
    AUTH(new AuthCommandContext()),
    UNKNOWN_COMMAND(new StubCommandContext()),
    TO_MAIN_MENU(new StubCommandContext(), "/main_menu", "В главное меню"),
    ANSWER_POLL(new AnswerPollContext(), "/answer_poll", "📋 Пройти опрос", List.of("пройти", "опрос"));

    private final BotCommandContext initialContext;

    public BotCommandContext getInitialContext() {
        return initialContext.clone();
    }

    private final String command;

    private final List<String> triggers;

    private final String buttonText;

    BotCommand(BotCommandContext initialContext, String command, String buttonText, List<String> triggers) {
        this.initialContext = initialContext;
        this.command = command;
        this.triggers = triggers;
        this.buttonText = buttonText;
    }

    BotCommand(BotCommandContext initialContext, String command, String buttonText) {
        this(initialContext, command, buttonText, List.of());
    }

    BotCommand(BotCommandContext initialContext, String command) {
        this(initialContext, command, null, List.of());
    }

    BotCommand(BotCommandContext initialContext) {
        this(initialContext,null, null, List.of());
    }

    public boolean matches(String text) {
        if (command != null && text.toLowerCase().startsWith(command.toLowerCase())
                && command.equalsIgnoreCase(text)) return true;
        if (buttonText != null && buttonText.equalsIgnoreCase(text)) return true;
        return triggers.stream().anyMatch(trigger -> trigger.equalsIgnoreCase(text));
    }

}
