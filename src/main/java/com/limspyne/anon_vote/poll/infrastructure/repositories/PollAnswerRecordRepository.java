package com.limspyne.anon_vote.poll.infrastructure.repositories;

import com.limspyne.anon_vote.poll.domain.entities.PollAnswerRecord;
import com.limspyne.anon_vote.poll.infrastructure.dto.AnswerStatProjection;
import com.limspyne.anon_vote.poll.infrastructure.dto.StatProjection;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PollAnswerRecordRepository extends JpaRepository<PollAnswerRecord, UUID> {
//    boolean existsByPollIdAndFingerprint(UUID pollId, String fingerprint);

    Set<PollAnswerRecord> findAllByPollId(UUID pollId);

    @Query("""
        SELECT
            DATE(record.createdAt) as date,
            ans.question.id as questionId,
            ans.answer as answerText,
            COUNT(ans.id) as answerCount
        FROM PollAnswerRecord record
        JOIN record.answers ans
        WHERE record.createdAt BETWEEN :startDate AND :endDate
        AND record.poll.id = :pollId
        GROUP BY DATE(record.createdAt), ans.question.id, ans.answer
        ORDER BY date ASC
        """)
    List<AnswerStatProjection> getAnswerStatsByDateRangeGroupedByDays(
            @Param("pollId") UUID pollId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT
            ans.question.id as questionId,
            ans.answer as answerText,
            COUNT(ans.id) as answerCount
        FROM PollAnswerRecord record
        JOIN record.answers ans
        WHERE record.createdAt < :date AND record.poll.id = :pollId
        GROUP BY ans.question.id, ans.answer
        """)
    List<StatProjection> getAnswerStatsUpToInstant(@Param("pollId") UUID pollId, @Param("date") LocalDateTime date);
}
