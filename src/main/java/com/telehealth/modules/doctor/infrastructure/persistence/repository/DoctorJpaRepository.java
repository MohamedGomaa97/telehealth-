package com.telehealth.modules.doctor.infrastructure.persistence.repository;

import com.telehealth.modules.doctor.infrastructure.persistence.entity.DoctorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorJpaRepository extends JpaRepository<DoctorJpaEntity, UUID> {

    Optional<DoctorJpaEntity> findByEmailAndDeletedFalse(String email);
    Optional<DoctorJpaEntity> findByLicenseNumberAndDeletedFalse(String licenseNumber);
    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByLicenseNumberAndDeletedFalse(String licenseNumber);

    @Query("SELECT d FROM DoctorJpaEntity d WHERE d.status = 'ACTIVE' AND d.deleted = false " +
           "AND (:type = 'HOME_VISIT' AND d.availableForHomeVisit = true " +
           "  OR :type = 'REMOTE' AND d.availableForRemote = true " +
           "  OR :type = 'EMERGENCY' AND d.availableForEmergency = true)")
    List<DoctorJpaEntity> findAvailableByType(@Param("type") String type);

    @Query("SELECT d FROM DoctorJpaEntity d WHERE d.status = 'ACTIVE' AND d.deleted = false " +
           "AND d.specialization = :spec " +
           "AND (:type = 'HOME_VISIT' AND d.availableForHomeVisit = true " +
           "  OR :type = 'REMOTE' AND d.availableForRemote = true " +
           "  OR :type = 'EMERGENCY' AND d.availableForEmergency = true)")
    List<DoctorJpaEntity> findAvailableByTypeAndSpecialization(@Param("type") String type, @Param("spec") String spec);

    // Haversine formula in PostgreSQL for geo proximity search
    @Query(value = """
        SELECT * FROM doctors d
        WHERE d.status = 'ACTIVE'
          AND d.deleted = false
          AND d.available_emergency = true
          AND (6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude))
               * cos(radians(d.longitude) - radians(:lng))
               + sin(radians(:lat)) * sin(radians(d.latitude)))) < :radiusKm
        ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude))
               * cos(radians(d.longitude) - radians(:lng))
               + sin(radians(:lat)) * sin(radians(d.latitude))))
        """, nativeQuery = true)
    List<DoctorJpaEntity> findEmergencyDoctorsNear(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm);
}
