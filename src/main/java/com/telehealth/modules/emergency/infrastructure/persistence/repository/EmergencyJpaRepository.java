package com.telehealth.modules.emergency.infrastructure.persistence.repository;

import com.telehealth.modules.emergency.infrastructure.persistence.entity.EmergencyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EmergencyJpaRepository extends JpaRepository<EmergencyJpaEntity, UUID> {

    List<EmergencyJpaEntity> findByPatientIdAndDeletedFalse(UUID patientId);

    List<EmergencyJpaEntity> findByStatusAndDeletedFalse(String status);

    @Query("SELECT e FROM EmergencyJpaEntity e WHERE e.deleted = false AND e.status NOT IN ('RESOLVED','CANCELLED')")
    List<EmergencyJpaEntity> findActive();

    @Query("SELECT COUNT(e) > 0 FROM EmergencyJpaEntity e WHERE e.patientId = :patientId AND e.deleted = false AND e.status NOT IN ('RESOLVED','CANCELLED')")
    boolean hasActiveEmergency(UUID patientId);
}
