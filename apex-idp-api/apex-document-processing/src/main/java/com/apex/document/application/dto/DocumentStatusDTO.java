package com.apex.document.application.dto;

import com.apex.document.domain.ProcessingStage;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO for document status information
 */
@Data
@Builder
public class DocumentStatusDTO {
    private String id;
    private String filename;
    private ProcessingStage stage;
    private String status;
    private int progress;
    private String currentTask;
    private LocalDateTime lastUpdated;
    private String estimatedCompletion;
    private String errorMessage;
    
    // Constructors
    public DocumentStatusDTO() {}
    
    public DocumentStatusDTO(String id, String filename, ProcessingStage stage, String status,
                           int progress, String currentTask, LocalDateTime lastUpdated,
                           String estimatedCompletion, String errorMessage) {
        this.id = id;
        this.filename = filename;
        this.stage = stage;
        this.status = status;
        this.progress = progress;
        this.currentTask = currentTask;
        this.lastUpdated = lastUpdated;
        this.estimatedCompletion = estimatedCompletion;
        this.errorMessage = errorMessage;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public ProcessingStage getStage() { return stage; }
    public void setStage(ProcessingStage stage) { this.stage = stage; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    
    public String getCurrentTask() { return currentTask; }
    public void setCurrentTask(String currentTask) { this.currentTask = currentTask; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public String getEstimatedCompletion() { return estimatedCompletion; }
    public void setEstimatedCompletion(String estimatedCompletion) { this.estimatedCompletion = estimatedCompletion; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
