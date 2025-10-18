package com.limspyne.anon_vote.poll.domain.services;

import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PollStatService {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollAnswerRecordRepository pollAnswerRecordRepository;

    @Autowired
    private PollService pollService;

    public Map<UUID, Map<UUID, Integer>> getPollStat(UUID pollId) {
//        var poll = pollService.getPoll(pollId);

        var pollAnswerRecords = pollAnswerRecordRepository.findAllByPollId(pollId);

        Map<UUID, Map<UUID, Integer>> response = new HashMap<>();

        for (var record: pollAnswerRecords) {
            var answers = record.getAnswers();
            for (var answer: answers) {
                response.getOrDefault(answer.getQuestion().getId(), new HashMap<>());

            }
        }

        return response;
    }

}
