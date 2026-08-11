package com.telehealth.modules.patient.domain.model;

/**
 * Immutable value object for emergency contacts.
 * Value objects have no identity — equality is by value.
 */
public record EmergencyContact(
        String name,
        String relationship,
        String phoneNumber
) {
    public EmergencyContact {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Emergency contact name is required");
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new IllegalArgumentException("Emergency contact phone is required");
    }
}
