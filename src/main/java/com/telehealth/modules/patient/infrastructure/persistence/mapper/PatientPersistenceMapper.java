package com.telehealth.modules.patient.infrastructure.persistence.mapper;

import com.telehealth.modules.patient.domain.model.*;
import com.telehealth.modules.patient.infrastructure.persistence.entity.PatientJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps between Patient domain model and PatientJpaEntity.
 * Keeps domain clean from persistence annotations.
 */
@Component
public class PatientPersistenceMapper {

    public PatientJpaEntity toEntity(Patient domain) {
        PatientJpaEntity entity = new PatientJpaEntity();
        entity.setId(domain.getId());
        entity.setFullName(domain.getFullName());
        entity.setEmail(domain.getEmail());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setDateOfBirth(domain.getDateOfBirth());
        entity.setNationalId(domain.getNationalId());
        entity.setBloodType(domain.getBloodType() != null ? domain.getBloodType().name() : null);
        entity.setAddress(domain.getAddress());
        entity.setLatitude(domain.getLatitude());
        entity.setLongitude(domain.getLongitude());
        entity.setStatus(domain.getStatus().name());
        entity.setDeleted(domain.isDeleted());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getEmergencyContact() != null) {
            entity.setEmergencyContact(new PatientJpaEntity.EmergencyContactJson(
                    domain.getEmergencyContact().name(),
                    domain.getEmergencyContact().relationship(),
                    domain.getEmergencyContact().phoneNumber()
            ));
        }

        entity.setAllergies(List.copyOf(domain.getAllergies()));
        entity.setChronicConditions(List.copyOf(domain.getChronicConditions()));
        entity.setMedicalHistory(domain.getMedicalHistory().stream()
                .map(r -> new PatientJpaEntity.MedicalRecordJson(
                        r.recordId(), r.consultationId(), r.diagnosis(),
                        r.prescription(), r.doctorName(), r.recordedAt(), r.notes()))
                .toList());

        return entity;
    }

    public Patient toDomain(PatientJpaEntity entity) {
        Patient patient = Patient.register(
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getDateOfBirth(),
                entity.getNationalId(),
                entity.getBloodType() != null ? BloodType.valueOf(entity.getBloodType()) : null,
                entity.getAddress()
        );

        patient.setId(entity.getId());
        patient.setCreatedAt(entity.getCreatedAt());
        patient.setUpdatedAt(entity.getUpdatedAt());
        patient.setStatus(PatientStatus.valueOf(entity.getStatus()));
        patient.setLatitude(entity.getLatitude());
        patient.setLongitude(entity.getLongitude());

        if (entity.getEmergencyContact() != null) {
            var ec = entity.getEmergencyContact();
            patient.setEmergencyContact(new EmergencyContact(ec.name(), ec.relationship(), ec.phoneNumber()));
        }

        patient.setAllergies(entity.getAllergies() != null ? entity.getAllergies() : List.of());
        patient.setChronicConditions(entity.getChronicConditions() != null ? entity.getChronicConditions() : List.of());

        if (entity.getMedicalHistory() != null) {
            patient.setMedicalHistory(entity.getMedicalHistory().stream()
                    .map(r -> new MedicalRecord(
                            r.recordId(), r.consultationId(), r.diagnosis(),
                            r.prescription(), r.doctorName(), r.recordedAt(), r.notes()))
                    .toList());
        }

        return patient;
    }
}
