package com.apex.document.domain.events;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Event published when an error occurs during document processing
 */
@Getter
public class DocumentProcessingErrorEvent extends DomainEvent {
    private final String errorType;
    private final String errorMessage;
    
    public DocumentProcessingErrorEvent(UUID aggregateId, Long aggregateVersion, 
                                      String errorType, String errorMessage) {
        super(aggregateId, aggregateVersion, "system");
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }
    
    @Override
    public Object getEventData() {
        return new DocumentProcessingErrorEventData(errorType, errorMessage);
    }
    
    public record DocumentProcessingErrorEventData(String errorType, String errorMessage) {}
}
