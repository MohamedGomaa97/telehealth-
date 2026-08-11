package com.telehealth.modules.emergency.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "emergencies")
@Getter @Setter
public class EmergencyJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;

    @Column(name = "patient_phone", nullable = false, length = 20)
    private String patientPhone;

    @Column(name = "location", nullable = false, length = 500)
    private String location;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "responder_id")
    private UUID responderId;

    @Column(name = "responder_name", length = 100)
    private String responderName;

    @Column(name = "responder_phone", length = 20)
    private String responderPhone;

    @Column(name = "estimated_arrival_minutes", length = 10)
    private String estimatedArrivalMinutes;

    @Column(name = "resolution_notes", length = 1000)
    private String resolutionNotes;

    @Column(name = "triggered_at")
    private LocalDateTime triggeredAt;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
