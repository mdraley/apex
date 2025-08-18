package com.apex.document.application.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * DTO for batch upload response
 */
@Data
@Builder
public class BatchUploadResponse {
    private String batchId;
    private int totalFiles;
    private int successfulUploads;
    private int failedUploads;
    private List<DocumentUploadResponse> uploadResults;
    private Map<String, String> errors;
    
    // Constructors
    public BatchUploadResponse() {}
    
    public BatchUploadResponse(String batchId, int totalFiles, int successfulUploads,
                             int failedUploads, List<DocumentUploadResponse> uploadResults,
                             Map<String, String> errors) {
        this.batchId = batchId;
        this.totalFiles = totalFiles;
        this.successfulUploads = successfulUploads;
        this.failedUploads = failedUploads;
        this.uploadResults = uploadResults;
        this.errors = errors;
    }
    
    // Getters and setters
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    
    public int getTotalFiles() { return totalFiles; }
    public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }
    
    public int getSuccessfulUploads() { return successfulUploads; }
    public void setSuccessfulUploads(int successfulUploads) { this.successfulUploads = successfulUploads; }
    
    public int getFailedUploads() { return failedUploads; }
    public void setFailedUploads(int failedUploads) { this.failedUploads = failedUploads; }
    
    public List<DocumentUploadResponse> getUploadResults() { return uploadResults; }
    public void setUploadResults(List<DocumentUploadResponse> uploadResults) { this.uploadResults = uploadResults; }
    
    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
