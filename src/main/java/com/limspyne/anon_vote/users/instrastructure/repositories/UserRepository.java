package com.limspyne.anon_vote.users.instrastructure.repositories;

import com.limspyne.anon_vote.users.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByTelegramId(long telegramId);

    Optional<User> findByTelegramId(long telegramId);
}
