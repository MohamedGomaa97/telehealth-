package com.telehealth.modules.patient.api.controller;

import com.telehealth.modules.patient.api.dto.PatientRequest;
import com.telehealth.modules.patient.api.dto.PatientResponse;
import com.telehealth.modules.patient.api.mapper.PatientApiMapper;
import com.telehealth.modules.patient.application.commands.PatientCommand;
import com.telehealth.modules.patient.application.handlers.PatientApplicationService;
import com.telehealth.modules.patient.application.queries.PatientQuery;
import com.telehealth.modules.patient.domain.model.Patient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Module", description = "Patient registration, profiles, and medical history management")
public class PatientController {

    private final PatientApplicationService applicationService;
    private final PatientApiMapper apiMapper;

    @PostMapping
    @Operation(summary = "Register a new patient")
    public ResponseEntity<PatientResponse.PatientSummary> register(
            @Valid @RequestBody PatientRequest.RegisterPatientRequest request) {
        PatientCommand.RegisterPatient cmd = apiMapper.toCommand(request);
        UUID patientId = applicationService.handle(cmd);
        Patient patient = applicationService.handle(new PatientQuery.GetPatientById(patientId));
        return ResponseEntity.status(HttpStatus.CREATED).body(apiMapper.toSummary(patient));
    }

    @GetMapping("/{patientId}")
    @Operation(summary = "Get patient by ID")
    @PreAuthorize("hasRole('DOCTOR') or @patientSecurity.isSelf(#patientId)")
    public ResponseEntity<PatientResponse.PatientDetail> getById(@PathVariable UUID patientId) {
        Patient patient = applicationService.handle(new PatientQuery.GetPatientById(patientId));
        return ResponseEntity.ok(apiMapper.toDetail(patient));
    }

    @GetMapping
    @Operation(summary = "Get all active patients")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<List<PatientResponse.PatientSummary>> getAllActive() {
        List<Patient> patients = applicationService.handle(new PatientQuery.GetAllActivePatients());
        return ResponseEntity.ok(patients.stream().map(apiMapper::toSummary).toList());
    }

    @PatchMapping("/{patientId}/location")
    @Operation(summary = "Update patient GPS location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable UUID patientId,
            @Valid @RequestBody PatientRequest.UpdateLocationRequest request) {
        applicationService.handle(new PatientCommand.UpdatePatientLocation(
                patientId, request.latitude(), request.longitude()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{patientId}/allergies")
    @Operation(summary = "Add allergy to patient profile")
    public ResponseEntity<Void> addAllergy(
            @PathVariable UUID patientId,
            @Valid @RequestBody PatientRequest.AddAllergyRequest request) {
        applicationService.handle(new PatientCommand.AddAllergy(patientId, request.allergy()));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{patientId}/emergency-contact")
    @Operation(summary = "Set emergency contact")
    public ResponseEntity<Void> setEmergencyContact(
            @PathVariable UUID patientId,
            @Valid @RequestBody PatientRequest.EmergencyContactRequest request) {
        applicationService.handle(new PatientCommand.SetEmergencyContact(
                patientId, request.name(), request.relationship(), request.phone()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{patientId}/suspend")
    @Operation(summary = "Suspend patient account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> suspend(
            @PathVariable UUID patientId,
            @RequestParam String reason) {
        applicationService.handle(new PatientCommand.SuspendPatient(patientId, reason));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{patientId}/reactivate")
    @Operation(summary = "Reactivate patient account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reactivate(@PathVariable UUID patientId) {
        applicationService.handle(new PatientCommand.ReactivatePatient(patientId));
        return ResponseEntity.noContent().build();
    }
}
