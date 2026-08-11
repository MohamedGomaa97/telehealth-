package com.telehealth.modules.consultation.domain.model;
public enum ConsultationType {
    HOME_VISIT("Home Visit - Doctor comes to patient"),
    REMOTE("Remote Consultation - Video/Phone"),
    EMERGENCY("Emergency - Immediate dispatch");
    private final String description;
    ConsultationType(String d) { this.description = d; }
    public String getDescription() { return description; }
}
