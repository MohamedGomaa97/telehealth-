package com.telehealth.modules.consultation.application.queries;

import com.telehealth.modules.consultation.domain.model.ConsultationStatus;
import com.telehealth.modules.consultation.domain.model.Specialization;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Consultation Module - Queries (Read Side of CQRS)
 */
public sealed interface ConsultationQuery
        permits ConsultationQuery.GetConsultationById,
                ConsultationQuery.GetConsultationsByPatient,
                ConsultationQuery.GetConsultationsByDoctor,
                ConsultationQuery.GetPendingConsultations,
                ConsultationQuery.GetConsultationsByStatus {

    record GetConsultationById(@NotNull UUID consultationId) implements ConsultationQuery {}

    record GetConsultationsByPatient(@NotNull UUID patientId) implements ConsultationQuery {}

    record GetConsultationsByDoctor(@NotNull UUID doctorId) implements ConsultationQuery {}

    record GetPendingConsultations(Specialization specialization) implements ConsultationQuery {}

    record GetConsultationsByStatus(@NotNull ConsultationStatus status) implements ConsultationQuery {}
}
