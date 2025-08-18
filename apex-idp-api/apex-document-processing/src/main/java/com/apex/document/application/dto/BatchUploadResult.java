package com.apex.document.application.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

/**
 * Result of batch upload operation
 */
@Data
@Builder
public class BatchUploadResult {
    private String batchId;
    private int totalFiles;
    private int successfulUploads;
    private int failedUploads;
    private List<DocumentUploadResponse> results;
    private List<UUID> documentIds;
}
