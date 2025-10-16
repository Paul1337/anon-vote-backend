package com.limspyne.anon_vote.users.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthUser {
    @Data
    @NoArgsConstructor
    public static class Request {
        private String email;
        private String code;
    }
}
