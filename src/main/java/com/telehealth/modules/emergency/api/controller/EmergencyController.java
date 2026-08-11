package com.telehealth.modules.emergency.api.controller;

import com.telehealth.modules.emergency.application.commands.EmergencyCommand;
import com.telehealth.modules.emergency.application.handlers.EmergencyApplicationService;
import com.telehealth.modules.emergency.domain.model.Emergency;
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
@RequestMapping("/emergencies")
@RequiredArgsConstructor
@Tag(name = "Emergency Module", description = "SOS triggers, emergency dispatch, and resolution")
public class EmergencyController {

    private final EmergencyApplicationService applicationService;

    @PostMapping("/sos")
    @Operation(summary = "🚨 Trigger SOS emergency request")
    public ResponseEntity<Map<String, UUID>> triggerSos(
            @Valid @RequestBody EmergencyCommand.TriggerEmergency cmd) {
        UUID id = applicationService.handle(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("emergencyId", id));
    }

    @GetMapping("/{emergencyId}")
    @Operation(summary = "Get emergency by ID")
    public ResponseEntity<Emergency> getById(@PathVariable UUID emergencyId) {
        return ResponseEntity.ok(applicationService.getById(emergencyId));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active emergencies")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<List<Emergency>> getActive() {
        return ResponseEntity.ok(applicationService.getActiveEmergencies());
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get emergency history for a patient")
    public ResponseEntity<List<Emergency>> getByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(applicationService.getByPatient(patientId));
    }

    @PostMapping("/{emergencyId}/dispatch")
    @Operation(summary = "Dispatch a responder to the emergency")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Void> dispatch(
            @PathVariable UUID emergencyId,
            @Valid @RequestBody EmergencyCommand.DispatchResponder cmd) {
        applicationService.handle(new EmergencyCommand.DispatchResponder(
                emergencyId, cmd.responderId(), cmd.responderName(),
                cmd.responderPhone(), cmd.etaMinutes()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{emergencyId}/en-route")
    @Operation(summary = "Mark responder as en route")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> markEnRoute(@PathVariable UUID emergencyId) {
        applicationService.handle(new EmergencyCommand.MarkEnRoute(emergencyId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{emergencyId}/on-scene")
    @Operation(summary = "Mark responder as on scene")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> markOnScene(@PathVariable UUID emergencyId) {
        applicationService.handle(new EmergencyCommand.MarkOnScene(emergencyId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{emergencyId}/resolve")
    @Operation(summary = "Resolve the emergency")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> resolve(
            @PathVariable UUID emergencyId,
            @RequestBody Map<String, String> body) {
        applicationService.handle(new EmergencyCommand.ResolveEmergency(
                emergencyId, body.get("resolutionNotes")));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{emergencyId}/cancel")
    @Operation(summary = "Cancel a false alarm emergency")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID emergencyId,
            @RequestBody Map<String, String> body) {
        applicationService.handle(new EmergencyCommand.CancelEmergency(
                emergencyId, body.getOrDefault("reason", "False alarm")));
        return ResponseEntity.noContent().build();
    }
}
