package com.limspyne.anon_vote.poll.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollStatController {

    @GetMapping("/{pollId}/stats")
    public ResponseEntity<Void> getPollStats(@PathVariable("pollId") UUID pollId) {

        return ResponseEntity.ok().build();
    }
}
