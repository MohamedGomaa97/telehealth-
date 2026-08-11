package com.telehealth.shared.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all Domain Events.
 * Domain events are published via RabbitMQ for cross-module communication.
 * Each module listens to relevant events and reacts accordingly.
 */
public abstract class DomainEvent {

    private final UUID eventId;
    private final String eventType;
    private final LocalDateTime occurredAt;
    private final String sourceModule;

    protected DomainEvent(String eventType, String sourceModule) {
        this.eventId = UUID.randomUUID();
        this.eventType = eventType;
        this.sourceModule = sourceModule;
        this.occurredAt = LocalDateTime.now();
    }

    public UUID getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public String getSourceModule() { return sourceModule; }

    @Override
    public String toString() {
        return String.format("DomainEvent{eventId=%s, type='%s', source='%s', at=%s}",
                eventId, eventType, sourceModule, occurredAt);
    }
}
