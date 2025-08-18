package com.apex.document.domain;

/**
 * Represents the current status of a document in the system
 */
public enum DocumentStatus {
    UPLOADED,           // Document has been uploaded
    PROCESSING,         // Document is being processed
    PENDING_REVIEW,     // Document needs human validation
    APPROVED,           // Document has been approved
    REJECTED,           // Document has been rejected
    ERROR               // Document processing encountered an error
}
