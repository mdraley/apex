package com.apex.document.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an error that occurred during document processing
 */
@Entity
@Table(name = "processing_errors")
@Getter
@Setter
@NoArgsConstructor
public class ProcessingError {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "document_id", nullable = false)
    private UUID documentId;
    
    @Column(name = "error_type", nullable = false)
    private String errorType;
    
    @Column(name = "error_message", nullable = false)
    private String errorMessage;
    
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;
    
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
    
    public ProcessingError(String errorType, String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.occurredAt = Instant.now();
    }
    
    public ProcessingError(String errorType, String errorMessage, String stackTrace) {
        this(errorType, errorMessage);
        this.stackTrace = stackTrace;
    }
}
