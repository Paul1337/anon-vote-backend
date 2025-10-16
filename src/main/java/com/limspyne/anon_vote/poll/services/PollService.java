package com.limspyne.anon_vote.poll.services;

import com.limspyne.anon_vote.poll.dto.GetPoll;
import com.limspyne.anon_vote.poll.entities.Poll;
import com.limspyne.anon_vote.poll.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.mappers.PollMapper;
import com.limspyne.anon_vote.poll.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.repositories.PollRepository;
import com.limspyne.anon_vote.users.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PollService {

    @Autowired
    private PollMapper pollMapper;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollAnswerRecordRepository pollAnswerRecordRepository;

    public GetPoll.Response getPollForAnonymousUser(UUID pollId) {
        var poll = pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));
        return pollMapper.toResponseForAnonymousUser(poll);
    }

    public GetPoll.Response getPollForAuthenticatedUser(UUID pollId, AppUserDetails userDetails) {
        var poll = pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));
        var isAnswered = pollAnswerRecordRepository.existsByPollIdAndUserId(poll.getId(), userDetails.getId());
        return pollMapper.toResponseForAuthenticatedUser(poll, isAnswered);
    }

    public List<GetPoll.Response> toListOfResponsesForAuthenticatedUser(List<Poll> polls, AppUserDetails userDetails) {
        var answeredPollIds = pollAnswerRecordRepository.findAnsweredPollIdsByUserAndPollIds(userDetails.getId(), polls.stream().map(Poll::getId).toList());
        return polls.stream().map(poll -> pollMapper.toResponseForAuthenticatedUser(poll, answeredPollIds.contains(poll.getId()))).toList();
    }
}
