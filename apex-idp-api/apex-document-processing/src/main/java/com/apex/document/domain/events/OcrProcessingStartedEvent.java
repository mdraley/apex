package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Event published when OCR processing starts for a document
 */
@Getter
public class OcrProcessingStartedEvent extends DomainEvent {
    
    public OcrProcessingStartedEvent(UUID aggregateId, Long aggregateVersion) {
        super(aggregateId, aggregateVersion, "system");
    }
    
    @Override
    public Object getEventData() {
        return new OcrProcessingStartedEventData();
    }
    
    public record OcrProcessingStartedEventData() {}
}
