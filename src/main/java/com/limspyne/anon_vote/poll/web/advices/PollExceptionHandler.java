package com.limspyne.anon_vote.poll.web.advices;

import com.limspyne.anon_vote.poll.domain.exceptions.PollAlreadyExistException;
import com.limspyne.anon_vote.poll.domain.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.shared.AppBasicException;
import com.limspyne.anon_vote.shared.HttpErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class PollExceptionHandler {
    @ExceptionHandler({ PollNotFoundException.class, PollAlreadyExistException.class })
    public ResponseEntity<HttpErrorResponse> handleBadRequestErrors(AppBasicException ex) {
        return ResponseEntity
                .status(400)
                .body(new HttpErrorResponse(ex.getMessage()));
    }
}

