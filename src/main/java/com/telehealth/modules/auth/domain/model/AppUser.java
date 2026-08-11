package com.telehealth.modules.auth.domain.model;

import com.telehealth.shared.domain.BaseEntity;
import com.telehealth.shared.exceptions.TeleHealthException;

import java.util.UUID;


public class AppUser extends BaseEntity {

    private String email;
    private String passwordHash;
    private UserRole role;
    private UUID linkedId; // Patient.id or Doctor.id; null for ADMIN
    private boolean enabled;

    private AppUser() {
        super();
        this.enabled = true;
    }

    public static AppUser create(String email, String passwordHash, UserRole role, UUID linkedId) {
        if (email == null || email.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Email is required for login");
        if (passwordHash == null || passwordHash.isBlank())
            throw new TeleHealthException.BusinessRuleViolationException("Password is required for login");
        if (role == null)
            throw new TeleHealthException.BusinessRuleViolationException("Role is required for login");
        if (role != UserRole.ADMIN && linkedId == null)
            throw new TeleHealthException.BusinessRuleViolationException("linkedId is required for non-admin accounts");

        AppUser user = new AppUser();
        user.email = email.toLowerCase();
        user.passwordHash = passwordHash;
        user.role = role;
        user.linkedId = linkedId;
        return user;
    }

    public void disable() {
        this.enabled = false;
        markUpdated();
    }

    public void enable() {
        this.enabled = true;
        markUpdated();
    }

    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public UUID getLinkedId() { return linkedId; }
    public boolean isEnabled() { return enabled; }

    public void setId(UUID id) { this.id = id; }
    public void setCreatedAt(java.time.LocalDateTime t) { this.createdAt = t; }
    public void setUpdatedAt(java.time.LocalDateTime t) { this.updatedAt = t; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
