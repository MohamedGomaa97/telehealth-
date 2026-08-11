package com.telehealth.modules.consultation.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consultations")
@Getter @Setter
public class ConsultationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "required_specialization", length = 50)
    private String requiredSpecialization;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "urgency_level", length = 20)
    private String urgencyLevel;

    @Column(name = "patient_notes", length = 1000)
    private String patientNotes;

    @Column(name = "patient_location", length = 500)
    private String patientLocation;

    @Column(name = "patient_latitude")
    private double patientLatitude;

    @Column(name = "patient_longitude")
    private double patientLongitude;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "diagnosis", length = 2000)
    private String diagnosis;

    @Column(name = "prescription", length = 2000)
    private String prescription;

    @Column(name = "doctor_notes", length = 2000)
    private String doctorNotes;

    @Column(name = "patient_rating")
    private Double patientRating;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
