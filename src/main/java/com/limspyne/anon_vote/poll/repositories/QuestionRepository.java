package com.limspyne.anon_vote.poll.repositories;

import com.limspyne.anon_vote.poll.entities.PollAnswerRecord;
import com.limspyne.anon_vote.poll.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
