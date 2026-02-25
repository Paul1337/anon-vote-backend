package com.limspyne.anon_vote.poll.application.services.stat;

import com.limspyne.anon_vote.poll.application.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.application.services.query.PollQueryService;
import com.limspyne.anon_vote.poll.infrastructure.dto.AnswerStatProjection;
import com.limspyne.anon_vote.poll.infrastructure.dto.StatProjection;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.presenter.dto.GetDailyStat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollStatService {
    private final PollRepository pollRepository;

    private final PollAnswerRecordRepository answerRecordRepository;

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Map<UUID, Map<String, Long>> getBasicStat(UUID pollId) {
        var pollAnswerRecords = answerRecordRepository.findAllByPollId(pollId);

        Map<UUID, Map<String, Long>> response = new HashMap<>();

        var poll = pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));

        for (var question: poll.getQuestions()) {
            var defaultStat = new TreeMap<String, Long>();
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

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public GetDailyStat.Response getAnswerStatsByDay(UUID pollId, LocalDate startDate, LocalDate endDate) {
        // Бизнес-метод: получение кумулятивной статистики по дням
        // Полезно для анализа трендов и динамики ответов

        var poll = pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));

        List<AnswerStatProjection> statsInInterval = answerRecordRepository
                .getAnswerStatsByDateRangeGroupedByDays(
                        pollId,
                        startDate.atStartOfDay(),
                        endDate.atStartOfDay().plusDays(1)
                );
        List<StatProjection> statsUpToStartDay = answerRecordRepository.getAnswerStatsUpToInstant(pollId, startDate.atStartOfDay());

        Map<LocalDate, List<AnswerStatProjection>> dateToStatProjections = statsInInterval.stream()
                .collect(Collectors.groupingBy(AnswerStatProjection::getDate));

        List<GetDailyStat.StatItem> result = new ArrayList<>();
        Map<UUID, Map<String, Long>> answers = new HashMap<>();

        for (var statItem: statsUpToStartDay) {
            if (!answers.containsKey(statItem.getQuestionId())) {
                answers.put(statItem.getQuestionId(), new TreeMap<>());
            }
            answers.get(statItem.getQuestionId()).put(statItem.getAnswerText(), statItem.getAnswerCount());
        }

        poll.getQuestions().forEach(question -> {
            var answersForQuestion = answers.getOrDefault(question.getId(), new TreeMap<>());
            question.getOptions().forEach(option -> {
                if (!answersForQuestion.containsKey(option)) {
                    answersForQuestion.put(option, 0L);
                }
            });
            answers.put(question.getId(), answersForQuestion);
        });

        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            List<AnswerStatProjection> statsForDay = dateToStatProjections.get(currentDate);

            if (statsForDay != null) {
                for (var statItem: statsForDay) {
                    var question = answers.get(statItem.getQuestionId());
                    var addCount = statItem.getAnswerCount();
                    question.putIfAbsent(statItem.getAnswerText(), 0L);
                    question.compute(statItem.getAnswerText(), (k, prevCount) -> prevCount + addCount);
                }
            }

            Map<UUID, Map<String, Long>> currentAnswersCopy = answers.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new TreeMap<>(entry.getValue())
                    ));

            result.add(new GetDailyStat.StatItem(currentDate, currentAnswersCopy));

            currentDate = currentDate.plusDays(1);
        }

        return new GetDailyStat.Response(result);
    }

}
