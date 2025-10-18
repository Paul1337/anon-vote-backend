package com.limspyne.anon_vote.shared.web.dto;

import java.util.List;

public record HttpErrorResponse(String message, List<String> details) {
    public HttpErrorResponse(String message) {
        this(message, List.of());
    }
}
