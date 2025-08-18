package com.apex.document.domain;

/**
 * Represents the current processing stage of a document in the IDP pipeline
 */
public enum ProcessingStage {
    PENDING_OCR,                        // Document awaiting OCR processing
    OCR_IN_PROGRESS,                    // OCR processing is running
    PENDING_CLASSIFICATION,             // Awaiting AI classification
    PENDING_MANUAL_CLASSIFICATION,      // Needs manual classification (low confidence)
    PENDING_EXTRACTION,                 // Awaiting field extraction
    PENDING_VALIDATION,                 // Needs human validation
    COMPLETED,                          // Processing complete
    ERROR                               // Processing error occurred
}
