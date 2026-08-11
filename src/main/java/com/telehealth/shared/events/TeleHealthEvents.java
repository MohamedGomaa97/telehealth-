package com.telehealth.shared.events;

import com.telehealth.shared.domain.DomainEvent;

import java.util.UUID;

/**
 * All cross-module domain events used via RabbitMQ.
 * Each module publishes and listens to relevant events.
 *
 * Event Flow:
 * ┌─────────────┐    ConsultationRequestedEvent    ┌──────────────┐
 * │   Patient   │ ─────────────────────────────►   │ Consultation │
 * └─────────────┘                                  └──────┬───────┘
 *                                                         │ DoctorAssignedEvent
 *                                                         ▼
 * ┌─────────────┐    ConsultationAssignedEvent     ┌──────────────┐
 * │   Doctor    │ ◄───────────────────────────────  │ Consultation │
 * └─────────────┘                                  └──────────────┘
 *
 * ┌─────────────┐    EmergencyTriggeredEvent       ┌──────────────┐
 * │   Patient   │ ─────────────────────────────►   │  Emergency   │
 * └─────────────┘                                  └──────┬───────┘
 *                                                         │ EmergencyDispatchedEvent
 *                                                         ▼
 *                                                  ┌──────────────┐
 *                                                  │ Notification │
 *                                                  └──────────────┘
 */
public final class TeleHealthEvents {

    // ─── Consultation Events ───────────────────────────────────────────────────

    public record ConsultationRequestedEvent(
            UUID eventId,
            UUID consultationId,
            UUID patientId,
            String consultationType,   // HOME_VISIT | REMOTE | EMERGENCY
            String specialization,
            String urgencyLevel,       // LOW | MEDIUM | HIGH | CRITICAL
            String patientLocation,
            String occurredAt,
            String sourceModule
    ) {}

    public record ConsultationAssignedEvent(
            UUID eventId,
            UUID consultationId,
            UUID patientId,
            UUID doctorId,
            String doctorName,
            String scheduledAt,
            String consultationType,
            String occurredAt,
            String sourceModule
    ) {}

    public record ConsultationCompletedEvent(
            UUID eventId,
            UUID consultationId,
            UUID patientId,
            UUID doctorId,
            String diagnosis,
            String prescription,
            String completedAt,
            String sourceModule
    ) {}

    public record ConsultationCancelledEvent(
            UUID eventId,
            UUID consultationId,
            UUID patientId,
            String reason,
            String cancelledAt,
            String sourceModule
    ) {}

    // ─── Emergency Events ──────────────────────────────────────────────────────

    public record EmergencyTriggeredEvent(
            UUID eventId,
            UUID emergencyId,
            UUID patientId,
            String patientName,
            String patientPhone,
            String location,
            double latitude,
            double longitude,
            String emergencyType,     // CARDIAC | RESPIRATORY | TRAUMA | GENERAL
            String severity,          // CRITICAL | SEVERE | MODERATE
            String triggeredAt,
            String sourceModule
    ) {}

    public record EmergencyDispatchedEvent(
            UUID eventId,
            UUID emergencyId,
            UUID patientId,
            UUID responderId,
            String responderName,
            String responderPhone,
            String estimatedArrival,
            String dispatchedAt,
            String sourceModule
    ) {}

    public record EmergencyResolvedEvent(
            UUID eventId,
            UUID emergencyId,
            UUID patientId,
            String resolution,
            String resolvedAt,
            String sourceModule
    ) {}

    // ─── Doctor Events ─────────────────────────────────────────────────────────

    public record DoctorAvailabilityChangedEvent(
            UUID eventId,
            UUID doctorId,
            boolean available,
            String availabilityZone,
            String changedAt,
            String sourceModule
    ) {}

    // ─── Patient Events ────────────────────────────────────────────────────────

    public record PatientRegisteredEvent(
            UUID eventId,
            UUID patientId,
            String patientName,
            String email,
            String phone,
            String registeredAt,
            String sourceModule
    ) {}

    private TeleHealthEvents() {}
}
