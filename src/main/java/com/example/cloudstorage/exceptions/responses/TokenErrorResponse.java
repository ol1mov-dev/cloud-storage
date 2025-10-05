package com.example.cloudstorage.exceptions.responses;

import lombok.Builder;

@Builder
public record TokenErrorResponse(
    int httpStatusCode,
    String message
) {}
