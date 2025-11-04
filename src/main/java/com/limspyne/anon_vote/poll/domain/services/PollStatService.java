package com.limspyne.anon_vote.poll.domain.services;

import com.limspyne.anon_vote.poll.domain.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PollStatService {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollAnswerRecordRepository pollAnswerRecordRepository;

    @Autowired
    private PollQueryService pollQueryService;

    public Map<UUID, Map<String, Long>> getPollStat(UUID pollId) {
        var pollAnswerRecords = pollAnswerRecordRepository.findAllByPollId(pollId);

        Map<UUID, Map<String, Long>> response = new HashMap<>();

        var poll = pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));

        for (var question: poll.getQuestions()) {
            var defaultStat = new HashMap<String, Long>();
            question.getOptions().forEach(option -> {
                defaultStat.put(option, 0L);
            });
            response.put(question.getId(), defaultStat);
        }

        for (var record: pollAnswerRecords) {
            var answers = record.getAnswers();
            for (var answer: answers) {
                var questionMap = response.getOrDefault(answer.getQuestion().getId(), new HashMap<>());
                questionMap.put(answer.getAnswer(), questionMap.getOrDefault(answer.getAnswer(), 0L) + 1);
                response.put(answer.getQuestion().getId(), questionMap);
            }
        }

        return response;
    }

}
