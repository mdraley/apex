package com.apex.document.application.dto;

import com.apex.document.domain.ProcessingStage;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for document validation information
 */
@Data
@Builder
public class DocumentValidationDTO {
    private String documentId;
    private String filename;
    private ProcessingStage stage;
    private boolean isValid;
    private List<ValidationErrorDTO> errors;
    private List<ValidationWarningDTO> warnings;
    private Map<String, Object> extractedFields;
    private Map<String, Double> fieldConfidences;
    private Double overallConfidence;
    private LocalDateTime validatedAt;
    private String validatedBy;
    
    // Constructors
    public DocumentValidationDTO() {}
    
    public DocumentValidationDTO(String documentId, String filename, ProcessingStage stage,
                               boolean isValid, List<ValidationErrorDTO> errors, 
                               List<ValidationWarningDTO> warnings, Map<String, Object> extractedFields,
                               Map<String, Double> fieldConfidences, Double overallConfidence,
                               LocalDateTime validatedAt, String validatedBy) {
        this.documentId = documentId;
        this.filename = filename;
        this.stage = stage;
        this.isValid = isValid;
        this.errors = errors;
        this.warnings = warnings;
        this.extractedFields = extractedFields;
        this.fieldConfidences = fieldConfidences;
        this.overallConfidence = overallConfidence;
        this.validatedAt = validatedAt;
        this.validatedBy = validatedBy;
    }
    
    // Getters and setters
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public ProcessingStage getStage() { return stage; }
    public void setStage(ProcessingStage stage) { this.stage = stage; }
    
    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
    
    public List<ValidationErrorDTO> getErrors() { return errors; }
    public void setErrors(List<ValidationErrorDTO> errors) { this.errors = errors; }
    
    public List<ValidationWarningDTO> getWarnings() { return warnings; }
    public void setWarnings(List<ValidationWarningDTO> warnings) { this.warnings = warnings; }
    
    public Map<String, Object> getExtractedFields() { return extractedFields; }
    public void setExtractedFields(Map<String, Object> extractedFields) { this.extractedFields = extractedFields; }
    
    public Map<String, Double> getFieldConfidences() { return fieldConfidences; }
    public void setFieldConfidences(Map<String, Double> fieldConfidences) { this.fieldConfidences = fieldConfidences; }
    
    public Double getOverallConfidence() { return overallConfidence; }
    public void setOverallConfidence(Double overallConfidence) { this.overallConfidence = overallConfidence; }
    
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    
    public String getValidatedBy() { return validatedBy; }
    public void setValidatedBy(String validatedBy) { this.validatedBy = validatedBy; }
    
    // Helper DTOs
    public static class ValidationErrorDTO {
        private String field;
        private String message;
        private String severity;
        
        public ValidationErrorDTO() {}
        
        public ValidationErrorDTO(String field, String message, String severity) {
            this.field = field;
            this.message = message;
            this.severity = severity;
        }
        
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }
    
    public static class ValidationWarningDTO {
        private String field;
        private String message;
        private Double confidence;
        
        public ValidationWarningDTO() {}
        
        public ValidationWarningDTO(String field, String message, Double confidence) {
            this.field = field;
            this.message = message;
            this.confidence = confidence;
        }
        
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
    }
}
