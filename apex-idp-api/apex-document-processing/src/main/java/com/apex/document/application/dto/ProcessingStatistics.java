package com.apex.document.application.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for processing statistics
 */
@Data
@Builder
public class ProcessingStatistics {
    private long totalDocuments;
    private long documentsProcessed;
    private long documentsInProgress;
    private long documentsFailed;
    private long documentsValidated;
    private double averageProcessingTime;
    private double successRate;
    private Map<String, Long> processingByType;
    private Map<String, Long> processingByStage;
    private LocalDateTime lastUpdated;
    
    // Constructors
    public ProcessingStatistics() {}
    
    public ProcessingStatistics(long totalDocuments, long documentsProcessed, 
                              long documentsInProgress, long documentsFailed,
                              long documentsValidated, double averageProcessingTime,
                              double successRate, Map<String, Long> processingByType,
                              Map<String, Long> processingByStage, LocalDateTime lastUpdated) {
        this.totalDocuments = totalDocuments;
        this.documentsProcessed = documentsProcessed;
        this.documentsInProgress = documentsInProgress;
        this.documentsFailed = documentsFailed;
        this.documentsValidated = documentsValidated;
        this.averageProcessingTime = averageProcessingTime;
        this.successRate = successRate;
        this.processingByType = processingByType;
        this.processingByStage = processingByStage;
        this.lastUpdated = lastUpdated;
    }
    
    // Getters and setters
    public long getTotalDocuments() { return totalDocuments; }
    public void setTotalDocuments(long totalDocuments) { this.totalDocuments = totalDocuments; }
    
    public long getDocumentsProcessed() { return documentsProcessed; }
    public void setDocumentsProcessed(long documentsProcessed) { this.documentsProcessed = documentsProcessed; }
    
    public long getDocumentsInProgress() { return documentsInProgress; }
    public void setDocumentsInProgress(long documentsInProgress) { this.documentsInProgress = documentsInProgress; }
    
    public long getDocumentsFailed() { return documentsFailed; }
    public void setDocumentsFailed(long documentsFailed) { this.documentsFailed = documentsFailed; }
    
    public long getDocumentsValidated() { return documentsValidated; }
    public void setDocumentsValidated(long documentsValidated) { this.documentsValidated = documentsValidated; }
    
    public double getAverageProcessingTime() { return averageProcessingTime; }
    public void setAverageProcessingTime(double averageProcessingTime) { this.averageProcessingTime = averageProcessingTime; }
    
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    
    public Map<String, Long> getProcessingByType() { return processingByType; }
    public void setProcessingByType(Map<String, Long> processingByType) { this.processingByType = processingByType; }
    
    public Map<String, Long> getProcessingByStage() { return processingByStage; }
    public void setProcessingByStage(Map<String, Long> processingByStage) { this.processingByStage = processingByStage; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
