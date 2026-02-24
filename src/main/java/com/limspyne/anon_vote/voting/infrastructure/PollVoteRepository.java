package com.limspyne.anon_vote.voting.infrastructure;
import com.limspyne.anon_vote.voting.entities.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PollVoteRepository extends JpaRepository<PollVote, UUID> {
    Optional<PollVote> findByAuthorIdAndPollId(UUID authorId, UUID pollId);
}
