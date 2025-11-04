package com.limspyne.anon_vote.poll.infrastructure.repositories;

import com.limspyne.anon_vote.poll.domain.entities.PollAnswerRecord;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PollAnswerRecordRepository extends JpaRepository<PollAnswerRecord, UUID> {
    boolean existsByPollIdAndFingerprint(UUID pollId, String fingerprint);

    Set<PollAnswerRecord> findAllByPollId(UUID pollId);

}
