package com.limspyne.anon_vote.poll.web.controllers;

import com.limspyne.anon_vote.poll.web.dto.SearchPolls;
import com.limspyne.anon_vote.poll.web.dto.SubmitPoll;
import com.limspyne.anon_vote.poll.domain.entities.PollTag;
import com.limspyne.anon_vote.poll.domain.entities.Question;
import com.limspyne.anon_vote.poll.domain.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.domain.mappers.PollMapper;
import com.limspyne.anon_vote.poll.infrastructure.repositories.CategoryRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.web.dto.CreatePoll;
import com.limspyne.anon_vote.poll.web.dto.GetPoll;
import com.limspyne.anon_vote.poll.domain.entities.Poll;
import com.limspyne.anon_vote.poll.domain.services.PollQueryService;
import com.limspyne.anon_vote.poll.domain.services.PollSubmitService;
import com.limspyne.anon_vote.poll.domain.services.PollTagProviderService;
import com.limspyne.anon_vote.shared.web.dto.PageResponseDto;
import com.limspyne.anon_vote.users.domain.services.UserService;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollController {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PollMapper pollMapper;

    @Autowired
    private PollQueryService pollQueryService;

    @Autowired
    private PollTagProviderService pollTagProviderService;

    @Autowired
    private PollSubmitService pollSubmitService;

    @Autowired
    private UserService userService;

    @PostMapping({""})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetPoll.Response> createPoll(@RequestBody @Valid CreatePoll.Request dto, @AuthenticationPrincipal AppUserDetails userDetails) {
        UUID categoryId = UUID.fromString(dto.categoryId());
        var pollCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));

        var author = userService.getUserById(userDetails.getId());

        Poll poll = new Poll(dto.title(), pollCategory, author);

        Set<PollTag> tags = pollTagProviderService.provideTagsOfNames(dto.tags());
        poll.setTags(tags);

        List<Question> questions = dto.questions().stream().map(qstDto -> {
            var question = new Question(qstDto.getText(), qstDto.getOptions());
            poll.addQuestion(question);
            return question;
        }).toList();

        poll.setQuestions(questions);

        pollRepository.save(poll);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pollMapper.toResponseForAnonymousUser(poll));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<GetPoll.Response>> searchPolls(@Parameter(description = "Search parameters for polls") @Validated SearchPolls.Request request, @AuthenticationPrincipal AppUserDetails userDetails) {
        Page<Poll> pollsPage;
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        if (request.getTags().isEmpty()) {
            pollsPage = pollRepository.findAllByTitle(request.getTitle(), request.getCategoryId(), pageRequest);
        } else {
            pollsPage = pollRepository.findAllByTitleAndTags(request.getTitle(), request.getCategoryId(), request.getTags(), pageRequest);
        }
        List<GetPoll.Response> pollsDtos;

        if (userDetails != null) {
            pollsDtos = pollMapper.toListOfResponsesForAuthenticatedUser(pollsPage.getContent(), userDetails);
        } else {
            pollsDtos = pollsPage.stream().map(poll -> pollMapper.toResponseForAnonymousUser(poll)).toList();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new PageResponseDto<>(pollsDtos, pollsPage.hasNext()));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponseDto<GetPoll.Response>> searchPolls(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @AuthenticationPrincipal AppUserDetails userDetails) {
        PageRequest pageRequest = PageRequest.of(page, size);
        var pollsPage = pollRepository.findAllByAuthorId(userDetails.getId(), pageRequest);
        var pollsDtos = pollMapper.toListOfResponsesForAuthenticatedUser(pollsPage.getContent(), userDetails);
        return ResponseEntity.ok().body(new PageResponseDto<>(pollsDtos, pollsPage.hasNext()));
    }

    @PostMapping("/{id}/submit")
    @Operation(
            summary = "Submit poll answers",
            description = "Creates a new poll answer record with the provided answers"
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> submitPoll(@RequestBody SubmitPoll.Request request, @PathVariable(name = "id") UUID pollId) {
        pollSubmitService.submitPoll(pollId, request.getAnswers());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPoll.Response> getPoll(@PathVariable(name = "id") UUID pollId, @AuthenticationPrincipal AppUserDetails userDetails) {
        GetPoll.Response pollResponse;
        var poll = pollQueryService.getPoll(pollId);
        if (userDetails != null) {
            pollResponse = pollMapper.toResponseForAuthenticatedUser(poll, userDetails);
        } else {
            pollResponse = pollMapper.toResponseForAnonymousUser(poll);
        }
        return ResponseEntity.status(HttpStatus.OK).body(pollResponse);
    }


}
