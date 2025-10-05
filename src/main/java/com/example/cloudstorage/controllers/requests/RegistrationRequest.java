package com.example.cloudstorage.controllers.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RegistrationRequest (
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank String email,
        @NotBlank String password
){ }
