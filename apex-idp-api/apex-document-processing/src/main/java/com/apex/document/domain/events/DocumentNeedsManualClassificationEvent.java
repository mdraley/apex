package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event published when a document needs manual classification due to low confidence
 */
@Getter
public class DocumentNeedsManualClassificationEvent extends DomainEvent {
    private final BigDecimal confidence;
    
    public DocumentNeedsManualClassificationEvent(UUID aggregateId, Long aggregateVersion, BigDecimal confidence) {
        super(aggregateId, aggregateVersion, "system");
        this.confidence = confidence;
    }
    
    @Override
    public Object getEventData() {
        return new DocumentNeedsManualClassificationEventData(confidence);
    }
    
    public record DocumentNeedsManualClassificationEventData(BigDecimal confidence) {}
}
