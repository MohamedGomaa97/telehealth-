package com.telehealth.modules.emergency.infrastructure.persistence.repository;

import com.telehealth.modules.emergency.domain.model.Emergency;
import com.telehealth.modules.emergency.domain.model.EmergencyStatus;
import com.telehealth.modules.emergency.domain.repository.EmergencyRepository;
import com.telehealth.modules.emergency.infrastructure.persistence.mapper.EmergencyPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EmergencyRepositoryAdapter implements EmergencyRepository {

    private final EmergencyJpaRepository jpa;
    private final EmergencyPersistenceMapper mapper;

    @Override public Emergency save(Emergency e) {
        return mapper.toDomain(jpa.save(mapper.toEntity(e)));
    }
    @Override public Optional<Emergency> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }
    @Override public List<Emergency> findByPatientId(UUID patientId) {
        return jpa.findByPatientIdAndDeletedFalse(patientId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<Emergency> findActive() {
        return jpa.findActive().stream().map(mapper::toDomain).toList();
    }
    @Override public List<Emergency> findByStatus(EmergencyStatus status) {
        return jpa.findByStatusAndDeletedFalse(status.name()).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean hasActiveEmergency(UUID patientId) {
        return jpa.hasActiveEmergency(patientId);
    }
}
