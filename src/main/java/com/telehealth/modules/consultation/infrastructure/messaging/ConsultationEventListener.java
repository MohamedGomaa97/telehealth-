package com.telehealth.modules.consultation.infrastructure.messaging;

import com.telehealth.shared.events.TeleHealthEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens to events from other modules that affect consultations.
 * Handles post-completion processing such as notifications and audit trails.
 */
@Slf4j
@Component
public class ConsultationEventListener {

    @RabbitListener(queues = "${telehealth.rabbitmq.queues.consultation-completed}")
    public void onConsultationCompleted(TeleHealthEvents.ConsultationCompletedEvent event) {
        log.info("Consultation completed - id: {} patient: {} doctor: {}",
                event.consultationId(), event.patientId(), event.doctorId());
        // Hook point: trigger notification, update analytics, etc.
    }
}
