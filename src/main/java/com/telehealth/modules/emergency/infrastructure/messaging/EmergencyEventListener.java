package com.telehealth.modules.emergency.infrastructure.messaging;

import com.telehealth.modules.emergency.application.commands.EmergencyCommand;
import com.telehealth.modules.emergency.application.handlers.EmergencyApplicationService;
import com.telehealth.modules.emergency.domain.model.EmergencySeverity;
import com.telehealth.modules.emergency.domain.model.EmergencyType;
import com.telehealth.shared.events.TeleHealthEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens to cross-module events that require emergency action.
 *
 * Example: a CRITICAL consultation request auto-escalates to an emergency dispatch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmergencyEventListener {

    private final EmergencyApplicationService emergencyApplicationService;

    /**
     * If a CRITICAL consultation request comes in with type EMERGENCY,
     * auto-trigger an emergency dispatch in parallel.
     */
    @RabbitListener(queues = "${telehealth.rabbitmq.queues.emergency-triggered}")
    public void onEmergencyTriggered(TeleHealthEvents.EmergencyTriggeredEvent event) {
        log.warn("🚨 Emergency event received for patient: {} severity: {}",
                event.patientId(), event.severity());
        // Could auto-assign nearest available emergency doctor here
        // For now we log and let the dispatch be manual or via scheduler
    }

    /**
     * When a consultation requests EMERGENCY type, convert it to a formal emergency.
     */
    @RabbitListener(queues = "${telehealth.rabbitmq.queues.consultation-requested}")
    public void onConsultationRequested(TeleHealthEvents.ConsultationRequestedEvent event) {
        if ("EMERGENCY".equals(event.consultationType()) && "CRITICAL".equals(event.urgencyLevel())) {
            log.warn("🚨 CRITICAL consultation detected — escalating to emergency: {}", event.consultationId());
            try {
                emergencyApplicationService.handle(new EmergencyCommand.TriggerEmergency(
                        event.patientId(),
                        "Patient (Emergency Escalation)",
                        "Unknown",
                        event.patientLocation() != null ? event.patientLocation() : "Location not provided",
                        0.0, 0.0,
                        EmergencyType.GENERAL,
                        EmergencySeverity.CRITICAL
                ));
            } catch (Exception ex) {
                log.error("Failed to auto-escalate consultation to emergency: {}", ex.getMessage());
            }
        }
    }
}
