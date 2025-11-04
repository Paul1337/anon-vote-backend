package com.limspyne.anon_vote.poll.infrastructure.repositories;
import com.limspyne.anon_vote.poll.domain.entities.Poll;
import com.limspyne.anon_vote.users.domain.entities.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    @EntityGraph(attributePaths = {"tags", "category"})
    Page<Poll> findAllByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);

    @Query("SELECT p FROM Poll p WHERE p.id = :id")
    @EntityGraph(attributePaths = {"tags", "category"})
    Optional<Poll> findById(@Param("id") UUID id);

    @Query("SELECT DISTINCT p FROM Poll p JOIN FETCH p.tags JOIN FETCH p.category WHERE p.title ILIKE %:title% AND (:categoryId IS NULL or p.category.id = :categoryId)")
    Page<Poll> findAllByTitle(@Param("title") String title, @Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT DISTINCT p.id FROM Poll p JOIN p.tags t " +
            "WHERE t.name IN (:tags) " +
            "AND p.title ILIKE %:title% " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<UUID> findPollIdsByFilters(@Param("title") String title, @Param("categoryId") UUID categoryId, @Param("tags") Set<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Poll p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.category " +
            "WHERE p.id IN :pollIds")
    List<Poll> findPollsWithTagsAndCategory(@Param("pollIds") List<UUID> pollIds);

    default Page<Poll> findAllByTitleAndTags(String title, UUID categoryId, Set<String> tags, Pageable pageable) {
        Page<UUID> pollIds = findPollIdsByFilters(title, categoryId, tags, pageable);
        List<Poll> polls = findPollsWithTagsAndCategory(pollIds.getContent());
        return new PageImpl<>(polls, pageable, pollIds.getTotalElements());
    }

    boolean existsByIdAndAttemptedUsersId(UUID pollId, UUID userId);

    @Query("SELECT p.id FROM Poll p JOIN p.attemptedUsers u WHERE u.id = :attemptedUserId AND p.id IN :pollIds")
    Set<UUID> findIdByAttemptedUsersIdAndIdIn(UUID attemptedUserId, Collection<UUID> pollIds);
}
