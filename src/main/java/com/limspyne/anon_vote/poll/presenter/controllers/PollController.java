package com.limspyne.anon_vote.poll.presenter.controllers;

import com.limspyne.anon_vote.poll.application.services.PollCreationService;
import com.limspyne.anon_vote.poll.presenter.dto.*;
import com.limspyne.anon_vote.poll.application.services.query.PollQueryService;
import com.limspyne.anon_vote.poll.application.services.PollSubmitService;
import com.limspyne.anon_vote.shared.presenter.dto.PageResponseDto;
import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import com.limspyne.anon_vote.users.instrastructure.security.SecurityContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/polls")
@RequiredArgsConstructor
public class PollController {
    private final PollQueryService pollQueryService;

    private final PollSubmitService pollSubmitService;

    private final PollCreationService pollCreationService;

    private final SecurityContextService securityContextService;

    @PostMapping({""})
    public ResponseEntity<GetPoll.Response> createPoll(@RequestBody @Valid CreatePoll.Request dto, @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pollCreationService.createPoll(dto, userDetails));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<GetPoll.Response>> searchPolls(@Parameter(description = "Search parameters for polls") @Validated SearchPolls.Request request, @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(pollQueryService.searchPolls(request, userDetails));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponseDto<GetPoll.Response>> findMyPolls(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.ok().body(pollQueryService.findUsersPolls(page, size, userDetails));
    }

    @PostMapping("/{id}/submit")
    @Operation(
            summary = "Submit poll answers",
            description = "Creates a new poll answer record with the provided answers"
    )
    public ResponseEntity<Void> submitPoll(@RequestBody SubmitPoll.Request request, @PathVariable(name = "id") UUID pollId) {
        User user = securityContextService.getCurrentUser();
        pollSubmitService.submitPoll(user, pollId, request.getAnswers());
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
