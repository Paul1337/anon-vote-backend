package com.limspyne.anon_vote.poll.web.controllers;

import com.limspyne.anon_vote.poll.domain.services.stat.PollStatService;
import com.limspyne.anon_vote.poll.domain.services.stat.export.PollStatExportService;
import com.limspyne.anon_vote.poll.web.dto.GetBasicStat;
import com.limspyne.anon_vote.poll.web.dto.GetDailyStat;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/polls")
@RequiredArgsConstructor
public class PollStatController {
    private final PollStatService pollStatService;

    private final PollStatExportService exportService;

    @GetMapping("/{pollId}/basicStat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetBasicStat.Response> getPollStat(@PathVariable("pollId") UUID pollId) {
        var statResponse = pollStatService.getBasicStat(pollId);
        return ResponseEntity.ok().body(new GetBasicStat.Response(statResponse));
    }

    @GetMapping("/{pollId}/dailyStat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetDailyStat.Response> getTimeStat(@PathVariable("pollId") UUID pollId, @RequestParam(defaultValue = "7") int daysBefore) {
        var statResult = pollStatService.getAnswerStatsByDay(pollId, LocalDate.now().minusDays(daysBefore - 1), LocalDate.now());
        return ResponseEntity.ok().body(statResult);
    }
//
//    @GetMapping("/{pollId}/dailyStat/export/pdf")
//    public ResponseEntity<byte[]> exportToPdf(
//            @PathVariable UUID pollId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        byte[] pdfContent = exportService.exportToPdf(pollId, startDate, endDate);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"poll_stats_" + LocalDate.now() + ".pdf\"")
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(pdfContent);
//    }

    @GetMapping("/{pollId}/dailyStat/export/csv")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportToCsv(
            @PathVariable UUID pollId,
            @RequestParam(defaultValue = "7") int daysBefore) {

        var startDate = LocalDate.now().minusDays(daysBefore - 1);
        var endDate = LocalDate.now();

        byte[] csvContent = exportService.exportDailyStatToCsv(pollId, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"daily_stats_%s-%s.csv\"".formatted(startDate, endDate))
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csvContent);
    }

}
