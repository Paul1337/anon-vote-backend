package com.limspyne.anon_vote.poll.domain.services;

import com.limspyne.anon_vote.poll.domain.entities.Poll;
import com.limspyne.anon_vote.poll.domain.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.infrastructure.mappers.PollMapper;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollAnswerRecordRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.web.dto.GetPoll;
import com.limspyne.anon_vote.poll.web.dto.SearchPolls;
import com.limspyne.anon_vote.shared.web.dto.PageResponseDto;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PollQueryService {
    @Autowired
    private PollMapper pollMapper;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollAnswerRecordRepository pollAnswerRecordRepository;

    public Poll getPollById(UUID pollId) {
        return pollRepository.findById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));
    }

    @Transactional(readOnly = true)
    public GetPoll.Response getPollByIdForAnonymousUser(UUID pollId) {
        var poll = getPollById(pollId);
        return pollMapper.toResponseForAnonymousUser(poll);
    }

    @Transactional(readOnly = true)
    public GetPoll.Response getPollByIdForAuthedUser(UUID pollId, AppUserDetails userDetails) {
        var poll = getPollById(pollId);
        return pollMapper.toResponseWithUserSpecificData(poll, userDetails);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<GetPoll.Response> searchPolls(SearchPolls.Request dto, AppUserDetails userDetails) {
        Page<Poll> pollsPage;
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize());
        if (dto.getTags().isEmpty()) {
            pollsPage = pollRepository.findAllByTitle(dto.getTitle(), dto.getCategoryId(), pageRequest);
        } else {
            pollsPage = pollRepository.findAllByTitleAndTags(dto.getTitle(), dto.getCategoryId(), dto.getTags(), pageRequest);
        }
        List<GetPoll.Response> pollsDtos;

        if (userDetails != null) {
            pollsDtos = pollMapper.toListOfResponsesForAuthenticatedUser(pollsPage.getContent(), userDetails);
        } else {
            pollsDtos = pollsPage.stream().map(poll -> pollMapper.toResponseForAnonymousUser(poll)).toList();
        }
        return new PageResponseDto<>(pollsDtos, pollsPage.hasNext());
    }

    @Transactional(readOnly = true)
    public PageResponseDto<GetPoll.Response> findUsersPolls(int page, int size, AppUserDetails userDetails) {
        PageRequest pageRequest = PageRequest.of(page, size);
        var pollsPage = pollRepository.findAllByAuthorId(userDetails.getId(), pageRequest);
        var pollsDtos = pollMapper.toListOfResponsesForAuthenticatedUser(pollsPage.getContent(), userDetails);
        return new PageResponseDto<>(pollsDtos, pollsPage.hasNext());
    }
}
