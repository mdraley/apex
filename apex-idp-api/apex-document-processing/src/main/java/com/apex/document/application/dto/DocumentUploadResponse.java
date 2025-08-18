package com.apex.document.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Response DTO for document upload operations
 */
@Data
@Builder
public class DocumentUploadResponse {
    private UUID documentId;
    private String fileName;
    private String status;
    private String message;
}
