package com.limspyne.anon_vote.poll.repositories;
import com.limspyne.anon_vote.poll.entities.Poll;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    @Query("SELECT p FROM Poll p WHERE p.id = :id")
    @EntityGraph(attributePaths = {"questions", "tags", "category"})
//    @EntityGraph(attributePaths = { "tags", "questions", "questions.options",  "category" })
    Optional<Poll> findById(@Param("id") UUID id);

    @Query("SELECT DISTINCT p FROM Poll p JOIN FETCH p.tags JOIN FETCH p.questions JOIN FETCH p.category WHERE p.title ILIKE %:title%")
    Page<Poll> findAllByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Poll p JOIN FETCH p.tags t WHERE t.name IN (:tags) AND title ILIKE %:title%")
    Page<Poll> findAllByTitleAndTags(@Param("title") String title, @Param("tags") Set<String> tags, Pageable pageable);
}
