package com.apex.document.application.dto;

import com.apex.document.domain.ProcessingStage;
import com.apex.document.domain.DocumentType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for document information
 */
@Data
@Builder
public class DocumentDTO {
    private String id;
    private String filename;
    private DocumentType type;
    private ProcessingStage stage;
    private String userId;
    private LocalDateTime uploadedAt;
    private LocalDateTime lastModified;
    private Map<String, Object> extractedFields;
    private List<String> validationErrors;
    private Double confidenceScore;
    private String storagePath;
    
    // Constructors
    public DocumentDTO() {}
    
    public DocumentDTO(String id, String filename, DocumentType type, ProcessingStage stage, 
                      String userId, LocalDateTime uploadedAt, LocalDateTime lastModified,
                      Map<String, Object> extractedFields, List<String> validationErrors,
                      Double confidenceScore, String storagePath) {
        this.id = id;
        this.filename = filename;
        this.type = type;
        this.stage = stage;
        this.userId = userId;
        this.uploadedAt = uploadedAt;
        this.lastModified = lastModified;
        this.extractedFields = extractedFields;
        this.validationErrors = validationErrors;
        this.confidenceScore = confidenceScore;
        this.storagePath = storagePath;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public DocumentType getType() { return type; }
    public void setType(DocumentType type) { this.type = type; }
    
    public ProcessingStage getStage() { return stage; }
    public void setStage(ProcessingStage stage) { this.stage = stage; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public Map<String, Object> getExtractedFields() { return extractedFields; }
    public void setExtractedFields(Map<String, Object> extractedFields) { this.extractedFields = extractedFields; }
    
    public List<String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(List<String> validationErrors) { this.validationErrors = validationErrors; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
}
