package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event published when a field is extracted from a document
 */
@Getter
public class FieldExtractedEvent extends DomainEvent {
    private final String fieldName;
    private final BigDecimal confidence;
    
    public FieldExtractedEvent(UUID aggregateId, Long aggregateVersion, String fieldName, BigDecimal confidence) {
        super(aggregateId, aggregateVersion, "system");
        this.fieldName = fieldName;
        this.confidence = confidence;
    }
    
    @Override
    public Object getEventData() {
        return new FieldExtractedEventData(fieldName, confidence);
    }
    
    public record FieldExtractedEventData(String fieldName, BigDecimal confidence) {}
}
