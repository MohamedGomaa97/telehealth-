package com.telehealth.modules.doctor.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctors", schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_doctors_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_doctors_license", columnNames = "license_number")
        })
@Getter @Setter
public class DoctorJpaEntity {

    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "license_number", nullable = false, length = 50)
    private String licenseNumber;

    @Column(name = "specialization", nullable = false, length = 50)
    private String specialization;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sub_specializations", columnDefinition = "jsonb")
    private List<String> subSpecializations = new ArrayList<>();

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "available_home_visit")
    private boolean availableForHomeVisit;

    @Column(name = "available_remote")
    private boolean availableForRemote;

    @Column(name = "available_emergency")
    private boolean availableForEmergency;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "coverage_zone", length = 100)
    private String coverageZone;

    @Column(name = "rating")
    private double rating;

    @Column(name = "total_consultations")
    private int totalConsultations;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
