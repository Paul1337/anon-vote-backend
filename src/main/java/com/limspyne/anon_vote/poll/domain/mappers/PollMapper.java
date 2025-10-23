package com.limspyne.anon_vote.poll.domain.mappers;

import com.limspyne.anon_vote.poll.domain.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.web.dto.GetPoll;
import com.limspyne.anon_vote.poll.domain.entities.Poll;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PollMapper {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PollAnswerRecordRepository pollAnswerRecordRepository;

    public GetPoll.Response toResponseForAnonymousUser(Poll poll) {
        GetPoll.Response response = modelMapper.map(poll, GetPoll.Response.class);
        response.setAnswered(false);
        return response;
    }

    public GetPoll.Response toResponseForAuthenticatedUser(Poll poll, AppUserDetails userDetails) {
        var isAnswered = pollAnswerRecordRepository.existsByPollIdAndUserId(poll.getId(), userDetails.getId());
        return toResponseForAuthenticatedUser(poll, isAnswered);
    }

    public List<GetPoll.Response> toListOfResponsesForAuthenticatedUser(List<Poll> polls, AppUserDetails userDetails) {
        var answeredPollIds = pollAnswerRecordRepository.findAnsweredPollIdsByUserAndPollIds(userDetails.getId(), polls.stream().map(Poll::getId).toList());
        return polls.stream().map(poll -> toResponseForAuthenticatedUser(poll, answeredPollIds.contains(poll.getId()))).toList();
    }

    private GetPoll.Response toResponseForAuthenticatedUser(Poll poll, boolean isAnswered) {
        GetPoll.Response response = modelMapper.map(poll, GetPoll.Response.class);
        response.setAnswered(isAnswered);
        return response;
    }

}
