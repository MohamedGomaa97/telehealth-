package com.telehealth.modules.emergency.application.handlers;

import com.telehealth.modules.emergency.application.commands.EmergencyCommand;
import com.telehealth.modules.emergency.domain.model.Emergency;
import com.telehealth.modules.emergency.domain.repository.EmergencyRepository;
import com.telehealth.modules.emergency.infrastructure.messaging.EmergencyEventPublisher;
import com.telehealth.shared.exceptions.TeleHealthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmergencyApplicationService {

    private final EmergencyRepository emergencyRepository;
    private final EmergencyEventPublisher eventPublisher;

    public UUID handle(EmergencyCommand.TriggerEmergency cmd) {
        log.warn("🚨 EMERGENCY TRIGGERED by patient: {} type: {}", cmd.patientId(), cmd.type());

        if (emergencyRepository.hasActiveEmergency(cmd.patientId())) {
            throw new TeleHealthException.ConflictException(
                    "Patient already has an active emergency. Contact support if this is an error.");
        }

        Emergency emergency = Emergency.trigger(
                cmd.patientId(), cmd.patientName(), cmd.patientPhone(),
                cmd.location(), cmd.latitude(), cmd.longitude(),
                cmd.type(), cmd.severity()
        );

        Emergency saved = emergencyRepository.save(emergency);
        eventPublisher.publishEmergencyTriggered(saved);

        log.warn("🚨 Emergency created: {} severity: {}", saved.getId(), saved.getSeverity());
        return saved.getId();
    }

    public void handle(EmergencyCommand.DispatchResponder cmd) {
        Emergency emergency = getOrThrow(cmd.emergencyId());
        emergency.dispatch(cmd.responderId(), cmd.responderName(), cmd.responderPhone(), cmd.etaMinutes());
        emergencyRepository.save(emergency);
        eventPublisher.publishEmergencyDispatched(emergency);
        log.info("Responder dispatched to emergency: {}", cmd.emergencyId());
    }

    public void handle(EmergencyCommand.MarkEnRoute cmd) {
        Emergency emergency = getOrThrow(cmd.emergencyId());
        emergency.markEnRoute();
        emergencyRepository.save(emergency);
        log.info("Responder en route to emergency: {}", cmd.emergencyId());
    }

    public void handle(EmergencyCommand.MarkOnScene cmd) {
        Emergency emergency = getOrThrow(cmd.emergencyId());
        emergency.markOnScene();
        emergencyRepository.save(emergency);
        log.info("Responder on scene for emergency: {}", cmd.emergencyId());
    }

    public void handle(EmergencyCommand.ResolveEmergency cmd) {
        Emergency emergency = getOrThrow(cmd.emergencyId());
        emergency.resolve(cmd.resolutionNotes());
        emergencyRepository.save(emergency);
        eventPublisher.publishEmergencyResolved(emergency);
        log.info("Emergency resolved: {}", cmd.emergencyId());
    }

    public void handle(EmergencyCommand.CancelEmergency cmd) {
        Emergency emergency = getOrThrow(cmd.emergencyId());
        emergency.cancel(cmd.reason());
        emergencyRepository.save(emergency);
        log.info("Emergency cancelled: {} reason: {}", cmd.emergencyId(), cmd.reason());
    }

    @Transactional(readOnly = true)
    public Emergency getById(UUID id) { return getOrThrow(id); }

    @Transactional(readOnly = true)
    public List<Emergency> getActiveEmergencies() { return emergencyRepository.findActive(); }

    @Transactional(readOnly = true)
    public List<Emergency> getByPatient(UUID patientId) { return emergencyRepository.findByPatientId(patientId); }

    private Emergency getOrThrow(UUID id) {
        return emergencyRepository.findById(id)
                .orElseThrow(() -> new TeleHealthException.EntityNotFoundException("Emergency", id));
    }
}
