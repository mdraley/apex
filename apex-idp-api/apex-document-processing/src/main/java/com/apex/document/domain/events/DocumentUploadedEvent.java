package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Event published when a document is uploaded to the system
 */
@Getter
public class DocumentUploadedEvent extends DomainEvent {
    private final String fileName;
    private final UUID vendorId;
    
    public DocumentUploadedEvent(UUID aggregateId, Long aggregateVersion, String fileName, UUID vendorId) {
        super(aggregateId, aggregateVersion, "system");
        this.fileName = fileName;
        this.vendorId = vendorId;
    }
    
    @Override
    public Object getEventData() {
        return new DocumentUploadedEventData(fileName, vendorId);
    }
    
    public record DocumentUploadedEventData(String fileName, UUID vendorId) {}
}
