package com.telehealth.modules.patient.api.dto;

import com.telehealth.modules.patient.domain.model.BloodType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public final class PatientRequest {

    public record RegisterPatientRequest(
            @NotBlank String fullName,
            @NotBlank @Email String email,
            @NotBlank String phoneNumber,
            @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
            @NotNull @Past LocalDate dateOfBirth,
            @NotBlank String nationalId,
            @NotNull BloodType bloodType,
            @NotBlank String address
    ) {}

    public record UpdateLocationRequest(
            @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
            @DecimalMin("-180.0") @DecimalMax("180.0") double longitude
    ) {}

    public record AddAllergyRequest(@NotBlank String allergy) {}

    public record EmergencyContactRequest(
            @NotBlank String name,
            @NotBlank String relationship,
            @NotBlank String phone
    ) {}
}
