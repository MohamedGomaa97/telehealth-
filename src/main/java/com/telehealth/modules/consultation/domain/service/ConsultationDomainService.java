package com.telehealth.modules.consultation.domain.service;

import com.telehealth.modules.consultation.domain.model.Consultation;
import com.telehealth.modules.consultation.domain.model.ConsultationStatus;
import com.telehealth.modules.consultation.domain.repository.ConsultationRepository;
import com.telehealth.shared.exceptions.TeleHealthException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultationDomainService {

    private final ConsultationRepository consultationRepository;

    public ConsultationDomainService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    public void validatePatientHasNoActiveConsultation(UUID patientId) {
        boolean hasActive = consultationRepository.findByPatientId(patientId).stream()
                .anyMatch(c -> c.getStatus() == ConsultationStatus.PENDING
                        || c.getStatus() == ConsultationStatus.ASSIGNED
                        || c.getStatus() == ConsultationStatus.IN_PROGRESS);
        if (hasActive) {
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Patient already has an active consultation. Complete or cancel it first.");
        }
    }

    public void validateDoctorNotOverloaded(UUID doctorId) {
        long active = consultationRepository.countActiveByDoctorId(doctorId);
        if (active >= 3) {
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Doctor has reached maximum concurrent consultations (3)");
        }
    }

    public Consultation getOrThrow(UUID consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Consultation", consultationId));
    }
}
