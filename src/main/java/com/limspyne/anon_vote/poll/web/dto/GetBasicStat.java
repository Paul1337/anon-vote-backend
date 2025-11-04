package com.limspyne.anon_vote.poll.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

public class GetBasicStat {
    @Data
    @NoArgsConstructor
    public static class Request {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Map<UUID, Map<String, Long>> data;
    }
}
