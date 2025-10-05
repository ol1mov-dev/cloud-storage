package com.example.cloudstorage.controllers.requests;

public record AuthenticationRequest(
        String email,
        String password
) {}
