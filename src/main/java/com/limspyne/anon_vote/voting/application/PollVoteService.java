package com.limspyne.anon_vote.voting.application;

import com.limspyne.anon_vote.poll.application.services.query.PollQueryService;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.users.application.services.UserService;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import com.limspyne.anon_vote.voting.entities.PollVote;
import com.limspyne.anon_vote.voting.infrastructure.PollVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollVoteService {
    private final PollRepository pollRepository;

    private final PollVoteRepository pollVoteRepository;

    private final PollQueryService pollQueryService;

    private final UserService userService;

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class, DataIntegrityViolationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updateVoteType(UUID pollId, AppUserDetails userDetails, PollVote.VoteType voteType) {
        makeVote(userDetails.getId(), pollId, voteType);
    }

    private void makeVote(UUID authorId, UUID pollId, PollVote.VoteType voteType) {
        var poll = pollQueryService.getPollByIdWithoutRelations(pollId);
        var author = userService.getUserById(authorId);

        var userVote = pollVoteRepository.findByAuthorIdAndPollId(authorId, pollId)
                .orElseGet(() -> PollVote.ofDefault(author, poll));

        if (userVote.getVoteType() == voteType) {
            // повторный голос, просто игнорируем, метод успешен, но ничего не делает
            return;
        }

        poll.cancelVote(userVote);
        userVote.updateType(voteType);
        poll.applyVote(userVote);

        pollRepository.save(poll);
        pollVoteRepository.save(userVote);
    }
}
