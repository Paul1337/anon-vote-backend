package com.limspyne.anon_vote.poll.web.controllers;

import com.limspyne.anon_vote.poll.domain.services.PollStatService;
import com.limspyne.anon_vote.poll.web.dto.GetBasicStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollStatController {

    @Autowired
    private PollStatService pollStatService;

    @GetMapping("/{pollId}/basicStat")
    public ResponseEntity<GetBasicStat.Response> getPollStats(@PathVariable("pollId") UUID pollId) {
        var statResponse = pollStatService.getPollStat(pollId);
        return ResponseEntity.ok().body(new GetBasicStat.Response(statResponse));
    }


}
