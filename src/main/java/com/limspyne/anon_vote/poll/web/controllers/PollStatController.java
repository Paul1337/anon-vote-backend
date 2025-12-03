package com.limspyne.anon_vote.poll.web.controllers;

import com.limspyne.anon_vote.poll.domain.services.PollStatService;
import com.limspyne.anon_vote.poll.web.dto.GetBasicStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollStatController {

    @Autowired
    private PollStatService pollStatService;

    @GetMapping("/{pollId}/basicStat")
    public ResponseEntity<GetBasicStat.Response> getPollStat(@PathVariable("pollId") UUID pollId) {
        var statResponse = pollStatService.getBasicStat(pollId);
        return ResponseEntity.ok().body(new GetBasicStat.Response(statResponse));
    }

    @GetMapping("/{pollId}/dailyStat")
    public ResponseEntity<Map<LocalDate, Map<String, Long>>> getTimeStat(@PathVariable("pollId") UUID pollId, @RequestParam(defaultValue = "7") int daysBefore) {
        var statResult = pollStatService.getAnswerStatsByDay(pollId, LocalDate.now().minusDays(daysBefore), LocalDate.now());
        return ResponseEntity.ok().body(statResult);
    }


}
