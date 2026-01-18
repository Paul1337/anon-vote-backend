package com.limspyne.anon_vote.users.instrastructure.repositories;

import com.limspyne.anon_vote.users.application.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @EntityGraph(value = "User.withActiveCodes", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findWithActiveCodesByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByTelegramId(long telegramId);

    Optional<User> findByTelegramId(long telegramId);
}
