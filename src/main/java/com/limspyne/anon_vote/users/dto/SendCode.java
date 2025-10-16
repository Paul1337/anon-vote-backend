package com.limspyne.anon_vote.users.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

public class SendCode {
    @Data
    @NoArgsConstructor
    public static class Request {
        private String email;
    }
}
