package com.telehealth.modules.consultation.infrastructure.persistence.repository;

import com.telehealth.modules.consultation.infrastructure.persistence.entity.ConsultationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConsultationJpaRepository extends JpaRepository<ConsultationJpaEntity, UUID> {

    List<ConsultationJpaEntity> findByPatientIdAndDeletedFalse(UUID patientId);
    List<ConsultationJpaEntity> findByDoctorIdAndDeletedFalse(UUID doctorId);
    List<ConsultationJpaEntity> findByStatusAndDeletedFalse(String status);

    @Query("SELECT COUNT(c) FROM ConsultationJpaEntity c WHERE c.doctorId = :doctorId AND c.status IN ('ASSIGNED','IN_PROGRESS') AND c.deleted = false")
    long countActiveByDoctorId(UUID doctorId);

    @Query("SELECT c FROM ConsultationJpaEntity c WHERE c.status = 'PENDING' AND c.deleted = false ORDER BY CASE c.urgencyLevel WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 ELSE 4 END")
    List<ConsultationJpaEntity> findPendingByUrgency();
}
