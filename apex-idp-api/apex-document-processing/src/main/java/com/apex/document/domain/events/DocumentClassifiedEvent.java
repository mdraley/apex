package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import com.apex.document.domain.DocumentType;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event published when a document is classified
 */
@Getter
public class DocumentClassifiedEvent extends DomainEvent {
    private final DocumentType documentType;
    private final BigDecimal confidence;
    
    public DocumentClassifiedEvent(UUID aggregateId, Long aggregateVersion, 
                                 DocumentType documentType, BigDecimal confidence) {
        super(aggregateId, aggregateVersion, "system");
        this.documentType = documentType;
        this.confidence = confidence;
    }
    
    @Override
    public Object getEventData() {
        return new DocumentClassifiedEventData(documentType, confidence);
    }
    
    public record DocumentClassifiedEventData(DocumentType documentType, BigDecimal confidence) {}
}
