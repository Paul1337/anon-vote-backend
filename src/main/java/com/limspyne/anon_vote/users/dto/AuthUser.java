package com.limspyne.anon_vote.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthUser {
    @Data
    @NoArgsConstructor
    public static class Request {
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Code is required")
        private String code;
    }
}
