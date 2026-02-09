package com.limspyne.anon_vote.shared.inftrastrucure.repositories;

import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Управление сессиями телеграм пользователей.
 * Для каждого пользователя создаётся сессия в redis,
 * которая позволяет хранить очередь команд и контекст каждой команды
**/
@Repository
@RequiredArgsConstructor
public class UserTelegramSessionRepository {
    private static final Duration TTL = Duration.ofMinutes(15);

    private final RedisTemplate<String, UserTelegramSession> redis;

    public Optional<UserTelegramSession> get(Long telegramId) {
        return Optional.ofNullable(redis.opsForValue().get(key(telegramId)));
    }

    public UserTelegramSession getOrCreate(Long telegramId) {
        return get(telegramId)
                .orElse(UserTelegramSession.empty(telegramId));
    }

    public void save(UserTelegramSession session) {
        redis.opsForValue().set(
                key(session.getTelegramId()),
                session,
                TTL
        );
    }

    public void clear(Long telegramId) {
        redis.delete(key(telegramId));
    }

    private String key(Long telegramId) {
        return "telegram:session:" + telegramId;
    }

}
