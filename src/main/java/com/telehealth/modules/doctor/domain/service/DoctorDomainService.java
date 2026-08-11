package com.telehealth.modules.doctor.domain.service;

import com.telehealth.modules.doctor.domain.model.ConsultationType;
import com.telehealth.modules.doctor.domain.model.Doctor;
import com.telehealth.modules.doctor.domain.model.Specialization;
import com.telehealth.modules.doctor.domain.repository.DoctorRepository;
import com.telehealth.shared.exceptions.TeleHealthException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Doctor Domain Service - handles multi-entity business logic.
 * Doctor matching algorithm lives here (pure domain logic).
 */
@Service
public class DoctorDomainService {

    private final DoctorRepository doctorRepository;

    public DoctorDomainService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public void validateUniqueDoctor(String email, String licenseNumber) {
        if (doctorRepository.existsByEmail(email))
            throw new TeleHealthException.ConflictException("Doctor with email '" + email + "' already exists");
        if (doctorRepository.existsByLicenseNumber(licenseNumber))
            throw new TeleHealthException.ConflictException("Doctor with license '" + licenseNumber + "' already exists");
    }

    /**
     * Business rule: Find best available doctor for a consultation.
     * Priority: Specialization match → Rating → Fewest active consultations
     */
    public Doctor findBestAvailableDoctor(ConsultationType type, Specialization requiredSpec) {
        List<Doctor> candidates = doctorRepository.findAvailableByTypeAndSpecialization(type, requiredSpec);

        if (candidates.isEmpty()) {
            // Fallback: any available doctor of that type
            candidates = doctorRepository.findAvailableByType(type);
        }

        if (candidates.isEmpty())
            throw new TeleHealthException.BusinessRuleViolationException(
                    "No available doctors for " + type + " consultation right now");

        return candidates.stream()
                .max(Comparator.comparingDouble(Doctor::getRating))
                .orElseThrow();
    }

    /**
     * Find nearest emergency doctor within radius.
     */
    public Doctor findNearestEmergencyDoctor(double patientLat, double patientLng) {
        List<Doctor> nearby = doctorRepository.findEmergencyDoctorsNear(patientLat, patientLng, 20.0);

        if (nearby.isEmpty())
            throw new TeleHealthException.BusinessRuleViolationException(
                    "No emergency doctors available within 20km of your location");

        return nearby.stream()
                .min(Comparator.comparingDouble(d -> haversineDistance(patientLat, patientLng, d.getLatitude(), d.getLongitude())))
                .orElseThrow();
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
