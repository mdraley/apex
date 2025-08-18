package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Event published when a document is approved by a human validator
 */
@Getter
public class DocumentApprovedEvent extends DomainEvent {
    private final String approvedBy;
    
    public DocumentApprovedEvent(UUID aggregateId, Long aggregateVersion, String approvedBy) {
        super(aggregateId, aggregateVersion, approvedBy);
        this.approvedBy = approvedBy;
    }
    
    @Override
    public Object getEventData() {
        return new DocumentApprovedEventData(approvedBy);
    }
    
    public record DocumentApprovedEventData(String approvedBy) {}
}
