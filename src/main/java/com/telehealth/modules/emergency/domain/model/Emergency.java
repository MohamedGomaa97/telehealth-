package com.telehealth.modules.emergency.domain.model;

import com.telehealth.shared.domain.BaseEntity;
import com.telehealth.shared.exceptions.TeleHealthException;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Emergency - Core Domain Entity (Emergency Module)
 *
 * Handles SOS requests, emergency dispatch, and resolution.
 * Critical urgency — every state transition is strictly enforced.
 */
public class Emergency extends BaseEntity {

    private UUID patientId;
    private String patientName;
    private String patientPhone;
    private String location;
    private double latitude;
    private double longitude;
    private EmergencyType type;
    private EmergencySeverity severity;
    private EmergencyStatus status;
    private UUID responderId;
    private String responderName;
    private String responderPhone;
    private String estimatedArrivalMinutes;
    private String resolutionNotes;
    private LocalDateTime triggeredAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime resolvedAt;

    private Emergency() { super(); }

    // ─── Factory ───────────────────────────────────────────────────────────────

    public static Emergency trigger(UUID patientId, String patientName, String patientPhone,
                                    String location, double latitude, double longitude,
                                    EmergencyType type, EmergencySeverity severity) {
        if (patientId == null)
            throw new TeleHealthException.BusinessRuleViolationException("Patient ID required for emergency");
        if (location == null || location.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Location is required for emergency dispatch");

        Emergency e = new Emergency();
        e.patientId = patientId;
        e.patientName = patientName;
        e.patientPhone = patientPhone;
        e.location = location;
        e.latitude = latitude;
        e.longitude = longitude;
        e.type = type != null ? type : EmergencyType.GENERAL;
        e.severity = severity != null ? severity : EmergencySeverity.SEVERE;
        e.status = EmergencyStatus.TRIGGERED;
        e.triggeredAt = LocalDateTime.now();
        return e;
    }

    // ─── Business Methods ──────────────────────────────────────────────────────

    public void dispatch(UUID responderId, String responderName, String responderPhone, String etaMinutes) {
        if (this.status != EmergencyStatus.TRIGGERED && this.status != EmergencyStatus.PENDING_DISPATCH)
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Emergency cannot be dispatched from status: " + this.status);

        this.responderId = responderId;
        this.responderName = responderName;
        this.responderPhone = responderPhone;
        this.estimatedArrivalMinutes = etaMinutes;
        this.status = EmergencyStatus.DISPATCHED;
        this.dispatchedAt = LocalDateTime.now();
        markUpdated();
    }

    public void markEnRoute() {
        if (this.status != EmergencyStatus.DISPATCHED)
            throw new TeleHealthException.BusinessRuleViolationException("Emergency must be dispatched first");
        this.status = EmergencyStatus.EN_ROUTE;
        markUpdated();
    }

    public void markOnScene() {
        if (this.status != EmergencyStatus.EN_ROUTE)
            throw new TeleHealthException.BusinessRuleViolationException("Responder must be en route first");
        this.status = EmergencyStatus.ON_SCENE;
        markUpdated();
    }

    public void resolve(String resolutionNotes) {
        if (this.status == EmergencyStatus.RESOLVED || this.status == EmergencyStatus.CANCELLED)
            throw new TeleHealthException.BusinessRuleViolationException("Emergency already " + this.status);
        this.resolutionNotes = resolutionNotes;
        this.status = EmergencyStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        markUpdated();
    }

    public void cancel(String reason) {
        if (this.status == EmergencyStatus.RESOLVED || this.status == EmergencyStatus.ON_SCENE)
            throw new TeleHealthException.BusinessRuleViolationException("Cannot cancel emergency in status: " + this.status);
        this.resolutionNotes = "CANCELLED: " + reason;
        this.status = EmergencyStatus.CANCELLED;
        this.resolvedAt = LocalDateTime.now();
        markUpdated();
    }

    public boolean isCritical() {
        return this.severity == EmergencySeverity.CRITICAL;
    }

    public boolean isActive() {
        return this.status != EmergencyStatus.RESOLVED && this.status != EmergencyStatus.CANCELLED;
    }

    // ─── Getters ───────────────────────────────────────────────────────────────
    public UUID getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getPatientPhone() { return patientPhone; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public EmergencyType getType() { return type; }
    public EmergencySeverity getSeverity() { return severity; }
    public EmergencyStatus getStatus() { return status; }
    public UUID getResponderId() { return responderId; }
    public String getResponderName() { return responderName; }
    public String getResponderPhone() { return responderPhone; }
    public String getEstimatedArrivalMinutes() { return estimatedArrivalMinutes; }
    public String getResolutionNotes() { return resolutionNotes; }
    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public LocalDateTime getDispatchedAt() { return dispatchedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }

    // Setters for persistence reconstitution
    public void setId(UUID id) { this.id = id; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
    public void setStatus(EmergencyStatus s) { this.status = s; }
    public void setResponderId(UUID id) { this.responderId = id; }
    public void setResponderName(String n) { this.responderName = n; }
    public void setResponderPhone(String p) { this.responderPhone = p; }
    public void setEstimatedArrivalMinutes(String e) { this.estimatedArrivalMinutes = e; }
    public void setResolutionNotes(String n) { this.resolutionNotes = n; }
    public void setTriggeredAt(LocalDateTime t) { this.triggeredAt = t; }
    public void setDispatchedAt(LocalDateTime t) { this.dispatchedAt = t; }
    public void setResolvedAt(LocalDateTime t) { this.resolvedAt = t; }
}
