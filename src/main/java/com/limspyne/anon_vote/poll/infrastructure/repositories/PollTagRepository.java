package com.limspyne.anon_vote.poll.infrastructure.repositories;

import com.limspyne.anon_vote.poll.application.entities.PollTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PollTagRepository extends JpaRepository<PollTag, UUID> {
    List<PollTag> findAllByName(String name);

    List<PollTag> findAllByNameIn(Iterable<String> names);
}
