package com.limspyne.anon_vote.users.application.services;

import com.limspyne.anon_vote.users.application.exceptions.UserNotFoundException;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;

    public boolean isAuthedByTelegramId(long telegramId) {
        var user = userRepository.findByTelegramId(telegramId);
        return user.isPresent() && user.get().isConfirmedTelegram();
    }

    public boolean startedTelegramAuth(long telegramId) {
        var user = userRepository.findByTelegramId(telegramId);
        return user.isPresent() && !user.get().isConfirmedTelegram();
    }
}
