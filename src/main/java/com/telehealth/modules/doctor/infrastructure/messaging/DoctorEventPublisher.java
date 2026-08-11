package com.telehealth.modules.doctor.infrastructure.messaging;

import com.telehealth.modules.doctor.domain.model.Doctor;
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
public class DoctorEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${telehealth.rabbitmq.exchanges.notification}")
    private String notificationExchange;

    public void publishAvailabilityChanged(Doctor doctor) {
        var event = new TeleHealthEvents.DoctorAvailabilityChangedEvent(
                UUID.randomUUID(),
                doctor.getId(),
                doctor.isAvailableForHomeVisit() || doctor.isAvailableForRemote() || doctor.isAvailableForEmergency(),
                doctor.getCoverageZone(),
                LocalDateTime.now().toString(),
                "doctor-module"
        );
        try {
            rabbitTemplate.convertAndSend(notificationExchange, "doctor.availability.changed", event);
        } catch (Exception e) {
            log.error("Failed to publish DoctorAvailabilityChangedEvent for doctor: {}", doctor.getId(), e);
        }
    }
}
