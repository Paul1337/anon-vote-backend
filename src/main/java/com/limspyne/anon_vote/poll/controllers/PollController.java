package com.limspyne.anon_vote.poll.controllers;

import com.limspyne.anon_vote.poll.repositories.PollRepository;
import com.limspyne.anon_vote.poll.dto.CreatePoll;
import com.limspyne.anon_vote.poll.dto.GetPoll;
import com.limspyne.anon_vote.poll.entities.Poll;
import com.limspyne.anon_vote.poll.exceptions.PollNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollController {
    @Autowired
    private PollRepository quizRepository;

    @PostMapping()
    public ResponseEntity<CreatePoll.Response> createQuiz(@RequestBody @Valid CreatePoll.Request dto) {
        Poll poll = new Poll(dto.title());
        quizRepository.save(poll);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreatePoll.Response(poll.getId().toString(), poll.getTitle()));
    }

    @PostMapping("/{id}/submit")
    public void submitPoll() {

    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPoll.Response> getQuiz(@PathVariable(name = "id") String id) {
        UUID uuid = UUID.fromString(id);
        var poll = quizRepository.findById(uuid).orElseThrow(() -> new PollNotFoundException(uuid));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GetPoll.Response(poll.getId().toString(), poll.getTitle()));
    }


}
