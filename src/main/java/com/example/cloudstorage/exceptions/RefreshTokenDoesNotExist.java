package com.example.cloudstorage.exceptions;

public class RefreshTokenDoesNotExist extends RuntimeException {
    public RefreshTokenDoesNotExist(String message) {
        super(message);
    }
}