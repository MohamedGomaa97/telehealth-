package com.telehealth.modules.consultation.domain.repository;

import com.telehealth.modules.consultation.domain.model.Consultation;
import com.telehealth.modules.consultation.domain.model.ConsultationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepository {
    Consultation save(Consultation consultation);
    Optional<Consultation> findById(UUID id);
    List<Consultation> findByPatientId(UUID patientId);
    List<Consultation> findByDoctorId(UUID doctorId);
    List<Consultation> findByStatus(ConsultationStatus status);
    List<Consultation> findPendingByUrgency();
    long countActiveByDoctorId(UUID doctorId);
}
