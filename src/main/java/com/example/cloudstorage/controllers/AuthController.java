package com.example.cloudstorage.controllers;

import com.example.cloudstorage.controllers.requests.RefreshTokenRequest;
import com.example.cloudstorage.controllers.requests.RegistrationRequest;
import com.example.cloudstorage.controllers.responses.JwtResponse;
import com.example.cloudstorage.services.AuthService;
import com.example.cloudstorage.services.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(authService.register(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(refreshTokenService.refreshToken(request));
    }
}