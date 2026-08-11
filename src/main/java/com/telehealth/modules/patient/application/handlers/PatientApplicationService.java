package com.telehealth.modules.patient.application.handlers;

import com.telehealth.modules.auth.application.commands.AuthCommand;
import com.telehealth.modules.auth.application.handlers.AuthApplicationService;
import com.telehealth.modules.auth.domain.model.UserRole;
import com.telehealth.modules.patient.application.commands.PatientCommand;
import com.telehealth.modules.patient.application.queries.PatientQuery;
import com.telehealth.modules.patient.domain.model.*;
import com.telehealth.modules.patient.domain.repository.PatientRepository;
import com.telehealth.modules.patient.domain.service.PatientDomainService;
import com.telehealth.modules.patient.infrastructure.messaging.PatientEventPublisher;
import com.telehealth.shared.exceptions.TeleHealthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Patient Application Service
 *
 * This is the Application Layer — it orchestrates:
 * 1. Domain validation via Domain Services
 * 2. Domain model manipulation
 * 3. Persistence via Repository
 * 4. Event publishing via RabbitMQ
 *
 * No business logic lives here — that belongs in the domain.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientApplicationService {

    private final PatientRepository patientRepository;
    private final PatientDomainService patientDomainService;
    private final PatientEventPublisher eventPublisher;
    private final AuthApplicationService authApplicationService;

    // ─── Command Handlers ──────────────────────────────────────────────────────

    public UUID handle(PatientCommand.RegisterPatient cmd) {
        log.info("Registering new patient: {}", cmd.email());

        // Domain service validates business rules
        patientDomainService.validateUniquePatient(cmd.email(), cmd.nationalId());

        // Domain model creation (business logic in domain)
        Patient patient = Patient.register(
                cmd.fullName(), cmd.email(), cmd.phoneNumber(),
                cmd.dateOfBirth(), cmd.nationalId(), cmd.bloodType(), cmd.address()
        );

        // Persistence
        Patient saved = patientRepository.save(patient);

        // Create login credentials linked to this patient, in the same transaction
        authApplicationService.createCredentials(
                new AuthCommand.CreateCredentials(cmd.email(), cmd.password(), UserRole.PATIENT, saved.getId()));

        // Publish domain event (async via RabbitMQ)
        eventPublisher.publishPatientRegistered(saved);

        log.info("Patient registered successfully with id: {}", saved.getId());
        return saved.getId();
    }

    public void handle(PatientCommand.UpdatePatientLocation cmd) {
        Patient patient = getActivePatient(cmd.patientId());
        patient.updateLocation(cmd.latitude(), cmd.longitude());
        patientRepository.save(patient);
        log.debug("Location updated for patient: {}", cmd.patientId());
    }

    public void handle(PatientCommand.AddMedicalRecord cmd) {
        Patient patient = getActivePatient(cmd.patientId());
        MedicalRecord record = MedicalRecord.create(
                cmd.consultationId(), cmd.diagnosis(),
                cmd.prescription(), cmd.doctorName(), cmd.notes()
        );
        patient.addMedicalRecord(record);
        patientRepository.save(patient);
        log.info("Medical record added for patient: {}", cmd.patientId());
    }

    public void handle(PatientCommand.AddAllergy cmd) {
        Patient patient = getActivePatient(cmd.patientId());
        patient.addAllergy(cmd.allergy());
        patientRepository.save(patient);
    }

    public void handle(PatientCommand.SetEmergencyContact cmd) {
        Patient patient = getActivePatient(cmd.patientId());
        EmergencyContact contact = new EmergencyContact(
                cmd.contactName(), cmd.relationship(), cmd.contactPhone()
        );
        patient.setEmergencyContact(contact);
        patientRepository.save(patient);
    }

    public void handle(PatientCommand.SuspendPatient cmd) {
        Patient patient = getActivePatient(cmd.patientId());
        patient.suspend(cmd.reason());
        patientRepository.save(patient);
        log.warn("Patient suspended: {} - reason: {}", cmd.patientId(), cmd.reason());
    }

    public void handle(PatientCommand.ReactivatePatient cmd) {
        Patient patient = patientRepository.findById(cmd.patientId())
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Patient", cmd.patientId()));
        patient.reactivate();
        patientRepository.save(patient);
    }

    // ─── Query Handlers ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Patient handle(PatientQuery.GetPatientById query) {
        return patientRepository.findById(query.patientId())
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Patient", query.patientId()));
    }

    @Transactional(readOnly = true)
    public Patient handle(PatientQuery.GetPatientByEmail query) {
        return patientRepository.findByEmail(query.email())
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Patient", query.email()));
    }

    @Transactional(readOnly = true)
    public List<Patient> handle(PatientQuery.GetAllActivePatients query) {
        return patientRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public List<Patient> handle(PatientQuery.GetPatientsByCondition query) {
        return patientRepository.findByChronicCondition(query.condition());
    }

    // ─── Private Helpers ───────────────────────────────────────────────────────

    private Patient getActivePatient(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Patient", patientId));
        if (!patient.isActive()) {
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Operation not allowed for inactive patient: " + patientId);
        }
        return patient;
    }
}
