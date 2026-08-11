package com.telehealth.modules.consultation.domain.model;
public enum UrgencyLevel {
    LOW(72), MEDIUM(24), HIGH(4), CRITICAL(1);
    private final int maxWaitHours;
    UrgencyLevel(int h) { this.maxWaitHours = h; }
    public int getMaxWaitHours() { return maxWaitHours; }
}
