package com.apex.document.infrastructure.projection;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Read-side projection of Document for CQRS pattern
 */
@Entity
@Table(name = "document_projections")
public class DocumentProjection {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String documentType;
    
    @Column(nullable = false)
    private String stage;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    private LocalDateTime lastModified;
    
    @ElementCollection
    @CollectionTable(name = "document_extracted_fields", joinColumns = @JoinColumn(name = "document_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    private Map<String, String> extractedFields;
    
    private Double confidenceScore;
    
    private String storagePath;
    
    private boolean isValid;
    
    @Column(length = 1000)
    private String validationErrors;
    
    // Constructors
    public DocumentProjection() {}
    
    public DocumentProjection(String id, String filename, String documentType, String stage,
                            String userId, LocalDateTime uploadedAt, LocalDateTime lastModified,
                            Map<String, String> extractedFields, Double confidenceScore,
                            String storagePath, boolean isValid, String validationErrors) {
        this.id = id;
        this.filename = filename;
        this.documentType = documentType;
        this.stage = stage;
        this.userId = userId;
        this.uploadedAt = uploadedAt;
        this.lastModified = lastModified;
        this.extractedFields = extractedFields;
        this.confidenceScore = confidenceScore;
        this.storagePath = storagePath;
        this.isValid = isValid;
        this.validationErrors = validationErrors;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public Map<String, String> getExtractedFields() { return extractedFields; }
    public void setExtractedFields(Map<String, String> extractedFields) { this.extractedFields = extractedFields; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    
    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
    
    public String getValidationErrors() { return validationErrors; }
    public void setValidationErrors(String validationErrors) { this.validationErrors = validationErrors; }
    
    // Additional getters needed by services
    public String getFileName() { return filename; }
    public String getType() { return documentType; }
    public String getStatus() { return stage; } // Using stage as status for compatibility
    public String getVendorName() { return ""; } // TODO: Add vendor name field
    public LocalDateTime getCreatedAt() { return uploadedAt; }
    public LocalDateTime getUpdatedAt() { return lastModified; }
    public LocalDateTime getProcessedAt() { return lastModified; }
    public Double getExtractionConfidence() { return confidenceScore; }
    public String getProcessingErrors() { return validationErrors; }
}
