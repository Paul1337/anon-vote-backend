package com.limspyne.anon_vote.poll.domain.mappers;

import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.web.dto.GetPoll;
import com.limspyne.anon_vote.poll.domain.entities.Poll;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PollMapper {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PollRepository pollRepository;

    public GetPoll.Response toResponseForAnonymousUser(Poll poll) {
        return toResponseWithUserSpecificData(poll, false);
    }

    public GetPoll.Response toResponseWithUserSpecificData(Poll poll, AppUserDetails userDetails) {
        var isAnswered = pollRepository.existsByIdAndAttemptedUsersId(poll.getId(), userDetails.getId());
        return toResponseWithUserSpecificData(poll, isAnswered);
    }

    public List<GetPoll.Response> toListOfResponsesForAuthenticatedUser(List<Poll> polls, AppUserDetails userDetails) {
        var answeredPollIds = pollRepository.findIdByAttemptedUsersIdAndIdIn(userDetails.getId(), polls.stream().map(Poll::getId).toList());
        return polls.stream().map(poll -> toResponseWithUserSpecificData(poll, answeredPollIds.contains(poll.getId()))).toList();
    }

    private GetPoll.Response toResponseWithUserSpecificData(Poll poll, boolean isAnswered) {
        GetPoll.Response response = modelMapper.map(poll, GetPoll.Response.class);
        response.setAnswered(isAnswered);
        return response;
    }

}
