package com.limspyne.anon_vote.shared.presenter.dto;

import java.util.List;

public record HttpErrorResponse(String message, List<String> details) {
    public HttpErrorResponse(String message) {
        this(message, List.of());
    }
}
