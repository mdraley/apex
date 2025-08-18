package com.apex.document.domain;

/**
 * Processing status for document lifecycle
 */
public enum ProcessingStatus {
    UPLOADED,      // Document uploaded but not yet processed
    PROCESSING,    // OCR and classification in progress
    COMPLETED,     // Processing completed successfully
    FAILED,        // Processing failed
    ARCHIVED       // Document archived
}
