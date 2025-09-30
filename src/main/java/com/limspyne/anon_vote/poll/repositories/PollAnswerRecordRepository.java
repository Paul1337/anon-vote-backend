package com.limspyne.anon_vote.poll.repositories;

import com.limspyne.anon_vote.poll.entities.Poll;
import com.limspyne.anon_vote.poll.entities.PollAnswerRecord;
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
public interface PollAnswerRecordRepository extends JpaRepository<PollAnswerRecord, UUID> {
}
