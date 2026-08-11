package com.telehealth.modules.auth.api.controller;

import com.telehealth.modules.auth.api.dto.AuthRequest;
import com.telehealth.modules.auth.api.dto.AuthResponse;
import com.telehealth.modules.auth.application.commands.AuthCommand;
import com.telehealth.modules.auth.application.handlers.AuthApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Module", description = "Login and JWT issuance for patients, doctors, and admins")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/login")
    @Operation(summary = "Login with email + password and receive a JWT access token")
    public ResponseEntity<AuthResponse.TokenResponse> login(@Valid @RequestBody AuthRequest.LoginRequest request) {
        String token = authApplicationService.login(new AuthCommand.Login(request.email(), request.password()));
        return ResponseEntity.ok(new AuthResponse.TokenResponse(token));
    }
}
