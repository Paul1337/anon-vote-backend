package com.limspyne.anon_vote.poll.infrastructure.mappers;

import com.limspyne.anon_vote.poll.web.dto.GetPoll;
import com.limspyne.anon_vote.poll.domain.entities.Poll;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PollMapper {
    @Autowired
    private ModelMapper modelMapper;

    public GetPoll.Response toResponseForAnonymousUser(Poll poll) {
        GetPoll.Response response = modelMapper.map(poll, GetPoll.Response.class);
        response.setAnswered(false);
        return response;
    }

    public GetPoll.Response toResponseForAuthenticatedUser(Poll poll, boolean isAnswered) {
        GetPoll.Response response = modelMapper.map(poll, GetPoll.Response.class);
        response.setAnswered(isAnswered);
        return response;
    }


}
