package com.telehealth.modules.consultation.infrastructure.messaging;

import com.telehealth.modules.consultation.domain.model.Consultation;
import com.telehealth.shared.events.TeleHealthEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${telehealth.rabbitmq.exchanges.consultation}")
    private String consultationExchange;

    public void publishConsultationRequested(Consultation c) {
        var event = new TeleHealthEvents.ConsultationRequestedEvent(
                UUID.randomUUID(), c.getId(), c.getPatientId(),
                c.getType().name(),
                c.getRequiredSpecialization() != null ? c.getRequiredSpecialization().name() : null,
                c.getUrgencyLevel() != null ? c.getUrgencyLevel().name() : null,
                c.getPatientLocation(), LocalDateTime.now().toString(), "consultation-module"
        );
        log.info("Publishing ConsultationRequested: {}", c.getId());
        rabbitTemplate.convertAndSend(consultationExchange, "consultation.requested", event);
    }

    public void publishConsultationAssigned(Consultation c) {
        var event = new TeleHealthEvents.ConsultationAssignedEvent(
                UUID.randomUUID(), c.getId(), c.getPatientId(), c.getDoctorId(),
                null, // doctorName resolved by doctor module listener
                c.getScheduledAt() != null ? c.getScheduledAt().toString() : null,
                c.getType().name(), LocalDateTime.now().toString(), "consultation-module"
        );
        log.info("Publishing ConsultationAssigned: {}", c.getId());
        rabbitTemplate.convertAndSend(consultationExchange, "consultation.assigned", event);
    }

    public void publishConsultationCompleted(Consultation c) {
        var event = new TeleHealthEvents.ConsultationCompletedEvent(
                UUID.randomUUID(), c.getId(), c.getPatientId(), c.getDoctorId(),
                c.getDiagnosis(), c.getPrescription(),
                LocalDateTime.now().toString(), "consultation-module"
        );
        log.info("Publishing ConsultationCompleted: {}", c.getId());
        rabbitTemplate.convertAndSend(consultationExchange, "consultation.completed", event);
    }

    public void publishConsultationCancelled(Consultation c) {
        var event = new TeleHealthEvents.ConsultationCancelledEvent(
                UUID.randomUUID(), c.getId(), c.getPatientId(),
                c.getCancellationReason(), LocalDateTime.now().toString(), "consultation-module"
        );
        rabbitTemplate.convertAndSend(consultationExchange, "consultation.cancelled", event);
    }
}
