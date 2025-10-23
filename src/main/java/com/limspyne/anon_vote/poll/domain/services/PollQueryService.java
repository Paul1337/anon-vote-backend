package com.limspyne.anon_vote.poll.domain.services;

import com.limspyne.anon_vote.poll.domain.entities.Poll;
import com.limspyne.anon_vote.poll.domain.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.domain.mappers.PollMapper;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PollQueryService {
    @Autowired
    private PollMapper pollMapper;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollAnswerRecordRepository pollAnswerRecordRepository;

    public Poll getPoll(UUID pollId) {
        return pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));
    }
}
