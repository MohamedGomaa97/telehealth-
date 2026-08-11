package com.telehealth.modules.consultation.infrastructure.persistence.mapper;

import com.telehealth.modules.consultation.domain.model.*;
import com.telehealth.modules.consultation.infrastructure.persistence.entity.ConsultationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ConsultationPersistenceMapper {

    public ConsultationJpaEntity toEntity(Consultation d) {
        ConsultationJpaEntity e = new ConsultationJpaEntity();
        e.setId(d.getId());
        e.setPatientId(d.getPatientId());
        e.setDoctorId(d.getDoctorId());
        e.setType(d.getType().name());
        e.setRequiredSpecialization(d.getRequiredSpecialization() != null ? d.getRequiredSpecialization().name() : null);
        e.setStatus(d.getStatus().name());
        e.setUrgencyLevel(d.getUrgencyLevel() != null ? d.getUrgencyLevel().name() : null);
        e.setPatientNotes(d.getPatientNotes());
        e.setPatientLocation(d.getPatientLocation());
        e.setPatientLatitude(d.getPatientLatitude());
        e.setPatientLongitude(d.getPatientLongitude());
        e.setRequestedAt(d.getRequestedAt());
        e.setScheduledAt(d.getScheduledAt());
        e.setStartedAt(d.getStartedAt());
        e.setCompletedAt(d.getCompletedAt());
        e.setDiagnosis(d.getDiagnosis());
        e.setPrescription(d.getPrescription());
        e.setDoctorNotes(d.getDoctorNotes());
        e.setPatientRating(d.getPatientRating());
        e.setCancellationReason(d.getCancellationReason());
        e.setDeleted(d.isDeleted());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }

    public Consultation toDomain(ConsultationJpaEntity e) {
        Consultation c = Consultation.request(
                e.getPatientId(),
                ConsultationType.valueOf(e.getType()),
                e.getRequiredSpecialization() != null ? Specialization.valueOf(e.getRequiredSpecialization()) : null,
                e.getUrgencyLevel() != null ? UrgencyLevel.valueOf(e.getUrgencyLevel()) : null,
                e.getPatientNotes(),
                e.getPatientLocation(),
                e.getPatientLatitude(),
                e.getPatientLongitude()
        );
        c.setId(e.getId());
        c.setDoctorId(e.getDoctorId());
        c.setStatus(ConsultationStatus.valueOf(e.getStatus()));
        c.setScheduledAt(e.getScheduledAt());
        c.setStartedAt(e.getStartedAt());
        c.setCompletedAt(e.getCompletedAt());
        c.setRequestedAt(e.getRequestedAt());
        c.setDiagnosis(e.getDiagnosis());
        c.setPrescription(e.getPrescription());
        c.setDoctorNotes(e.getDoctorNotes());
        c.setPatientRating(e.getPatientRating());
        c.setCancellationReason(e.getCancellationReason());
        c.setCreatedAt(e.getCreatedAt());
        c.setUpdatedAt(e.getUpdatedAt());
        return c;
    }
}
