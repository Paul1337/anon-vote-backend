package com.limspyne.anon_vote.global;

import com.limspyne.anon_vote.shared.AppBasicException;
import com.limspyne.anon_vote.shared.HttpErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Order(10)
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity
                .badRequest()
                .body(new HttpErrorResponse("Validation failed", errors));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<HttpErrorResponse> handleDataAccessException(DataAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new HttpErrorResponse("Database error", List.of(ex.getMessage())));
    }

    @ExceptionHandler(AppBasicException.class)
    public ResponseEntity<HttpErrorResponse> handleAppBasicException(AppBasicException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new HttpErrorResponse(ex.getMessage()));
    }
}

