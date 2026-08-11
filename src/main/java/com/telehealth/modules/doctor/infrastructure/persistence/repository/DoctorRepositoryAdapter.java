package com.telehealth.modules.doctor.infrastructure.persistence.repository;

import com.telehealth.modules.doctor.domain.model.ConsultationType;
import com.telehealth.modules.doctor.domain.model.Doctor;
import com.telehealth.modules.doctor.domain.model.Specialization;
import com.telehealth.modules.doctor.domain.repository.DoctorRepository;
import com.telehealth.modules.doctor.infrastructure.persistence.mapper.DoctorPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DoctorRepositoryAdapter implements DoctorRepository {

    private final DoctorJpaRepository jpaRepository;
    private final DoctorPersistenceMapper mapper;

    @Override public Doctor save(Doctor d) { return mapper.toDomain(jpaRepository.save(mapper.toEntity(d))); }
    @Override public Optional<Doctor> findById(UUID id) { return jpaRepository.findById(id).map(mapper::toDomain); }
    @Override public Optional<Doctor> findByEmail(String email) { return jpaRepository.findByEmailAndDeletedFalse(email).map(mapper::toDomain); }
    @Override public Optional<Doctor> findByLicenseNumber(String lic) { return jpaRepository.findByLicenseNumberAndDeletedFalse(lic).map(mapper::toDomain); }
    @Override public boolean existsByEmail(String email) { return jpaRepository.existsByEmailAndDeletedFalse(email); }
    @Override public boolean existsByLicenseNumber(String lic) { return jpaRepository.existsByLicenseNumberAndDeletedFalse(lic); }

    @Override
    public List<Doctor> findAvailableByType(ConsultationType type) {
        return jpaRepository.findAvailableByType(type.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Doctor> findAvailableByTypeAndSpecialization(ConsultationType type, Specialization spec) {
        return jpaRepository.findAvailableByTypeAndSpecialization(type.name(), spec.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Doctor> findEmergencyDoctorsNear(double lat, double lng, double radiusKm) {
        return jpaRepository.findEmergencyDoctorsNear(lat, lng, radiusKm).stream().map(mapper::toDomain).toList();
    }
}
