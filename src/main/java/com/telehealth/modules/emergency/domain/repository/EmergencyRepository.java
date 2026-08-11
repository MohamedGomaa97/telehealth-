package com.telehealth.modules.emergency.domain.repository;

import com.telehealth.modules.emergency.domain.model.Emergency;
import com.telehealth.modules.emergency.domain.model.EmergencyStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmergencyRepository {
    Emergency save(Emergency emergency);
    Optional<Emergency> findById(UUID id);
    List<Emergency> findByPatientId(UUID patientId);
    List<Emergency> findActive();
    List<Emergency> findByStatus(EmergencyStatus status);
    boolean hasActiveEmergency(UUID patientId);
}
