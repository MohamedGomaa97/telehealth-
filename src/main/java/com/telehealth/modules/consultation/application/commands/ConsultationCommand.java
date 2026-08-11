package com.telehealth.modules.consultation.application.commands;

import com.telehealth.modules.consultation.domain.model.ConsultationType;
import com.telehealth.modules.consultation.domain.model.Specialization;
import com.telehealth.modules.consultation.domain.model.UrgencyLevel;
import jakarta.validation.constraints.*;

import java.util.UUID;

public sealed interface ConsultationCommand
        permits ConsultationCommand.RequestConsultation,
                ConsultationCommand.AssignDoctor,
                ConsultationCommand.StartConsultation,
                ConsultationCommand.CompleteConsultation,
                ConsultationCommand.CancelConsultation,
                ConsultationCommand.RateConsultation {

    record RequestConsultation(
            @NotNull UUID patientId,
            @NotNull ConsultationType type,
            @NotNull Specialization specialization,
            @NotNull UrgencyLevel urgencyLevel,
            String patientNotes,
            String patientLocation,
            double latitude,
            double longitude
    ) implements ConsultationCommand {}

    record AssignDoctor(
            @NotNull UUID consultationId,
            @NotNull UUID doctorId,
            String scheduledAt
    ) implements ConsultationCommand {}

    record StartConsultation(@NotNull UUID consultationId) implements ConsultationCommand {}

    record CompleteConsultation(
            @NotNull UUID consultationId,
            @NotBlank String diagnosis,
            String prescription,
            String doctorNotes
    ) implements ConsultationCommand {}

    record CancelConsultation(
            @NotNull UUID consultationId,
            @NotBlank String reason
    ) implements ConsultationCommand {}

    record RateConsultation(
            @NotNull UUID consultationId,
            @NotNull UUID patientId,
            @DecimalMin("1.0") @DecimalMax("5.0") double rating
    ) implements ConsultationCommand {}
}
