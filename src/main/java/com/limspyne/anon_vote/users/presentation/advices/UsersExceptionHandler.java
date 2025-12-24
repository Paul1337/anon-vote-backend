package com.limspyne.anon_vote.users.presentation.advices;

import com.limspyne.anon_vote.shared.presentation.dto.HttpErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class UsersExceptionHandler {
    @ExceptionHandler({ BadCredentialsException.class })
    public ResponseEntity<HttpErrorResponse> handleBadRequestErrors(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new HttpErrorResponse("Bad credentials!"));
    }
}

