package com.limspyne.anon_vote.poll.services;

import com.limspyne.anon_vote.poll.entities.Poll;
import com.limspyne.anon_vote.poll.entities.PollAnswerRecord;
import com.limspyne.anon_vote.poll.entities.PollQuestionAnswer;
import com.limspyne.anon_vote.poll.entities.Question;
import com.limspyne.anon_vote.poll.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.repositories.PollRepository;
import com.limspyne.anon_vote.poll.repositories.QuestionRepository;
import com.limspyne.anon_vote.users.entities.User;
import com.limspyne.anon_vote.users.repositories.UserRepository;
import jakarta.persistence.EntityManager;
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

    @Transactional
    public void submitPoll(UUID pollId, Map<UUID, String> answersMap) {
        Poll poll = pollRepository.getReferenceById(pollId);
        User user = userRepository.getReferenceById(UUID.randomUUID());

        List<PollQuestionAnswer> answersEntities = answersMap.entrySet().stream().map(
                (entry) -> new PollQuestionAnswer(questionRepository.getReferenceById(entry.getKey()), entry.getValue())
        ).toList();

        PollAnswerRecord answerRecord = new PollAnswerRecord(user, poll, answersEntities);
        pollAnswerRecordRepository.save(answerRecord);
    }
}
