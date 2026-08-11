package com.telehealth.modules.doctor.infrastructure.persistence.mapper;

import com.telehealth.modules.doctor.domain.model.*;
import com.telehealth.modules.doctor.infrastructure.persistence.entity.DoctorJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorPersistenceMapper {

    public DoctorJpaEntity toEntity(Doctor d) {
        DoctorJpaEntity e = new DoctorJpaEntity();
        e.setId(d.getId());
        e.setFullName(d.getFullName());
        e.setEmail(d.getEmail());
        e.setPhoneNumber(d.getPhoneNumber());
        e.setLicenseNumber(d.getLicenseNumber());
        e.setSpecialization(d.getSpecialization().name());
        e.setSubSpecializations(List.copyOf(d.getSubSpecializations()));
        e.setStatus(d.getStatus().name());
        e.setAvailableForHomeVisit(d.isAvailableForHomeVisit());
        e.setAvailableForRemote(d.isAvailableForRemote());
        e.setAvailableForEmergency(d.isAvailableForEmergency());
        e.setLatitude(d.getLatitude());
        e.setLongitude(d.getLongitude());
        e.setCoverageZone(d.getCoverageZone());
        e.setRating(d.getRating());
        e.setTotalConsultations(d.getTotalConsultations());
        e.setDeleted(d.isDeleted());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }

    public Doctor toDomain(DoctorJpaEntity e) {
        Doctor d = Doctor.register(e.getFullName(), e.getEmail(), e.getPhoneNumber(),
                e.getLicenseNumber(), Specialization.valueOf(e.getSpecialization()));
        d.setId(e.getId());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        d.setStatus(DoctorStatus.valueOf(e.getStatus()));
        d.setAvailableForHomeVisit(e.isAvailableForHomeVisit());
        d.setAvailableForRemote(e.isAvailableForRemote());
        d.setAvailableForEmergency(e.isAvailableForEmergency());
        d.setLatitude(e.getLatitude());
        d.setLongitude(e.getLongitude());
        d.setCoverageZone(e.getCoverageZone());
        d.setRating(e.getRating());
        d.setTotalConsultations(e.getTotalConsultations());
        d.setSubSpecializations(e.getSubSpecializations() != null ? e.getSubSpecializations() : List.of());
        return d;
    }
}
