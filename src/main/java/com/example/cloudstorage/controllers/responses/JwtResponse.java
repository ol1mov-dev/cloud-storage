package com.example.cloudstorage.controllers.responses;

import lombok.Builder;

@Builder
public record JwtResponse(
        String accessToken,
        String refreshToken
) {}
