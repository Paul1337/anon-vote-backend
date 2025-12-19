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
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    @EntityGraph(attributePaths = { "category" })
    Page<Poll> findAllByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);

    @Query("SELECT p FROM Poll p WHERE p.id = :id")
    @EntityGraph(attributePaths = { "tags", "category" })
    Optional<Poll> findById(@Param("id") UUID id);

    @Query("SELECT p FROM Poll p WHERE p.id = :id")
    @EntityGraph(attributePaths = { "questions" })
    Optional<Poll> findPollWithQuestionsById(@Param("id") UUID id);

    @Query("SELECT p FROM Poll p WHERE p.title ILIKE %:title% AND " +
            "(:categoryId IS NULL or p.category.path LIKE CONCAT( (SELECT cat.path FROM PollCategory cat WHERE cat.id = :categoryId), '%') )")
    Page<Poll> findAllByTitle(@Param("title") String title, @Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT DISTINCT p.id FROM Poll p JOIN p.tags t " +
            "WHERE t.name IN (:tags) " +
            "AND p.title ILIKE %:title% " +
            "AND (:categoryId IS NULL or p.category.path LIKE CONCAT((SELECT cat.path FROM PollCategory cat WHERE cat.id = :categoryId), '%'))")
    Page<UUID> findPollIdsByFilters(@Param("title") String title, @Param("categoryId") UUID categoryId, @Param("tags") Set<String> tags, Pageable pageable);

    @Query("SELECT p FROM Poll p WHERE p.id IN :pollIds")
    @EntityGraph(attributePaths = { "category" })
    List<Poll> findPollsWithTagsAndCategory(@Param("pollIds") List<UUID> pollIds);

    default Page<Poll> findAllByTitleAndTags(String title, UUID categoryId, Set<String> tags, Pageable pageable) {
        Page<UUID> pollIdsPage = findPollIdsByFilters(title, categoryId, tags, pageable);

        List<UUID> orderedPollIds = pollIdsPage.getContent();
        List<Poll> unorderedPolls = findPollsWithTagsAndCategory(orderedPollIds);

        Map<UUID, Poll> pollMap = unorderedPolls.stream()
                .collect(Collectors.toMap(Poll::getId, Function.identity()));

        List<Poll> orderedPolls = orderedPollIds.stream()
                .map(pollMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(orderedPolls, pageable, pollIdsPage.getTotalElements());
    }

    boolean existsByIdAndAttemptedUsersId(UUID pollId, UUID userId);

    @Query("SELECT p.id FROM Poll p JOIN p.attemptedUsers u WHERE u.id = :attemptedUserId AND p.id IN :pollIds")
    Set<UUID> findIdByAttemptedUsersIdAndIdIn(UUID attemptedUserId, Collection<UUID> pollIds);
}
