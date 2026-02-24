package com.limspyne.anon_vote.poll.application.services;

import com.limspyne.anon_vote.poll.application.entities.Poll;
import com.limspyne.anon_vote.poll.application.entities.PollAnswerRecord;
import com.limspyne.anon_vote.poll.application.entities.PollQuestionAnswer;
import com.limspyne.anon_vote.poll.application.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.application.exceptions.SubmitNotUniqueException;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.QuestionRepository;
import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import com.limspyne.anon_vote.users.instrastructure.security.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollSubmitService {
    private final PollRepository pollRepository;

    private final PollAnswerRecordRepository pollAnswerRecordRepository;

    private final QuestionRepository questionRepository;

    private final UserRepository userRepository;

    private final SecurityContextService securityContextService;

    @Transactional
    public void submitPoll(User user, UUID pollId, Map<UUID, String> answersMap) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));

        // Один пользователь может пройти опрос только один раз
        if (pollRepository.existsByIdAndAttemptedUsersId(poll.getId(), user.getId())) {
            throw new SubmitNotUniqueException();
        }

        // Используем getReferenceById() для избежания лишних запросов к БД
        List<PollQuestionAnswer> answersEntities = answersMap.entrySet().stream().map(
                (entry) -> new PollQuestionAnswer(questionRepository.getReferenceById(entry.getKey()), entry.getValue())
        ).toList();

        PollAnswerRecord answerRecord = new PollAnswerRecord(poll, answersEntities);
        poll.addAttemptedUser(user);
        pollAnswerRecordRepository.save(answerRecord);
        pollRepository.save(poll);
    }
}
