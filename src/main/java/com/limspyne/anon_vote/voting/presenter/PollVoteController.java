package com.limspyne.anon_vote.voting.presenter;

import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import com.limspyne.anon_vote.voting.application.PollVoteService;
import com.limspyne.anon_vote.voting.entities.PollVote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/polls")
@RequiredArgsConstructor
public class PollVoteController {
    private final PollVoteService pollVoteService;

    @PostMapping("/{id}/votes/up")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> upVotePoll(@PathVariable String id, @AuthenticationPrincipal AppUserDetails userDetails) {
        pollVoteService.updateVoteType(UUID.fromString(id), userDetails, PollVote.VoteType.Up);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/votes/down")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> downVotePoll(@PathVariable String id, @AuthenticationPrincipal AppUserDetails userDetails) {
        pollVoteService.updateVoteType(UUID.fromString(id), userDetails, PollVote.VoteType.Down);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/votes/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelVote(@PathVariable String id, @AuthenticationPrincipal AppUserDetails userDetails) {
        pollVoteService.updateVoteType(UUID.fromString(id), userDetails, PollVote.VoteType.None);
        return ResponseEntity.noContent().build();
    }
}
