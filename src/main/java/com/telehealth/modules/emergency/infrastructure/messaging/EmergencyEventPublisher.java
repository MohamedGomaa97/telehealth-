package com.telehealth.modules.emergency.infrastructure.messaging;

import com.telehealth.modules.emergency.domain.model.Emergency;
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
public class EmergencyEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${telehealth.rabbitmq.exchanges.emergency}")
    private String emergencyExchange;

    @Value("${telehealth.rabbitmq.exchanges.notification}")
    private String notificationExchange;

    public void publishEmergencyTriggered(Emergency e) {
        var event = new TeleHealthEvents.EmergencyTriggeredEvent(
                UUID.randomUUID(), e.getId(), e.getPatientId(),
                e.getPatientName(), e.getPatientPhone(),
                e.getLocation(), e.getLatitude(), e.getLongitude(),
                e.getType().name(), e.getSeverity().name(),
                LocalDateTime.now().toString(), "emergency-module"
        );
        log.warn("🚨 Publishing EmergencyTriggered: {}", e.getId());
        rabbitTemplate.convertAndSend(emergencyExchange, "emergency.triggered", event);
        // Also notify patient immediately
        rabbitTemplate.convertAndSend(notificationExchange, "notification.emergency.triggered", event);
    }

    public void publishEmergencyDispatched(Emergency e) {
        var event = new TeleHealthEvents.EmergencyDispatchedEvent(
                UUID.randomUUID(), e.getId(), e.getPatientId(),
                e.getResponderId(), e.getResponderName(), e.getResponderPhone(),
                e.getEstimatedArrivalMinutes(),
                LocalDateTime.now().toString(), "emergency-module"
        );
        log.info("Publishing EmergencyDispatched: {}", e.getId());
        rabbitTemplate.convertAndSend(emergencyExchange, "emergency.dispatched", event);
        rabbitTemplate.convertAndSend(notificationExchange, "notification.emergency.dispatched", event);
    }

    public void publishEmergencyResolved(Emergency e) {
        var event = new TeleHealthEvents.EmergencyResolvedEvent(
                UUID.randomUUID(), e.getId(), e.getPatientId(),
                e.getResolutionNotes(),
                LocalDateTime.now().toString(), "emergency-module"
        );
        log.info("Publishing EmergencyResolved: {}", e.getId());
        rabbitTemplate.convertAndSend(emergencyExchange, "emergency.resolved", event);
    }
}
