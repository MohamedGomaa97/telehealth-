package com.telehealth.modules.auth.api.dto;

public final class AuthResponse {

    public record TokenResponse(String accessToken, String tokenType) {
        public TokenResponse(String accessToken) {
            this(accessToken, "Bearer");
        }
    }
}
