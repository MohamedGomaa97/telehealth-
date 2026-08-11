package com.telehealth.modules.doctor.domain.model;

import com.telehealth.shared.domain.BaseEntity;
import com.telehealth.shared.exceptions.TeleHealthException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Doctor extends BaseEntity {

    private String fullName;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private Specialization specialization;
    private List<String> subSpecializations;
    private DoctorStatus status;
    private boolean availableForHomeVisit;
    private boolean availableForRemote;
    private boolean availableForEmergency;
    private double latitude;
    private double longitude;
    private String coverageZone;
    private double rating;
    private int totalConsultations;
    private WorkingHours workingHours;

    private Doctor() {
        super();
        this.subSpecializations = new ArrayList<>();
        this.status = DoctorStatus.PENDING_VERIFICATION;
        this.rating = 0.0;
        this.totalConsultations = 0;
    }

    public static Doctor register(String fullName, String email, String phone,
                                   String licenseNumber, Specialization specialization) {
        if (fullName == null || fullName.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Doctor full name is required");
        if (licenseNumber == null || licenseNumber.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("License number is required");
        Doctor d = new Doctor();
        d.fullName = fullName;
        d.email = email;
        d.phoneNumber = phone;
        d.licenseNumber = licenseNumber;
        d.specialization = specialization;
        return d;
    }

    public void verify() {
        if (this.status != DoctorStatus.PENDING_VERIFICATION)
            throw new TeleHealthException.BusinessRuleViolationException("Doctor is not pending verification");
        this.status = DoctorStatus.ACTIVE;
        markUpdated();
    }

    public void setAvailability(boolean homeVisit, boolean remote, boolean emergency) {
        if (this.status != DoctorStatus.ACTIVE)
            throw new TeleHealthException.BusinessRuleViolationException("Only active doctors can change availability");
        this.availableForHomeVisit = homeVisit;
        this.availableForRemote = remote;
        this.availableForEmergency = emergency;
        markUpdated();
    }

    public void updateLocation(double lat, double lng, String zone) {
        this.latitude = lat;
        this.longitude = lng;
        this.coverageZone = zone;
        markUpdated();
    }

    public void recordConsultation(double patientRating) {
        this.totalConsultations++;
        this.rating = ((this.rating * (totalConsultations - 1)) + patientRating) / totalConsultations;
        markUpdated();
    }

    public void goOffDuty() {
        this.availableForHomeVisit = false;
        this.availableForRemote = false;
        this.availableForEmergency = false;
        this.status = DoctorStatus.OFF_DUTY;
        markUpdated();
    }

    public void goOnDuty() {
        if (this.status == DoctorStatus.SUSPENDED)
            throw new TeleHealthException.BusinessRuleViolationException("Suspended doctors cannot go on duty");
        this.status = DoctorStatus.ACTIVE;
        markUpdated();
    }

    public void suspend(String reason) {
        this.status = DoctorStatus.SUSPENDED;
        markUpdated();
    }

    public boolean isAvailableFor(ConsultationType type) {
        if (this.status != DoctorStatus.ACTIVE) return false;
        return switch (type) {
            case HOME_VISIT -> availableForHomeVisit;
            case REMOTE -> availableForRemote;
            case EMERGENCY -> availableForEmergency;
        };
    }

    public boolean isActive() { return this.status == DoctorStatus.ACTIVE; }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getLicenseNumber() { return licenseNumber; }
    public Specialization getSpecialization() { return specialization; }
    public List<String> getSubSpecializations() { return Collections.unmodifiableList(subSpecializations); }
    public DoctorStatus getStatus() { return status; }
    public boolean isAvailableForHomeVisit() { return availableForHomeVisit; }
    public boolean isAvailableForRemote() { return availableForRemote; }
    public boolean isAvailableForEmergency() { return availableForEmergency; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getCoverageZone() { return coverageZone; }
    public double getRating() { return rating; }
    public int getTotalConsultations() { return totalConsultations; }
    public WorkingHours getWorkingHours() { return workingHours; }

    public void setId(UUID id) { this.id = id; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
    public void setStatus(DoctorStatus s) { this.status = s; }
    public void setRating(double r) { this.rating = r; }
    public void setTotalConsultations(int c) { this.totalConsultations = c; }
    public void setAvailableForHomeVisit(boolean b) { this.availableForHomeVisit = b; }
    public void setAvailableForRemote(boolean b) { this.availableForRemote = b; }
    public void setAvailableForEmergency(boolean b) { this.availableForEmergency = b; }
    public void setLatitude(double v) { this.latitude = v; }
    public void setLongitude(double v) { this.longitude = v; }
    public void setCoverageZone(String z) { this.coverageZone = z; }
    public void setSubSpecializations(List<String> s) { this.subSpecializations = new ArrayList<>(s); }
    public void setWorkingHours(WorkingHours wh) { this.workingHours = wh; }
}
