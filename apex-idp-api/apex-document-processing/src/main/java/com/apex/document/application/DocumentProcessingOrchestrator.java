package com.apex.document.application;

import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Orchestrates the document processing pipeline
 */
@Service
public class DocumentProcessingOrchestrator {
    
    public void startProcessingPipeline(UUID documentId) {
        // TODO: Implement async processing pipeline
        System.out.println("Starting processing pipeline for document: " + documentId);
    }
    
    public void proceedToClassification(UUID documentId) {
        // TODO: Implement classification step
        System.out.println("Proceeding to classification for document: " + documentId);
    }
    
    public void proceedToExtraction(UUID documentId) {
        // TODO: Implement extraction step
        System.out.println("Proceeding to extraction for document: " + documentId);
    }
}
