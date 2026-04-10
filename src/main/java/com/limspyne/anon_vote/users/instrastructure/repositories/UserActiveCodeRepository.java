package com.limspyne.anon_vote.users.instrastructure.repositories;

import com.limspyne.anon_vote.users.application.entities.UserActiveCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserActiveCodeRepository extends JpaRepository<UserActiveCode, UUID> {
}
