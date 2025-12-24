package com.limspyne.anon_vote.shared.presentation.dto;

import java.util.List;

public record HttpErrorResponse(String message, List<String> details) {
    public HttpErrorResponse(String message) {
        this(message, List.of());
    }
}
