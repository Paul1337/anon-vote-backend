package com.limspyne.anon_vote.poll.domain.services.telegram.commands;

import com.limspyne.anon_vote.shared.domain.dto.telegram.BotCommand;
import com.limspyne.anon_vote.shared.domain.services.TelegramUpdateCommandHandler;
import com.limspyne.anon_vote.shared.inftrastrucure.repositories.UserTelegramSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class SearchPollsTgCommand extends TelegramUpdateCommandHandler {
    private final UserTelegramSessionRepository telegramSessionRepository;

    public SearchPollsTgCommand(UserTelegramSessionRepository telegramSessionRepository) {
        super(BotCommand.SEARCH_POLLS);

        this.telegramSessionRepository = telegramSessionRepository;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) {

        var telegramId = update.getMessage().getChatId();
        var session = telegramSessionRepository.getOrCreate(telegramId);

//        switch (session.) {
//
//        }

    }
}
