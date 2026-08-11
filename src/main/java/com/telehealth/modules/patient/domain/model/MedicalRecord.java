package com.telehealth.modules.patient.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable value object for medical records / visits.
 */
public record MedicalRecord(
        UUID recordId,
        UUID consultationId,
        String diagnosis,
        String prescription,
        String doctorName,
        LocalDateTime recordedAt,
        String notes
) {
    public MedicalRecord {
        if (recordId == null) recordId = UUID.randomUUID();
        if (recordedAt == null) recordedAt = LocalDateTime.now();
    }

    public static MedicalRecord create(UUID consultationId, String diagnosis,
                                       String prescription, String doctorName, String notes) {
        return new MedicalRecord(
                UUID.randomUUID(), consultationId, diagnosis,
                prescription, doctorName, LocalDateTime.now(), notes
        );
    }
}
