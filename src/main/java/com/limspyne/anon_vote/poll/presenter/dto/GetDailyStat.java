package com.limspyne.anon_vote.poll.presenter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GetDailyStat {
    @Data
    @NoArgsConstructor
    public static class Request {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatItem {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        private Map<UUID, Map<String, Long>> answers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private List<StatItem> data;
    }
}
