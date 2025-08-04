package com.limspyne.anon_vote.poll.repositories;

import com.limspyne.anon_vote.poll.entities.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {
}
