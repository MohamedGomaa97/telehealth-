package com.telehealth.modules.patient.domain.service;

import com.telehealth.modules.patient.domain.model.Patient;
import com.telehealth.modules.patient.domain.repository.PatientRepository;
import com.telehealth.shared.exceptions.TeleHealthException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Patient Domain Service
 *
 * Handles domain logic that doesn't naturally belong to a single entity.
 * This is different from Application Services — domain services contain
 * pure business logic with no infrastructure concerns.
 */
@Service
public class PatientDomainService {

    private final PatientRepository patientRepository;

    public PatientDomainService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Validates that a patient can be registered — no duplicate email or national ID.
     */
    public void validateUniquePatient(String email, String nationalId) {
        if (patientRepository.existsByEmail(email)) {
            throw new TeleHealthException.ConflictException(
                    "A patient with email '" + email + "' already exists");
        }
        if (patientRepository.existsByNationalId(nationalId)) {
            throw new TeleHealthException.ConflictException(
                    "A patient with national ID '" + nationalId + "' already exists");
        }
    }

    /**
     * Validates that a patient is eligible to request a consultation.
     * Business rule: patient must be ACTIVE and have a verified phone number.
     */
    public void validateEligibilityForConsultation(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Patient", patientId));

        if (!patient.isActive()) {
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Patient " + patientId + " is not active and cannot request consultations");
        }
    }

    /**
     * Business rule: Emergency SOS requires patient to have a registered location.
     */
    public void validateEmergencyEligibility(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Patient", patientId));

        if (!patient.isActive()) {
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Inactive patient cannot trigger emergency");
        }

        if (!patient.hasLocation()) {
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Patient must have a registered location to use emergency services. Please update your location.");
        }
    }
}
