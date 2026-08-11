package com.telehealth.modules.emergency.infrastructure.persistence.mapper;

import com.telehealth.modules.emergency.domain.model.*;
import com.telehealth.modules.emergency.infrastructure.persistence.entity.EmergencyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EmergencyPersistenceMapper {

    public EmergencyJpaEntity toEntity(Emergency d) {
        EmergencyJpaEntity e = new EmergencyJpaEntity();
        e.setId(d.getId());
        e.setPatientId(d.getPatientId());
        e.setPatientName(d.getPatientName());
        e.setPatientPhone(d.getPatientPhone());
        e.setLocation(d.getLocation());
        e.setLatitude(d.getLatitude());
        e.setLongitude(d.getLongitude());
        e.setType(d.getType().name());
        e.setSeverity(d.getSeverity().name());
        e.setStatus(d.getStatus().name());
        e.setResponderId(d.getResponderId());
        e.setResponderName(d.getResponderName());
        e.setResponderPhone(d.getResponderPhone());
        e.setEstimatedArrivalMinutes(d.getEstimatedArrivalMinutes());
        e.setResolutionNotes(d.getResolutionNotes());
        e.setTriggeredAt(d.getTriggeredAt());
        e.setDispatchedAt(d.getDispatchedAt());
        e.setResolvedAt(d.getResolvedAt());
        e.setDeleted(d.isDeleted());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }

    public Emergency toDomain(EmergencyJpaEntity e) {
        Emergency d = Emergency.trigger(
                e.getPatientId(), e.getPatientName(), e.getPatientPhone(),
                e.getLocation(), e.getLatitude(), e.getLongitude(),
                EmergencyType.valueOf(e.getType()),
                EmergencySeverity.valueOf(e.getSeverity())
        );
        d.setId(e.getId());
        d.setStatus(EmergencyStatus.valueOf(e.getStatus()));
        d.setResponderId(e.getResponderId());
        d.setResponderName(e.getResponderName());
        d.setResponderPhone(e.getResponderPhone());
        d.setEstimatedArrivalMinutes(e.getEstimatedArrivalMinutes());
        d.setResolutionNotes(e.getResolutionNotes());
        d.setTriggeredAt(e.getTriggeredAt());
        d.setDispatchedAt(e.getDispatchedAt());
        d.setResolvedAt(e.getResolvedAt());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }
}
