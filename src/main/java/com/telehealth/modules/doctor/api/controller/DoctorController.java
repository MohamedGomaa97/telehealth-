package com.telehealth.modules.doctor.api.controller;

import com.telehealth.modules.doctor.application.commands.DoctorCommand;
import com.telehealth.modules.doctor.application.handlers.DoctorApplicationService;
import com.telehealth.modules.doctor.application.queries.DoctorQuery;
import com.telehealth.modules.doctor.domain.model.ConsultationType;
import com.telehealth.modules.doctor.domain.model.Doctor;
import com.telehealth.modules.doctor.domain.model.Specialization;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Module", description = "Doctor registration, availability, and profile management")
public class DoctorController {

    private final DoctorApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Register a new doctor")
    public ResponseEntity<Map<String, UUID>> register(@Valid @RequestBody DoctorCommand.RegisterDoctor cmd) {
        UUID id = applicationService.handle(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("doctorId", id));
    }

    @PostMapping("/{doctorId}/verify")
    @Operation(summary = "Verify doctor license (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verify(@PathVariable UUID doctorId) {
        applicationService.handle(new DoctorCommand.VerifyDoctor(doctorId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{doctorId}")
    @Operation(summary = "Get doctor by ID")
    public ResponseEntity<Doctor> getById(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(applicationService.handle(new DoctorQuery.GetDoctorById(doctorId)));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available doctors by consultation type")
    public ResponseEntity<List<Doctor>> getAvailable(@RequestParam ConsultationType type) {
        return ResponseEntity.ok(applicationService.handle(new DoctorQuery.GetAvailableDoctors(type)));
    }

    @GetMapping("/available/specialization")
    @Operation(summary = "Get available doctors by type and specialization")
    public ResponseEntity<List<Doctor>> getBySpecialization(
            @RequestParam ConsultationType type,
            @RequestParam Specialization specialization) {
        return ResponseEntity.ok(applicationService.handle(new DoctorQuery.GetDoctorsBySpecialization(type, specialization)));
    }

    @PatchMapping("/{doctorId}/availability")
    @Operation(summary = "Update doctor availability")
    public ResponseEntity<Void> setAvailability(
            @PathVariable UUID doctorId,
            @RequestBody Map<String, Boolean> availability) {
        applicationService.handle(new DoctorCommand.SetAvailability(
                doctorId,
                availability.getOrDefault("homeVisit", false),
                availability.getOrDefault("remote", false),
                availability.getOrDefault("emergency", false)
        ));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{doctorId}/location")
    @Operation(summary = "Update doctor GPS location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable UUID doctorId,
            @RequestBody DoctorCommand.UpdateLocation cmd) {
        applicationService.handle(new DoctorCommand.UpdateLocation(
                doctorId, cmd.latitude(), cmd.longitude(), cmd.coverageZone()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{doctorId}/off-duty")
    @Operation(summary = "Doctor goes off duty")
    public ResponseEntity<Void> goOffDuty(@PathVariable UUID doctorId) {
        applicationService.handle(new DoctorCommand.GoOffDuty(doctorId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{doctorId}/on-duty")
    @Operation(summary = "Doctor goes on duty")
    public ResponseEntity<Void> goOnDuty(@PathVariable UUID doctorId) {
        applicationService.handle(new DoctorCommand.GoOnDuty(doctorId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{doctorId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspend a doctor")
    public ResponseEntity<Void> suspend(@PathVariable UUID doctorId, @RequestParam String reason) {
        applicationService.handle(new DoctorCommand.SuspendDoctor(doctorId, reason));
        return ResponseEntity.noContent().build();
    }
}
