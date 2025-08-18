package com.apex.document.application;

import com.apex.document.application.dto.DocumentDTO;
import com.apex.document.application.dto.DocumentStatusDTO;
import com.apex.document.application.dto.DocumentValidationDTO;
import com.apex.document.application.dto.ProcessingStatistics;
import com.apex.document.application.dto.SearchCriteria;
import com.apex.document.application.dto.ValidationQueueDTO;
import com.apex.document.infrastructure.projection.DocumentProjection;
import com.apex.document.infrastructure.projection.DocumentProjectionRepository;
import com.apex.document.infrastructure.search.DocumentSearchService;
import com.apex.document.infrastructure.storage.DocumentStorageService;
import com.apex.document.domain.ProcessingStage;
import com.apex.document.domain.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Query service for document read operations.
 * This is the read side of our CQRS implementation, optimized for queries.
 */
@Service
@RequiredArgsConstructor
public class DocumentQueryService {
    
    private final DocumentProjectionRepository projectionRepository;
    private final DocumentSearchService searchService;
    private final DocumentStorageService storageService;
    private static final String PENDING_REVIEW_STATUS = "PENDING_REVIEW";
    
    /**
     * Get documents pending validation for AP Clerks.
     * Returns documents with confidence below threshold that need review.
     */
    public Page<ValidationQueueDTO> getValidationQueue(Pageable pageable) {
        return projectionRepository.findByStageAndConfidenceScoreLessThan(
            PENDING_REVIEW_STATUS,
            0.80,
            pageable
        ).map(this::toValidationQueueDTO);
    }
    
    /**
     * Get document details with extracted fields for validation UI.
     */
    public DocumentValidationDTO getDocumentForValidation(String documentId) {
        DocumentProjection projection = projectionRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        return DocumentValidationDTO.builder()
            .documentId(projection.getId())
            .filename(projection.getFileName())
            .stage(ProcessingStage.valueOf(projection.getStage()))
            .isValid(projection.isValid())
            .extractedFields(convertToObjectMap(projection.getExtractedFields()))
            .overallConfidence(projection.getExtractionConfidence())
            .build();
    }
    
    /**
     * Search documents using Elasticsearch for full-text search.
     */
    public Page<DocumentDTO> searchDocuments(SearchCriteria criteria, Pageable pageable) {
        return searchService.search(criteria, pageable)
            .map(this::toDocumentDTO);
    }
    
    /**
     * Get processing statistics for dashboard.
     */
    public ProcessingStatistics getProcessingStatistics() {
        return ProcessingStatistics.builder()
            .totalDocuments(projectionRepository.count())
            .documentsProcessed(projectionRepository.countByStage(PENDING_REVIEW_STATUS))
            .documentsInProgress(projectionRepository.countByCreatedAtAfter(java.time.LocalDate.now().atStartOfDay()))
            .averageProcessingTime(projectionRepository.calculateAverageConfidence() != null ? 
                projectionRepository.calculateAverageConfidence() : 0.0)
            .successRate(0.95) // TODO: Calculate actual success rate
            .lastUpdated(java.time.LocalDateTime.now())
            .build();
    }
    
    /**
     * Get document status for real-time updates
     */
    public DocumentStatusDTO getDocumentStatus(String documentId) {
        DocumentProjection projection = projectionRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        return DocumentStatusDTO.builder()
            .id(projection.getId())
            .filename(projection.getFileName())
            .stage(ProcessingStage.valueOf(projection.getStage()))
            .status(projection.getStatus())
            .lastUpdated(projection.getUpdatedAt())
            .build();
    }
    
    private ValidationQueueDTO toValidationQueueDTO(DocumentProjection projection) {
        return ValidationQueueDTO.builder()
            .id(UUID.fromString(projection.getId()))
            .filename(projection.getFileName())
            .documentType(projection.getType())
            .status(projection.getStatus())
            .confidence(projection.getExtractionConfidence() != null ? 
                java.math.BigDecimal.valueOf(projection.getExtractionConfidence()) : 
                java.math.BigDecimal.ZERO)
            .submittedAt(projection.getCreatedAt())
            .priority(1) // Default priority
            .validationStage("PENDING")
            .build();
    }
    
    private DocumentDTO toDocumentDTO(DocumentProjection projection) {
        return DocumentDTO.builder()
            .id(projection.getId())
            .filename(projection.getFileName())
            .type(DocumentType.valueOf(projection.getType()))
            .stage(ProcessingStage.valueOf(projection.getStage()))
            .userId(projection.getUserId())
            .uploadedAt(projection.getCreatedAt())
            .lastModified(projection.getUpdatedAt())
            .extractedFields(convertToObjectMap(projection.getExtractedFields()))
            .confidenceScore(projection.getExtractionConfidence())
            .storagePath(projection.getStoragePath())
            .build();
    }
    
    private String generateSecureUrl(String storagePath) {
        // Generate pre-signed URL for secure document access
        return storageService.generatePresignedUrl(storagePath);
    }
    
    private int calculateProgress(ProcessingStage stage) {
        return switch (stage) {
            case PENDING_OCR -> 10;
            case OCR_IN_PROGRESS -> 25;
            case PENDING_CLASSIFICATION -> 40;
            case PENDING_EXTRACTION -> 60;
            case PENDING_VALIDATION -> 80;
            case COMPLETED -> 100;
            default -> 0;
        };
    }
    
    private String calculatePriority(DocumentProjection projection) {
        // Calculate priority based on confidence, age, and vendor tier
        BigDecimal confidence = projection.getExtractionConfidence() != null ? 
            BigDecimal.valueOf(projection.getExtractionConfidence()) : BigDecimal.ZERO;
        long ageHours = java.time.Duration.between(projection.getCreatedAt(), java.time.Instant.now()).toHours();
        
        if (confidence.compareTo(new BigDecimal("0.50")) < 0 || ageHours > 24) {
            return "HIGH";
        } else if (confidence.compareTo(new BigDecimal("0.70")) < 0 || ageHours > 8) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    public String generateSecureDownloadUrl(String documentId) {
        // Generate a secure download URL with expiry
        // This is a placeholder implementation
        return "/api/documents/" + documentId + "/download?token=" + UUID.randomUUID();
    }
    
    // Helper methods
    private Map<String, Object> convertToObjectMap(Map<String, String> stringMap) {
        if (stringMap == null) return Map.of();
        return stringMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
