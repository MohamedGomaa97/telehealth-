package com.telehealth.modules.auth.application.handlers;

import com.telehealth.config.security.JwtService;
import com.telehealth.modules.auth.application.commands.AuthCommand;
import com.telehealth.modules.auth.domain.model.AppUser;
import com.telehealth.modules.auth.domain.model.UserRole;
import com.telehealth.modules.auth.domain.repository.AppUserRepository;
import com.telehealth.shared.exceptions.TeleHealthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthApplicationService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public UUID createCredentials(AuthCommand.CreateCredentials cmd) {
        if (appUserRepository.existsByEmail(cmd.email())) {
            throw new TeleHealthException.ConflictException(
                    "An account with email '" + cmd.email() + "' already exists");
        }
        String hash = passwordEncoder.encode(cmd.rawPassword());
        AppUser user = AppUser.create(cmd.email(), hash, cmd.role(), cmd.linkedId());
        AppUser saved = appUserRepository.save(user);
        log.info("Credentials created for {} account: {}", cmd.role(), cmd.email());
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public String login(AuthCommand.Login cmd) {
        AppUser user = appUserRepository.findByEmail(cmd.email().toLowerCase())
                .orElseThrow(() -> new TeleHealthException.UnauthorizedException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new TeleHealthException.UnauthorizedException("Account is disabled");
        }

        if (!passwordEncoder.matches(cmd.rawPassword(), user.getPasswordHash())) {
            throw new TeleHealthException.UnauthorizedException("Invalid email or password");
        }

        // Subject = the id that business endpoints actually check against
        // (Patient.id / Doctor.id for PATIENT/DOCTOR, the AppUser.id for ADMIN).
        String subject = user.getRole() == UserRole.ADMIN
                ? user.getId().toString()
                : user.getLinkedId().toString();

        return jwtService.generateToken(subject, Map.of(
                "role", user.getRole().name(),
                "email", user.getEmail()
        ));
    }
}
