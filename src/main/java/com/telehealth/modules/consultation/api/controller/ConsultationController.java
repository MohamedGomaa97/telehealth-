package com.telehealth.modules.consultation.api.controller;

import com.telehealth.modules.consultation.application.commands.ConsultationCommand;
import com.telehealth.modules.consultation.application.handlers.ConsultationApplicationService;
import com.telehealth.modules.consultation.application.queries.ConsultationQuery;
import com.telehealth.modules.consultation.domain.model.Consultation;
import com.telehealth.modules.consultation.domain.model.ConsultationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/consultations")
@RequiredArgsConstructor
@Tag(name = "Consultation Module", description = "Home visits, remote consultations, and scheduling")
public class ConsultationController {

    private final ConsultationApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Request a new consultation (home visit or remote)")
    public ResponseEntity<Map<String, UUID>> request(
            @Valid @RequestBody ConsultationCommand.RequestConsultation cmd) {
        UUID id = applicationService.handle(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("consultationId", id));
    }

    @GetMapping("/{consultationId}")
    @Operation(summary = "Get consultation by ID")
    public ResponseEntity<Consultation> getById(@PathVariable UUID consultationId) {
        return ResponseEntity.ok(applicationService.handle(new ConsultationQuery.GetConsultationById(consultationId)));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get all consultations for a patient")
    public ResponseEntity<List<Consultation>> getByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(applicationService.handle(new ConsultationQuery.GetConsultationsByPatient(patientId)));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get all consultations for a doctor")
    public ResponseEntity<List<Consultation>> getByDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(applicationService.handle(new ConsultationQuery.GetConsultationsByDoctor(doctorId)));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending consultations (ordered by urgency)")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Consultation>> getPending() {
        return ResponseEntity.ok(applicationService.handle(new ConsultationQuery.GetPendingConsultations(null)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get consultations by status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Consultation>> getByStatus(@PathVariable ConsultationStatus status) {
        return ResponseEntity.ok(applicationService.handle(new ConsultationQuery.GetConsultationsByStatus(status)));
    }

    @PostMapping("/{consultationId}/assign")
    @Operation(summary = "Assign a doctor to a consultation")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> assign(
            @PathVariable UUID consultationId,
            @RequestBody Map<String, String> body) {
        applicationService.handle(new ConsultationCommand.AssignDoctor(
                consultationId,
                UUID.fromString(body.get("doctorId")),
                body.get("scheduledAt")
        ));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{consultationId}/start")
    @Operation(summary = "Start a consultation (doctor begins session)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> start(@PathVariable UUID consultationId) {
        applicationService.handle(new ConsultationCommand.StartConsultation(consultationId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{consultationId}/complete")
    @Operation(summary = "Complete a consultation with diagnosis and prescription")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> complete(
            @PathVariable UUID consultationId,
            @Valid @RequestBody ConsultationCommand.CompleteConsultation cmd) {
        applicationService.handle(new ConsultationCommand.CompleteConsultation(
                consultationId, cmd.diagnosis(), cmd.prescription(), cmd.doctorNotes()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{consultationId}/cancel")
    @Operation(summary = "Cancel a consultation")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID consultationId,
            @RequestBody Map<String, String> body) {
        applicationService.handle(new ConsultationCommand.CancelConsultation(
                consultationId, body.getOrDefault("reason", "Cancelled by user")));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{consultationId}/rate")
    @Operation(summary = "Rate a completed consultation (patient only)")
    public ResponseEntity<Void> rate(
            @PathVariable UUID consultationId,
            @Valid @RequestBody ConsultationCommand.RateConsultation cmd) {
        applicationService.handle(new ConsultationCommand.RateConsultation(
                consultationId, cmd.patientId(), cmd.rating()));
        return ResponseEntity.noContent().build();
    }
}
