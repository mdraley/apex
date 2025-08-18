package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Event published when a document is rejected
 */
@Getter
public class DocumentRejectedEvent extends DomainEvent {
    private final String rejectedBy;
    private final String reason;
    
    public DocumentRejectedEvent(UUID aggregateId, Long aggregateVersion, String rejectedBy, String reason) {
        super(aggregateId, aggregateVersion, rejectedBy);
        this.rejectedBy = rejectedBy;
        this.reason = reason;
    }
    
    @Override
    public Object getEventData() {
        return new DocumentRejectedEventData(rejectedBy, reason);
    }
    
    public record DocumentRejectedEventData(String rejectedBy, String reason) {}
}
