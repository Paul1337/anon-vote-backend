package com.limspyne.anon_vote.poll.repositories;

import com.limspyne.anon_vote.poll.entities.Poll;
import com.limspyne.anon_vote.poll.entities.PollCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    @Query(value = "SELECT * FROM poll WHERE title ILIKE %:title%", nativeQuery = true)
    List<Poll> findByTitleContaining(@Param("title") String title, Pageable pageable);
}
