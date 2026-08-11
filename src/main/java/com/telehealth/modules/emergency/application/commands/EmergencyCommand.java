package com.telehealth.modules.emergency.application.commands;

import com.telehealth.modules.emergency.domain.model.EmergencyType;
import com.telehealth.modules.emergency.domain.model.EmergencySeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public sealed interface EmergencyCommand
        permits EmergencyCommand.TriggerEmergency,
                EmergencyCommand.DispatchResponder,
                EmergencyCommand.MarkEnRoute,
                EmergencyCommand.MarkOnScene,
                EmergencyCommand.ResolveEmergency,
                EmergencyCommand.CancelEmergency {

    record TriggerEmergency(
            @NotNull UUID patientId,
            @NotBlank String patientName,
            @NotBlank String patientPhone,
            @NotBlank String location,
            double latitude,
            double longitude,
            EmergencyType type,
            EmergencySeverity severity
    ) implements EmergencyCommand {}

    record DispatchResponder(
            @NotNull UUID emergencyId,
            @NotNull UUID responderId,
            @NotBlank String responderName,
            @NotBlank String responderPhone,
            String etaMinutes
    ) implements EmergencyCommand {}

    record MarkEnRoute(@NotNull UUID emergencyId) implements EmergencyCommand {}

    record MarkOnScene(@NotNull UUID emergencyId) implements EmergencyCommand {}

    record ResolveEmergency(
            @NotNull UUID emergencyId,
            String resolutionNotes
    ) implements EmergencyCommand {}

    record CancelEmergency(
            @NotNull UUID emergencyId,
            @NotBlank String reason
    ) implements EmergencyCommand {}
}
