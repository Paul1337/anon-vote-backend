package com.limspyne.anon_vote.poll.domain.services.stat.export;

import com.limspyne.anon_vote.poll.domain.services.stat.PollStatService;
import com.limspyne.anon_vote.poll.web.dto.GetDailyStat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollStatExportService {
    private final PollStatService pollStatService;

    private final PollStatCsvExportService csvExportService;

    public byte[] exportDailyStatToCsv(UUID pollId, LocalDate startDate, LocalDate endDate) {
        GetDailyStat.Response stats = pollStatService.getAnswerStatsByDay(
                pollId, startDate, endDate
        );
        return csvExportService.exportDailyStat(pollId, stats);
    }
}
