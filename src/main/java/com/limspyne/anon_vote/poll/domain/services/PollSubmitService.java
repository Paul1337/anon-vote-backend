package com.limspyne.anon_vote.poll.domain.services;

import com.limspyne.anon_vote.poll.domain.entities.Poll;
import com.limspyne.anon_vote.poll.domain.entities.PollAnswerRecord;
import com.limspyne.anon_vote.poll.domain.entities.PollQuestionAnswer;
import com.limspyne.anon_vote.poll.domain.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.domain.exceptions.SubmitNotUniqueException;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.QuestionRepository;
import com.limspyne.anon_vote.users.domain.entities.User;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import com.limspyne.anon_vote.users.instrastructure.security.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PollSubmitService {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollAnswerRecordRepository pollAnswerRecordRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityContextService securityContextService;

    @Transactional
    public void submitPoll(UUID pollId, Map<UUID, String> answersMap) {
        User user = securityContextService.getCurrentUser();
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));

        if (pollRepository.existsByIdAndAttemptedUsersId(poll.getId(), user.getId())) {
            throw new SubmitNotUniqueException();
        }

        List<PollQuestionAnswer> answersEntities = answersMap.entrySet().stream().map(
                (entry) -> new PollQuestionAnswer(questionRepository.getReferenceById(entry.getKey()), entry.getValue())
        ).toList();

        PollAnswerRecord answerRecord = new PollAnswerRecord(poll, answersEntities);
        poll.addAttemptedUser(user);
        pollAnswerRecordRepository.save(answerRecord);
        pollRepository.save(poll);
    }
}
