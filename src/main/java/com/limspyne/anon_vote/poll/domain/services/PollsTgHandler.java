package com.limspyne.anon_vote.poll.domain.services;

import com.limspyne.anon_vote.shared.domain.services.TelegramPreHandler;
import com.limspyne.anon_vote.shared.domain.services.TelegramUpdateHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class PollsTgHandler extends TelegramUpdateHandler {
    @Override
    public boolean canHandle(Update update) {
        return false;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) {

    }
}
