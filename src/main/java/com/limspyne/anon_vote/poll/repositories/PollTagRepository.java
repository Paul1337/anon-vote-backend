package com.limspyne.anon_vote.poll.repositories;

import com.limspyne.anon_vote.poll.entities.PollTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PollTagRepository extends JpaRepository<PollTag, UUID> {
    List<PollTag> findAllByName(String name);

    List<PollTag> findAllByNameIn(Iterable<String> names);
}
