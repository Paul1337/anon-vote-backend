package com.limspyne.anon_vote.poll.web.controllers;

import com.limspyne.anon_vote.poll.domain.services.PollCreationService;
import com.limspyne.anon_vote.poll.web.dto.*;
import com.limspyne.anon_vote.poll.infrastructure.mappers.PollMapper;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.domain.services.PollQueryService;
import com.limspyne.anon_vote.poll.domain.services.PollSubmitService;
import com.limspyne.anon_vote.shared.web.dto.PageResponseDto;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollController {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollQueryService pollQueryService;

    @Autowired
    private PollSubmitService pollSubmitService;

    @Autowired
    private PollCreationService pollCreationService;

    @PostMapping({""})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetPoll.Response> createPoll(@RequestBody @Valid CreatePoll.Request dto, @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pollCreationService.createPoll(dto,userDetails));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<GetPoll.Response>> searchPolls(@Parameter(description = "Search parameters for polls") @Validated SearchPolls.Request request, @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(pollQueryService.searchPolls(request, userDetails));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponseDto<GetPoll.Response>> findMyPolls(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.ok().body(pollQueryService.findUsersPolls(page, size, userDetails));
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
        if (userDetails != null) {
            pollResponse = pollQueryService.getPollByIdForAuthedUser(pollId, userDetails);
        } else {
            pollResponse = pollQueryService.getPollByIdForAnonymousUser(pollId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(pollResponse);
    }



}
