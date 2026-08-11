package com.telehealth.modules.patient.infrastructure.messaging;

import com.telehealth.modules.patient.domain.model.Patient;
import com.telehealth.shared.events.TeleHealthEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Patient Event Publisher - Infrastructure Layer
 *
 * Publishes domain events to RabbitMQ exchanges.
 * The application layer calls this after successful domain operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${telehealth.rabbitmq.exchanges.notification}")
    private String notificationExchange;

    public void publishPatientRegistered(Patient patient) {
        var event = new TeleHealthEvents.PatientRegisteredEvent(
                UUID.randomUUID(),
                patient.getId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhoneNumber(),
                LocalDateTime.now().toString(),
                "patient-module"
        );

        try {
            rabbitTemplate.convertAndSend(notificationExchange, "patient.registered", event);
            log.info("Published PatientRegisteredEvent for patient: {}", patient.getId());
        } catch (Exception e) {
            log.error("Failed to publish PatientRegisteredEvent for patient: {}", patient.getId(), e);
            // Do not throw — event publishing failure should not rollback patient registration
        }
    }
}
