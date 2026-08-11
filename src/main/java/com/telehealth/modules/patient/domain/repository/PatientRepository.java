package com.telehealth.modules.patient.domain.repository;

import com.telehealth.modules.patient.domain.model.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Patient Repository - Domain Port (Interface)
 *
 * This interface lives in the DOMAIN layer.
 * The implementation lives in the INFRASTRUCTURE layer.
 * This is the "Dependency Inversion" principle of Clean Architecture.
 *
 * The domain never depends on infrastructure — infrastructure adapts to domain.
 */
public interface PatientRepository {

    Patient save(Patient patient);

    Optional<Patient> findById(UUID id);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByNationalId(String nationalId);

    Optional<Patient> findByPhoneNumber(String phoneNumber);

    List<Patient> findAllActive();

    List<Patient> findByChronicCondition(String condition);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    void delete(UUID id);
}
