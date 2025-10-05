package com.example.cloudstorage.services;

import com.example.cloudstorage.controllers.requests.RefreshTokenRequest;
import com.example.cloudstorage.controllers.responses.JwtResponse;
import com.example.cloudstorage.exceptions.RefreshTokenDoesNotExist;
import com.example.cloudstorage.models.RefreshToken;
import com.example.cloudstorage.models.User;
import com.example.cloudstorage.repos.RefreshTokenRepo;
import com.example.cloudstorage.repos.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    @Value("${jwt.access-token.secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;

    public final JwtService jwtService;

    public RefreshTokenService(JwtService jwtService, UserRepo userRepo, RefreshTokenRepo refreshTokenRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService
                .getClaimsFromToken(refreshTokenRequest.accessToken(), ACCESS_TOKEN_SECRET_KEY)
                .getSubject();

        User user = userRepo.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        RefreshToken refreshToken = refreshTokenRepo.findByUser(user).orElseThrow(
                () -> new RefreshTokenDoesNotExist("Refresh token not found")
        );

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        refreshToken.setToken(newRefreshToken);
        refreshTokenRepo.save(refreshToken);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }
}