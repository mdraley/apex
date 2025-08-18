package com.apex.core.events;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

/**
 * Base domain event that captures all the necessary metadata
 * for event sourcing and audit trails.
 */
@Getter
public abstract class DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final String eventType;
    private final UUID aggregateId;
    private final Long aggregateVersion;
    private final String userId;
    
    protected DomainEvent(UUID aggregateId, Long aggregateVersion, String userId) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.eventType = this.getClass().getSimpleName();
        this.aggregateId = aggregateId;
        this.aggregateVersion = aggregateVersion;
        this.userId = userId;
    }
    
    /**
     * Returns the domain event data for serialization
     */
    public abstract Object getEventData();
    
    /**
     * Determines if this event should be published externally
     */
    public boolean isPublishable() {
        return true;
    }
}
