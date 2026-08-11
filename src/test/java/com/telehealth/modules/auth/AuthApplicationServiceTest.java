package com.telehealth.modules.auth;

import com.telehealth.config.security.JwtService;
import com.telehealth.modules.auth.application.commands.AuthCommand;
import com.telehealth.modules.auth.application.handlers.AuthApplicationService;
import com.telehealth.modules.auth.domain.model.AppUser;
import com.telehealth.modules.auth.domain.model.UserRole;
import com.telehealth.modules.auth.domain.repository.AppUserRepository;
import com.telehealth.shared.exceptions.TeleHealthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private JwtService jwtService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4); // low cost for fast tests

    private AuthApplicationService authApplicationService;

    @BeforeEach
    void setUp() {
        authApplicationService = new AuthApplicationService(appUserRepository, passwordEncoder, jwtService);
    }

    @Test
    void loginSucceedsWithCorrectPassword() {
        UUID patientId = UUID.randomUUID();
        AppUser user = AppUser.create("patient@example.com", passwordEncoder.encode("correct-password"),
                UserRole.PATIENT, patientId);

        when(appUserRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(), any())).thenReturn("fake-jwt-token");

        String token = authApplicationService.login(new AuthCommand.Login("patient@example.com", "correct-password"));

        assertThat(token).isEqualTo("fake-jwt-token");
    }

    @Test
    void loginFailsWithWrongPassword() {
        AppUser user = AppUser.create("patient@example.com", passwordEncoder.encode("correct-password"),
                UserRole.PATIENT, UUID.randomUUID());

        when(appUserRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                authApplicationService.login(new AuthCommand.Login("patient@example.com", "wrong-password")))
                .isInstanceOf(TeleHealthException.UnauthorizedException.class);
    }

    @Test
    void loginFailsForUnknownEmail() {
        when(appUserRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authApplicationService.login(new AuthCommand.Login("nobody@example.com", "whatever")))
                .isInstanceOf(TeleHealthException.UnauthorizedException.class);
    }

    @Test
    void createCredentialsRejectsDuplicateEmail() {
        when(appUserRepository.existsByEmail("dupe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authApplicationService.createCredentials(
                new AuthCommand.CreateCredentials("dupe@example.com", "password123", UserRole.PATIENT, UUID.randomUUID())))
                .isInstanceOf(TeleHealthException.ConflictException.class);
    }
}
