package com.telehealth.modules.emergency.domain.model;

public enum EmergencyStatus {
    TRIGGERED,
    PENDING_DISPATCH,
    DISPATCHED,
    EN_ROUTE,
    ON_SCENE,
    RESOLVED,
    CANCELLED
}
