package com.limspyne.anon_vote.poll.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class GetCategory {

    @NoArgsConstructor
    @Data
    public static class ResponseDto {
        public UUID id;
        public String name;
        public List<ResponseDto> children;

        public ResponseDto(UUID id, String name, List<ResponseDto> children) {
            this.id = id;
            this.name = name;
            this.children = children;
        }
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class ResponseWithPathDto {
        public UUID id;
        public String name;
        public List<String> path;
    }
}
