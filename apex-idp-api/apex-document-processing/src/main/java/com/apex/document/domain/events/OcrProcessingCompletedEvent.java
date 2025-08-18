package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Event published when OCR processing completes for a document
 */
@Getter
public class OcrProcessingCompletedEvent extends DomainEvent {
    private final Integer pageCount;
    
    public OcrProcessingCompletedEvent(UUID aggregateId, Long aggregateVersion, Integer pageCount) {
        super(aggregateId, aggregateVersion, "system");
        this.pageCount = pageCount;
    }
    
    @Override
    public Object getEventData() {
        return new OcrProcessingCompletedEventData(pageCount);
    }
    
    public record OcrProcessingCompletedEventData(Integer pageCount) {}
}
