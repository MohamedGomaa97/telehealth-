package com.telehealth.modules.doctor.domain.repository;

import com.telehealth.modules.doctor.domain.model.ConsultationType;
import com.telehealth.modules.doctor.domain.model.Doctor;
import com.telehealth.modules.doctor.domain.model.Specialization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository {
    Doctor save(Doctor doctor);
    Optional<Doctor> findById(UUID id);
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    List<Doctor> findAvailableByType(ConsultationType type);
    List<Doctor> findAvailableByTypeAndSpecialization(ConsultationType type, Specialization specialization);
    List<Doctor> findEmergencyDoctorsNear(double lat, double lng, double radiusKm);
    boolean existsByEmail(String email);
    boolean existsByLicenseNumber(String licenseNumber);
}
