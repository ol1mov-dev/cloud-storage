package com.example.cloudstorage.exceptions.handler;

import com.example.cloudstorage.exceptions.RefreshTokenDoesNotExist;
import com.example.cloudstorage.exceptions.RefreshTokenExpiredException;
import com.example.cloudstorage.exceptions.responses.TokenErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            RefreshTokenExpiredException.class,
            RefreshTokenDoesNotExist.class
    })
    public ResponseEntity<TokenErrorResponse> handleJwtExceptions(Exception ex) {
        TokenErrorResponse response = TokenErrorResponse
                .builder()
                .httpStatusCode(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}