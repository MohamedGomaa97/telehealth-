package com.telehealth.modules.patient.application.commands;

import com.telehealth.modules.patient.domain.model.BloodType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Patient Module - Commands (Write Side of CQRS)
 *
 * Commands represent the intent to change state.
 * They are validated before reaching the domain.
 */
public sealed interface PatientCommand
        permits PatientCommand.RegisterPatient,
                PatientCommand.UpdatePatientLocation,
                PatientCommand.AddMedicalRecord,
                PatientCommand.AddAllergy,
                PatientCommand.SetEmergencyContact,
                PatientCommand.SuspendPatient,
                PatientCommand.ReactivatePatient {

    record RegisterPatient(
            @NotBlank(message = "Full name is required")
            @Size(min = 3, max = 100)
            String fullName,

            @NotBlank @Email(message = "Valid email required")
            String email,

            @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Valid phone number required")
            String phoneNumber,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            String password,

            @NotNull
            @Past(message = "Date of birth must be in the past")
            LocalDate dateOfBirth,

            @NotBlank(message = "National ID is required")
            String nationalId,

            @NotNull
            BloodType bloodType,

            @NotBlank
            String address
    ) implements PatientCommand {}

    record UpdatePatientLocation(
            @NotNull UUID patientId,
            @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
            @DecimalMin("-180.0") @DecimalMax("180.0") double longitude
    ) implements PatientCommand {}

    record AddMedicalRecord(
            @NotNull UUID patientId,
            @NotNull UUID consultationId,
            @NotBlank String diagnosis,
            String prescription,
            @NotBlank String doctorName,
            String notes
    ) implements PatientCommand {}

    record AddAllergy(
            @NotNull UUID patientId,
            @NotBlank String allergy
    ) implements PatientCommand {}

    record SetEmergencyContact(
            @NotNull UUID patientId,
            @NotBlank String contactName,
            @NotBlank String relationship,
            @NotBlank String contactPhone
    ) implements PatientCommand {}

    record SuspendPatient(
            @NotNull UUID patientId,
            @NotBlank String reason
    ) implements PatientCommand {}

    record ReactivatePatient(
            @NotNull UUID patientId
    ) implements PatientCommand {}
}
