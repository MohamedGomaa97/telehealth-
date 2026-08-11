package com.telehealth.modules.emergency.domain.model;

public enum EmergencySeverity {
    CRITICAL,   // Life-threatening, dispatch immediately
    SEVERE,     // Serious, dispatch within 5 min
    MODERATE    // Concerning, dispatch within 15 min
}
