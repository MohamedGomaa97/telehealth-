package com.telehealth.modules.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthRequest {

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}
}
