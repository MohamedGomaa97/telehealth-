package com.telehealth.modules.doctor.application.handlers;

import com.telehealth.modules.auth.application.commands.AuthCommand;
import com.telehealth.modules.auth.application.handlers.AuthApplicationService;
import com.telehealth.modules.auth.domain.model.UserRole;
import com.telehealth.modules.doctor.application.commands.DoctorCommand;
import com.telehealth.modules.doctor.application.queries.DoctorQuery;
import com.telehealth.modules.doctor.domain.model.Doctor;
import com.telehealth.modules.doctor.domain.repository.DoctorRepository;
import com.telehealth.modules.doctor.domain.service.DoctorDomainService;
import com.telehealth.modules.doctor.infrastructure.messaging.DoctorEventPublisher;
import com.telehealth.shared.exceptions.TeleHealthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoctorApplicationService {

    private final DoctorRepository doctorRepository;
    private final DoctorDomainService doctorDomainService;
    private final DoctorEventPublisher eventPublisher;
    private final AuthApplicationService authApplicationService;

    public UUID handle(DoctorCommand.RegisterDoctor cmd) {
        log.info("Registering doctor: {} - {}", cmd.fullName(), cmd.licenseNumber());
        doctorDomainService.validateUniqueDoctor(cmd.email(), cmd.licenseNumber());
        Doctor doctor = Doctor.register(cmd.fullName(), cmd.email(), cmd.phoneNumber(),
                cmd.licenseNumber(), cmd.specialization());
        Doctor saved = doctorRepository.save(doctor);

        authApplicationService.createCredentials(
                new AuthCommand.CreateCredentials(cmd.email(), cmd.password(), UserRole.DOCTOR, saved.getId()));

        log.info("Doctor registered: {}", saved.getId());
        return saved.getId();
    }

    public void handle(DoctorCommand.VerifyDoctor cmd) {
        Doctor doctor = getDoctor(cmd.doctorId());
        doctor.verify();
        doctorRepository.save(doctor);
        log.info("Doctor verified: {}", cmd.doctorId());
    }

    public void handle(DoctorCommand.SetAvailability cmd) {
        Doctor doctor = getDoctor(cmd.doctorId());
        doctor.setAvailability(cmd.homeVisit(), cmd.remote(), cmd.emergency());
        doctorRepository.save(doctor);
        eventPublisher.publishAvailabilityChanged(doctor);
        log.debug("Availability updated for doctor: {}", cmd.doctorId());
    }

    public void handle(DoctorCommand.UpdateLocation cmd) {
        Doctor doctor = getDoctor(cmd.doctorId());
        doctor.updateLocation(cmd.latitude(), cmd.longitude(), cmd.coverageZone());
        doctorRepository.save(doctor);
    }

    public void handle(DoctorCommand.GoOffDuty cmd) {
        Doctor doctor = getDoctor(cmd.doctorId());
        doctor.goOffDuty();
        doctorRepository.save(doctor);
        eventPublisher.publishAvailabilityChanged(doctor);
    }

    public void handle(DoctorCommand.GoOnDuty cmd) {
        Doctor doctor = getDoctor(cmd.doctorId());
        doctor.goOnDuty();
        doctorRepository.save(doctor);
    }

    public void handle(DoctorCommand.SuspendDoctor cmd) {
        Doctor doctor = getDoctor(cmd.doctorId());
        doctor.suspend(cmd.reason());
        doctorRepository.save(doctor);
        log.warn("Doctor suspended: {}", cmd.doctorId());
    }

    public void handle(DoctorCommand.RecordConsultationRating cmd) {
        if (cmd.rating() < 1.0 || cmd.rating() > 5.0)
            throw new TeleHealthException.BusinessRuleViolationException("Rating must be between 1 and 5");
        Doctor doctor = getDoctor(cmd.doctorId());
        doctor.recordConsultation(cmd.rating());
        doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public Doctor handle(DoctorQuery.GetDoctorById query) {
        return getDoctor(query.doctorId());
    }

    @Transactional(readOnly = true)
    public List<Doctor> handle(DoctorQuery.GetAvailableDoctors query) {
        return doctorRepository.findAvailableByType(query.type());
    }

    @Transactional(readOnly = true)
    public List<Doctor> handle(DoctorQuery.GetDoctorsBySpecialization query) {
        return doctorRepository.findAvailableByTypeAndSpecialization(query.type(), query.specialization());
    }

    private Doctor getDoctor(UUID id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Doctor", id));
    }
}
