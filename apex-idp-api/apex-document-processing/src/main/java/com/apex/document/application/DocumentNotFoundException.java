package com.apex.document.application;

import java.util.UUID;

/**
 * Exception thrown when a document is not found
 */
public class DocumentNotFoundException extends RuntimeException {
    
    public DocumentNotFoundException(UUID documentId) {
        super("Document not found with ID: " + documentId);
    }
    
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
