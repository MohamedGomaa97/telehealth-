package com.telehealth.modules.auth.application.commands;

import com.telehealth.modules.auth.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public sealed interface AuthCommand
        permits AuthCommand.CreateCredentials, AuthCommand.Login {


    record CreateCredentials(
            @NotBlank @Email String email,
            @NotBlank String rawPassword,
            @NotNull UserRole role,
            UUID linkedId
    )  implements AuthCommand {}


    record Login(
            @NotBlank @Email String email,
            @NotBlank String rawPassword
    )  implements AuthCommand {}
}
