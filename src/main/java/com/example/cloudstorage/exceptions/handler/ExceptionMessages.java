package com.example.cloudstorage.exceptions.handler;

import lombok.Getter;

@Getter
public enum ExceptionMessages {
    DEFAULT_USER_ROLE_NOT_FOUND("Default user not found!"),
    USER_WITH_CURRENT_EMAIL_NOT_FOUND("User with current email not found!"),
    REFRESH_TOKEN_DOES_NOT_EXIST("Refresh token does not exist!"),
    REFRESH_TOKEN_EXPIRED("Refresh token is expired!");

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }
}