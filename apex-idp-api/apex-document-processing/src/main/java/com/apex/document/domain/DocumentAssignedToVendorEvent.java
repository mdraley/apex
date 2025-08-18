package com.apex.document.domain;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event published when document is assigned to a vendor
 */
@Getter
public class DocumentAssignedToVendorEvent extends DomainEvent {
    
    private final UUID documentId;
    
    public DocumentAssignedToVendorEvent(UUID aggregateId, Long aggregateVersion, String userId) {
        super(aggregateId, aggregateVersion, userId);
        this.documentId = aggregateId;
    }
    
    @Override
    public Object getEventData() {
        return new DocumentEventData(documentId, "ASSIGNED_TO_VENDOR");
    }
    
    @Getter
    private static class DocumentEventData {
        private final UUID documentId;
        private final String action;
        
        public DocumentEventData(UUID documentId, String action) {
            this.documentId = documentId;
            this.action = action;
        }
    }
}
