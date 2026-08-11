package com.telehealth.config;

import com.telehealth.modules.auth.application.commands.AuthCommand;
import com.telehealth.modules.auth.application.handlers.AuthApplicationService;
import com.telehealth.modules.auth.domain.model.UserRole;
import com.telehealth.modules.auth.domain.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final AuthApplicationService authApplicationService;

    @Value("${telehealth.security.admin.email:admin@telehealth.local}")
    private String adminEmail;

    @Value("${telehealth.security.admin.password:ChangeMe123!}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (appUserRepository.existsByEmail(adminEmail.toLowerCase())) {
            return;
        }
        authApplicationService.createCredentials(
                new AuthCommand.CreateCredentials(adminEmail, adminPassword, UserRole.ADMIN, null));
        log.warn("Seeded default ADMIN account ({}). Change its password immediately in production, " +
                "or set TELEHEALTH_ADMIN_EMAIL / TELEHEALTH_ADMIN_PASSWORD before first startup.", adminEmail);
    }
}
