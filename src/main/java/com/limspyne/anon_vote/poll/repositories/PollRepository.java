package com.limspyne.anon_vote.poll.repositories;

import com.limspyne.anon_vote.poll.entities.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
}
