package com.telehealth.modules.consultation.infrastructure.persistence.repository;

import com.telehealth.modules.consultation.domain.model.Consultation;
import com.telehealth.modules.consultation.domain.model.ConsultationStatus;
import com.telehealth.modules.consultation.domain.repository.ConsultationRepository;
import com.telehealth.modules.consultation.infrastructure.persistence.mapper.ConsultationPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsultationRepositoryAdapter implements ConsultationRepository {

    private final ConsultationJpaRepository jpa;
    private final ConsultationPersistenceMapper mapper;

    @Override public Consultation save(Consultation c) {
        return mapper.toDomain(jpa.save(mapper.toEntity(c)));
    }
    @Override public Optional<Consultation> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }
    @Override public List<Consultation> findByPatientId(UUID patientId) {
        return jpa.findByPatientIdAndDeletedFalse(patientId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<Consultation> findByDoctorId(UUID doctorId) {
        return jpa.findByDoctorIdAndDeletedFalse(doctorId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<Consultation> findByStatus(ConsultationStatus status) {
        return jpa.findByStatusAndDeletedFalse(status.name()).stream().map(mapper::toDomain).toList();
    }
    @Override public List<Consultation> findPendingByUrgency() {
        return jpa.findPendingByUrgency().stream().map(mapper::toDomain).toList();
    }
    @Override public long countActiveByDoctorId(UUID doctorId) {
        return jpa.countActiveByDoctorId(doctorId);
    }
}
