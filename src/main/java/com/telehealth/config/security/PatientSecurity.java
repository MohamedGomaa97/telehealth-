package com.telehealth.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("patientSecurity")
public class PatientSecurity {

    public boolean isSelf(UUID patientId) {
        if (patientId == null) return false;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        return patientId.toString().equals(auth.getName());
    }
}
