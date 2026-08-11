package com.telehealth.modules.patient.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telehealth.modules.patient.domain.model.Patient;
import com.telehealth.modules.patient.domain.repository.PatientRepository;
import com.telehealth.modules.patient.infrastructure.persistence.entity.PatientJpaEntity;
import com.telehealth.modules.patient.infrastructure.persistence.mapper.PatientPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Patient Repository Adapter - Infrastructure Layer
 *
 * This class implements the domain's PatientRepository interface.
 * It adapts between domain model and JPA entity.
 *
 * The domain defines the CONTRACT (interface).
 * Infrastructure provides the IMPLEMENTATION (adapter).
 * This is the Ports & Adapters / Hexagonal architecture pattern.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepository {

    private final PatientJpaRepository jpaRepository;
    private final PatientPersistenceMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public Patient save(Patient patient) {
        PatientJpaEntity entity = mapper.toEntity(patient);
        PatientJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return jpaRepository.findByEmailAndDeletedFalse(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByNationalId(String nationalId) {
        return jpaRepository.findByNationalIdAndDeletedFalse(nationalId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByPhoneNumber(String phone) {
        return jpaRepository.findByPhoneNumberAndDeletedFalse(phone)
                .map(mapper::toDomain);
    }

    @Override
    public List<Patient> findAllActive() {
        return jpaRepository.findByStatusAndDeletedFalse("ACTIVE")
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Patient> findByChronicCondition(String condition) {
        try {
            String conditionJson = objectMapper.writeValueAsString(List.of(condition));
            return jpaRepository.findByChronicCondition(conditionJson)
                    .stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (Exception e) {
            log.error("Error querying by chronic condition: {}", condition, e);
            return List.of();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmailAndDeletedFalse(email);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return jpaRepository.existsByNationalIdAndDeletedFalse(nationalId);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            jpaRepository.save(entity);
        });
    }
}
