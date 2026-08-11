package com.telehealth.modules.consultation.domain.model;

import com.telehealth.shared.domain.BaseEntity;
import com.telehealth.shared.exceptions.TeleHealthException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Consultation extends BaseEntity {

    private UUID patientId;
    private UUID doctorId;
    private ConsultationType type;
    private Specialization requiredSpecialization;
    private ConsultationStatus status;
    private UrgencyLevel urgencyLevel;
    private String patientNotes;
    private String patientLocation;
    private double patientLatitude;
    private double patientLongitude;
    private LocalDateTime requestedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String diagnosis;
    private String prescription;
    private String doctorNotes;
    private Double patientRating;
    private String cancellationReason;

    private Consultation() { super(); }

    // ─── Factory ───────────────────────────────────────────────────────────────

    public static Consultation request(UUID patientId, ConsultationType type,
                                        Specialization specialization, UrgencyLevel urgency,
                                        String patientNotes, String location,
                                        double lat, double lng) {
        if (patientId == null)
            throw new TeleHealthException.BusinessRuleViolationException("Patient ID is required");

        Consultation c = new Consultation();
        c.patientId = patientId;
        c.type = type;
        c.requiredSpecialization = specialization;
        c.urgencyLevel = urgency;
        c.patientNotes = patientNotes;
        c.patientLocation = location;
        c.patientLatitude = lat;
        c.patientLongitude = lng;
        c.status = ConsultationStatus.PENDING;
        c.requestedAt = LocalDateTime.now();
        return c;
    }

    // ─── Business Methods ──────────────────────────────────────────────────────

    public void assignDoctor(UUID doctorId, LocalDateTime scheduledAt) {
        if (this.status != ConsultationStatus.PENDING)
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Only PENDING consultations can be assigned a doctor");
        if (doctorId == null)
            throw new TeleHealthException.BusinessRuleViolationException("Doctor ID required for assignment");

        this.doctorId = doctorId;
        this.scheduledAt = scheduledAt;
        this.status = ConsultationStatus.ASSIGNED;
        markUpdated();
    }

    public void start() {
        if (this.status != ConsultationStatus.ASSIGNED)
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Only ASSIGNED consultations can be started");
        this.status = ConsultationStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        markUpdated();
    }

    public void complete(String diagnosis, String prescription, String doctorNotes) {
        if (this.status != ConsultationStatus.IN_PROGRESS)
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Only IN_PROGRESS consultations can be completed");
        if (diagnosis == null || diagnosis.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Diagnosis is required to complete consultation");

        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.doctorNotes = doctorNotes;
        this.status = ConsultationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        markUpdated();
    }

    public void cancel(String reason) {
        if (this.status == ConsultationStatus.COMPLETED || this.status == ConsultationStatus.CANCELLED)
            throw new TeleHealthException.BusinessRuleViolationException(
                    "Cannot cancel a " + this.status + " consultation");
        this.cancellationReason = reason;
        this.status = ConsultationStatus.CANCELLED;
        markUpdated();
    }

    public void rateConsultation(double rating) {
        if (this.status != ConsultationStatus.COMPLETED)
            throw new TeleHealthException.BusinessRuleViolationException("Can only rate completed consultations");
        if (rating < 1.0 || rating > 5.0)
            throw new TeleHealthException.BusinessRuleViolationException("Rating must be between 1 and 5");
        if (this.patientRating != null)
            throw new TeleHealthException.BusinessRuleViolationException("Consultation already rated");
        this.patientRating = rating;
        markUpdated();
    }

    public boolean isPending() { return status == ConsultationStatus.PENDING; }
    public boolean isCompleted() { return status == ConsultationStatus.COMPLETED; }

    // ─── Getters ───────────────────────────────────────────────────────────────
    public UUID getPatientId() { return patientId; }
    public UUID getDoctorId() { return doctorId; }
    public ConsultationType getType() { return type; }
    public Specialization getRequiredSpecialization() { return requiredSpecialization; }
    public ConsultationStatus getStatus() { return status; }
    public UrgencyLevel getUrgencyLevel() { return urgencyLevel; }
    public String getPatientNotes() { return patientNotes; }
    public String getPatientLocation() { return patientLocation; }
    public double getPatientLatitude() { return patientLatitude; }
    public double getPatientLongitude() { return patientLongitude; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getDiagnosis() { return diagnosis; }
    public String getPrescription() { return prescription; }
    public String getDoctorNotes() { return doctorNotes; }
    public Double getPatientRating() { return patientRating; }
    public String getCancellationReason() { return cancellationReason; }

    public void setId(UUID id) { this.id = id; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
    public void setStatus(ConsultationStatus s) { this.status = s; }
    public void setDoctorId(UUID id) { this.doctorId = id; }
    public void setScheduledAt(LocalDateTime t) { this.scheduledAt = t; }
    public void setStartedAt(LocalDateTime t) { this.startedAt = t; }
    public void setCompletedAt(LocalDateTime t) { this.completedAt = t; }
    public void setRequestedAt(LocalDateTime t) { this.requestedAt = t; }
    public void setDiagnosis(String d) { this.diagnosis = d; }
    public void setPrescription(String p) { this.prescription = p; }
    public void setDoctorNotes(String n) { this.doctorNotes = n; }
    public void setPatientRating(Double r) { this.patientRating = r; }
    public void setCancellationReason(String r) { this.cancellationReason = r; }
}
