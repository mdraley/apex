package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Event published when a document is automatically approved due to high confidence
 */
@Getter
public class DocumentAutoApprovedEvent extends DomainEvent {
    
    public DocumentAutoApprovedEvent(UUID aggregateId, Long aggregateVersion) {
        super(aggregateId, aggregateVersion, "system");
    }
    
    @Override
    public Object getEventData() {
        return new DocumentAutoApprovedEventData();
    }
    
    public record DocumentAutoApprovedEventData() {}
}
