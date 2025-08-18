package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Event published when a field value is corrected during validation
 */
@Getter
public class FieldCorrectedEvent extends DomainEvent {
    private final String fieldName;
    private final String originalValue;
    private final String correctedValue;
    
    public FieldCorrectedEvent(UUID aggregateId, Long aggregateVersion, String fieldName, 
                              String originalValue, String correctedValue) {
        super(aggregateId, aggregateVersion, "validator");
        this.fieldName = fieldName;
        this.originalValue = originalValue;
        this.correctedValue = correctedValue;
    }
    
    @Override
    public Object getEventData() {
        return new FieldCorrectedEventData(fieldName, originalValue, correctedValue);
    }
    
    public record FieldCorrectedEventData(String fieldName, String originalValue, String correctedValue) {}
}
