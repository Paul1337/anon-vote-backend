package com.limspyne.anon_vote.poll.domain.services;

import com.limspyne.anon_vote.poll.domain.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.infrastructure.dto.AnswerStatProjection;
import com.limspyne.anon_vote.poll.infrastructure.dto.StatProjection;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PollStatService {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollAnswerRecordRepository answerRecordRepository;

    @Autowired
    private PollQueryService pollQueryService;

    public Map<UUID, Map<String, Long>> getBasicStat(UUID pollId) {
        var pollAnswerRecords = answerRecordRepository.findAllByPollId(pollId);

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

    public Map<LocalDate, Map<String, Long>> getAnswerStatsByDay(UUID pollId, LocalDate startDate, LocalDate endDate) {
        List<AnswerStatProjection> statsInInterval = answerRecordRepository.getAnswerStatsByDateRangeGroupedByDays(pollId, startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1));
        List<StatProjection> statsUpToStartDay = answerRecordRepository.getAnswerStatsUpToInstant(pollId, startDate.atStartOfDay());

        Map<String, Long> cumulativeCounts = statsUpToStartDay.stream()
                .collect(Collectors.toMap(
                        StatProjection::getAnswerText,
                        StatProjection::getAnswerCount
                ));

        Map<LocalDate, Map<String, Long>> result = new LinkedHashMap<>();

        result.put(startDate, new HashMap<>(cumulativeCounts));

        for (AnswerStatProjection dailyStat : statsInInterval) {
            cumulativeCounts.merge(
                    dailyStat.getAnswerText(),
                    dailyStat.getAnswerCount(),
                    Long::sum
            );
            result.put(dailyStat.getDate(), new HashMap<>(cumulativeCounts));
        }

        return result;
    }

}
