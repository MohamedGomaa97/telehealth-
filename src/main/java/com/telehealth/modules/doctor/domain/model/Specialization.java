package com.telehealth.modules.doctor.domain.model;
public enum Specialization {
    GENERAL_PRACTICE("General Practice"), CARDIOLOGY("Cardiology"),
    ORTHOPEDICS("Orthopedics"), PEDIATRICS("Pediatrics"),
    NEUROLOGY("Neurology"), EMERGENCY_MEDICINE("Emergency Medicine"),
    DERMATOLOGY("Dermatology"), INTERNAL_MEDICINE("Internal Medicine"),
    GYNECOLOGY("Gynecology"), PSYCHIATRY("Psychiatry");
    private final String display;
    Specialization(String d) { this.display = d; }
    public String getDisplay() { return display; }
}
