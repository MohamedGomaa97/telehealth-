package com.telehealth.modules.patient.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class PatientResponse {

    public record PatientSummary(
            UUID id,
            String fullName,
            String email,
            String phoneNumber,
            String status,
            String bloodType,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime createdAt
    ) {}

    public record PatientDetail(
            UUID id,
            String fullName,
            String email,
            String phoneNumber,
            LocalDate dateOfBirth,
            String nationalId,
            String bloodType,
            String address,
            double latitude,
            double longitude,
            String status,
            List<String> allergies,
            List<String> chronicConditions,
            EmergencyContactDto emergencyContact,
            List<MedicalRecordDto> medicalHistory,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record EmergencyContactDto(String name, String relationship, String phone) {}

    public record MedicalRecordDto(
            UUID recordId,
            UUID consultationId,
            String diagnosis,
            String prescription,
            String doctorName,
            LocalDateTime recordedAt,
            String notes
    ) {}
}
