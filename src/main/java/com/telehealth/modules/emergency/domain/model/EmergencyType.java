package com.telehealth.modules.emergency.domain.model;

public enum EmergencyType {
    CARDIAC("Cardiac Emergency"),
    RESPIRATORY("Respiratory Emergency"),
    TRAUMA("Physical Trauma"),
    STROKE("Stroke"),
    DIABETIC("Diabetic Emergency"),
    ALLERGIC("Severe Allergic Reaction"),
    GENERAL("General Emergency");

    private final String display;
    EmergencyType(String d) { this.display = d; }
    public String getDisplay() { return display; }
}
