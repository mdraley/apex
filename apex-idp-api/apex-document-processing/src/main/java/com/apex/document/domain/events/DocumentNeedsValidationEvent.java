package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event published when a document needs validation due to low confidence scores
 */
@Getter
public class DocumentNeedsValidationEvent extends DomainEvent {
    private final BigDecimal extractionConfidence;
    
    public DocumentNeedsValidationEvent(UUID aggregateId, Long aggregateVersion, BigDecimal extractionConfidence) {
        super(aggregateId, aggregateVersion, "system");
        this.extractionConfidence = extractionConfidence;
    }
    
    @Override
    public Object getEventData() {
        return new DocumentNeedsValidationEventData(extractionConfidence);
    }
    
    public record DocumentNeedsValidationEventData(BigDecimal extractionConfidence) {}
}
