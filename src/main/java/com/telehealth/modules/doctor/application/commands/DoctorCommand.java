package com.telehealth.modules.doctor.application.commands;

import com.telehealth.modules.doctor.domain.model.Specialization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public sealed interface DoctorCommand
        permits DoctorCommand.RegisterDoctor,
                DoctorCommand.VerifyDoctor,
                DoctorCommand.SetAvailability,
                DoctorCommand.UpdateLocation,
                DoctorCommand.GoOffDuty,
                DoctorCommand.GoOnDuty,
                DoctorCommand.SuspendDoctor,
                DoctorCommand.RecordConsultationRating {

    record RegisterDoctor(
            @NotBlank String fullName,
            @NotBlank String email,
            @NotBlank String phoneNumber,
            @NotBlank(message = "Password is required")
            @jakarta.validation.constraints.Size(min = 8, message = "Password must be at least 8 characters")
            String password,
            @NotBlank String licenseNumber,
            @NotNull Specialization specialization
    ) implements DoctorCommand {}

    record VerifyDoctor(@NotNull UUID doctorId) implements DoctorCommand {}

    record SetAvailability(
            @NotNull UUID doctorId,
            boolean homeVisit,
            boolean remote,
            boolean emergency
    ) implements DoctorCommand {}

    record UpdateLocation(
            @NotNull UUID doctorId,
            double latitude,
            double longitude,
            String coverageZone
    ) implements DoctorCommand {}

    record GoOffDuty(@NotNull UUID doctorId) implements DoctorCommand {}

    record GoOnDuty(@NotNull UUID doctorId) implements DoctorCommand {}

    record SuspendDoctor(@NotNull UUID doctorId, @NotBlank String reason) implements DoctorCommand {}

    record RecordConsultationRating(
            @NotNull UUID doctorId,
            @NotNull UUID consultationId,
            double rating
    ) implements DoctorCommand {}
}
