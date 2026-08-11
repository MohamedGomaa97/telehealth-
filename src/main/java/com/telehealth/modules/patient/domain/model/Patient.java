package com.telehealth.modules.patient.domain.model;

import com.telehealth.shared.domain.BaseEntity;
import com.telehealth.shared.exceptions.TeleHealthException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Patient - Core Domain Entity (Patient Module)
 *
 * This is a pure domain object with no framework dependencies.
 * Business rules are enforced here, not in services.
 */
public class Patient extends BaseEntity {

    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String nationalId;
    private BloodType bloodType;
    private String address;
    private double latitude;
    private double longitude;
    private PatientStatus status;
    private List<MedicalRecord> medicalHistory;
    private List<String> allergies;
    private List<String> chronicConditions;
    private EmergencyContact emergencyContact;

    // Private constructor — use factory methods
    private Patient() {
        super();
        this.medicalHistory = new ArrayList<>();
        this.allergies = new ArrayList<>();
        this.chronicConditions = new ArrayList<>();
        this.status = PatientStatus.ACTIVE;
    }

    // ─── Factory Methods ───────────────────────────────────────────────────────

    public static Patient register(
            String fullName,
            String email,
            String phoneNumber,
            LocalDate dateOfBirth,
            String nationalId,
            BloodType bloodType,
            String address
    ) {
        validateRegistration(fullName, email, phoneNumber, nationalId);

        Patient patient = new Patient();
        patient.fullName = fullName;
        patient.email = email;
        patient.phoneNumber = phoneNumber;
        patient.dateOfBirth = dateOfBirth;
        patient.nationalId = nationalId;
        patient.bloodType = bloodType;
        patient.address = address;

        return patient;
    }

    // ─── Business Methods ──────────────────────────────────────────────────────

    public void updateLocation(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90)
            throw new TeleHealthException.BusinessRuleViolationException("Invalid latitude: " + latitude);
        if (longitude < -180 || longitude > 180)
            throw new TeleHealthException.BusinessRuleViolationException("Invalid longitude: " + longitude);

        this.latitude = latitude;
        this.longitude = longitude;
        markUpdated();
    }

    public void addMedicalRecord(MedicalRecord record) {
        if (record == null)
            throw new TeleHealthException.BusinessRuleViolationException("Medical record cannot be null");
        this.medicalHistory.add(record);
        markUpdated();
    }

    public void addAllergy(String allergy) {
        if (allergy == null || allergy.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Allergy description cannot be empty");
        if (!this.allergies.contains(allergy)) {
            this.allergies.add(allergy);
            markUpdated();
        }
    }

    public void addChronicCondition(String condition) {
        if (condition == null || condition.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Condition cannot be empty");
        if (!this.chronicConditions.contains(condition)) {
            this.chronicConditions.add(condition);
            markUpdated();
        }
    }

    public void setEmergencyContact(EmergencyContact contact) {
        this.emergencyContact = contact;
        markUpdated();
    }

    public void suspend(String reason) {
        if (this.status == PatientStatus.SUSPENDED)
            throw new TeleHealthException.BusinessRuleViolationException("Patient is already suspended");
        this.status = PatientStatus.SUSPENDED;
        markUpdated();
    }

    public void reactivate() {
        if (this.status == PatientStatus.ACTIVE)
            throw new TeleHealthException.BusinessRuleViolationException("Patient is already active");
        this.status = PatientStatus.ACTIVE;
        markUpdated();
    }

    public boolean isActive() {
        return this.status == PatientStatus.ACTIVE;
    }

    public boolean hasLocation() {
        return this.latitude != 0.0 || this.longitude != 0.0;
    }

    // ─── Validation ────────────────────────────────────────────────────────────

    private static void validateRegistration(String fullName, String email, String phone, String nationalId) {
        if (fullName == null || fullName.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Full name is required");
        if (email == null || !email.contains("@"))
            throw new TeleHealthException.BusinessRuleViolationException("Valid email is required");
        if (phone == null || phone.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Phone number is required");
        if (nationalId == null || nationalId.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("National ID is required");
    }

    // ─── Getters ───────────────────────────────────────────────────────────────

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getNationalId() { return nationalId; }
    public BloodType getBloodType() { return bloodType; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public PatientStatus getStatus() { return status; }
    public EmergencyContact getEmergencyContact() { return emergencyContact; }
    public List<MedicalRecord> getMedicalHistory() { return Collections.unmodifiableList(medicalHistory); }
    public List<String> getAllergies() { return Collections.unmodifiableList(allergies); }
    public List<String> getChronicConditions() { return Collections.unmodifiableList(chronicConditions); }

    // Setters for reconstitution from persistence only
    public void setId(UUID id) { this.id = id; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setMedicalHistory(List<MedicalRecord> history) { this.medicalHistory = new ArrayList<>(history); }
    public void setAllergies(List<String> allergies) { this.allergies = new ArrayList<>(allergies); }
    public void setChronicConditions(List<String> conditions) { this.chronicConditions = new ArrayList<>(conditions); }
    public void setStatus(PatientStatus status) { this.status = status; }
    public void setLatitude(double lat) { this.latitude = lat; }
    public void setLongitude(double lng) { this.longitude = lng; }
}
