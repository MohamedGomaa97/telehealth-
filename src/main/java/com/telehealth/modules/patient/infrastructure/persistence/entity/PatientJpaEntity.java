package com.telehealth.modules.patient.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Patient JPA Entity - Infrastructure Layer
 *
 * This is a persistence concern ONLY. It knows about databases.
 * It is mapped to/from the domain model via PatientPersistenceMapper.
 * The domain Patient class has ZERO JPA annotations.
 */
@Entity
@Table(name = "patients", schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_patients_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_patients_national_id", columnNames = "national_id")
        })
@Getter
@Setter
public class PatientJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "national_id", nullable = false, length = 20)
    private String nationalId;

    @Column(name = "blood_type", length = 10)
    private String bloodType;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // Emergency contact stored as JSON column
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "emergency_contact", columnDefinition = "jsonb")
    private EmergencyContactJson emergencyContact;

    // Medical history stored as JSON array
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "medical_history", columnDefinition = "jsonb")
    private List<MedicalRecordJson> medicalHistory = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allergies", columnDefinition = "jsonb")
    private List<String> allergies = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chronic_conditions", columnDefinition = "jsonb")
    private List<String> chronicConditions = new ArrayList<>();

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ─── Embedded JSON Types ───────────────────────────────────────────────────

    public record EmergencyContactJson(String name, String relationship, String phoneNumber) {}

    public record MedicalRecordJson(
            UUID recordId,
            UUID consultationId,
            String diagnosis,
            String prescription,
            String doctorName,
            LocalDateTime recordedAt,
            String notes
    ) {}
}
