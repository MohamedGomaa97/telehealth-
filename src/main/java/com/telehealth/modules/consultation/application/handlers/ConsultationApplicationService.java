package com.telehealth.modules.consultation.application.handlers;

import com.telehealth.modules.consultation.application.commands.ConsultationCommand;
import com.telehealth.modules.consultation.application.queries.ConsultationQuery;
import com.telehealth.modules.consultation.domain.model.Consultation;
import com.telehealth.modules.consultation.domain.model.ConsultationStatus;
import com.telehealth.modules.consultation.domain.repository.ConsultationRepository;
import com.telehealth.modules.consultation.domain.service.ConsultationDomainService;
import com.telehealth.modules.consultation.infrastructure.messaging.ConsultationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationApplicationService {

    private final ConsultationRepository consultationRepository;
    private final ConsultationDomainService domainService;
    private final ConsultationEventPublisher eventPublisher;

    public UUID handle(ConsultationCommand.RequestConsultation cmd) {
        log.info("Consultation requested by patient: {} type: {}", cmd.patientId(), cmd.type());
        domainService.validatePatientHasNoActiveConsultation(cmd.patientId());

        Consultation consultation = Consultation.request(
                cmd.patientId(), cmd.type(), cmd.specialization(),
                cmd.urgencyLevel(), cmd.patientNotes(),
                cmd.patientLocation(), cmd.latitude(), cmd.longitude()
        );
        Consultation saved = consultationRepository.save(consultation);
        eventPublisher.publishConsultationRequested(saved);
        log.info("Consultation created: {}", saved.getId());
        return saved.getId();
    }

    public void handle(ConsultationCommand.AssignDoctor cmd) {
        Consultation consultation = domainService.getOrThrow(cmd.consultationId());
        domainService.validateDoctorNotOverloaded(cmd.doctorId());

        LocalDateTime scheduledAt = cmd.scheduledAt() != null
                ? LocalDateTime.parse(cmd.scheduledAt())
                : LocalDateTime.now().plusMinutes(30);

        consultation.assignDoctor(cmd.doctorId(), scheduledAt);
        consultationRepository.save(consultation);
        eventPublisher.publishConsultationAssigned(consultation);
        log.info("Doctor {} assigned to consultation {}", cmd.doctorId(), cmd.consultationId());
    }

    public void handle(ConsultationCommand.StartConsultation cmd) {
        Consultation consultation = domainService.getOrThrow(cmd.consultationId());
        consultation.start();
        consultationRepository.save(consultation);
        log.info("Consultation started: {}", cmd.consultationId());
    }

    public void handle(ConsultationCommand.CompleteConsultation cmd) {
        Consultation consultation = domainService.getOrThrow(cmd.consultationId());
        consultation.complete(cmd.diagnosis(), cmd.prescription(), cmd.doctorNotes());
        consultationRepository.save(consultation);
        eventPublisher.publishConsultationCompleted(consultation);
        log.info("Consultation completed: {}", cmd.consultationId());
    }

    public void handle(ConsultationCommand.CancelConsultation cmd) {
        Consultation consultation = domainService.getOrThrow(cmd.consultationId());
        consultation.cancel(cmd.reason());
        consultationRepository.save(consultation);
        eventPublisher.publishConsultationCancelled(consultation);
        log.info("Consultation cancelled: {} reason: {}", cmd.consultationId(), cmd.reason());
    }

    public void handle(ConsultationCommand.RateConsultation cmd) {
        Consultation consultation = domainService.getOrThrow(cmd.consultationId());
        consultation.rateConsultation(cmd.rating());
        consultationRepository.save(consultation);
    }

    @Transactional(readOnly = true)
    public Consultation handle(ConsultationQuery.GetConsultationById q) {
        return domainService.getOrThrow(q.consultationId());
    }

    @Transactional(readOnly = true)
    public List<Consultation> handle(ConsultationQuery.GetConsultationsByPatient q) {
        return consultationRepository.findByPatientId(q.patientId());
    }

    @Transactional(readOnly = true)
    public List<Consultation> handle(ConsultationQuery.GetConsultationsByDoctor q) {
        return consultationRepository.findByDoctorId(q.doctorId());
    }

    @Transactional(readOnly = true)
    public List<Consultation> handle(ConsultationQuery.GetPendingConsultations q) {
        return consultationRepository.findByStatus(ConsultationStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<Consultation> handle(ConsultationQuery.GetConsultationsByStatus q) {
        return consultationRepository.findByStatus(q.status());
    }
}
