package com.telehealth.modules.patient.infrastructure.persistence.repository;

import com.telehealth.modules.patient.infrastructure.persistence.entity.PatientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository - Infrastructure Layer
 * Lives inside infrastructure, invisible to domain.
 */
public interface PatientJpaRepository extends JpaRepository<PatientJpaEntity, UUID> {

    Optional<PatientJpaEntity> findByEmailAndDeletedFalse(String email);

    Optional<PatientJpaEntity> findByNationalIdAndDeletedFalse(String nationalId);

    Optional<PatientJpaEntity> findByPhoneNumberAndDeletedFalse(String phoneNumber);

    List<PatientJpaEntity> findByStatusAndDeletedFalse(String status);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByNationalIdAndDeletedFalse(String nationalId);

    @Query(value = """
            SELECT * FROM patients p
            WHERE p.deleted = false
              AND p.chronic_conditions @> CAST(:condition AS jsonb)
            """, nativeQuery = true)
    List<PatientJpaEntity> findByChronicCondition(@Param("condition") String conditionJson);
}
