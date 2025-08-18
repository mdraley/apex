package com.apex.document.domain;

import com.apex.core.domain.AggregateRoot;
import com.apex.core.events.DomainEvent;
import com.apex.document.domain.events.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Document aggregate root representing an invoice or other document
 * going through the IDP pipeline.
 */
@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor
@Slf4j
public class Document extends AggregateRoot<Document> {
    
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    
    @Enumerated(EnumType.STRING)
    private ProcessingStage stage;
    
    @Column(name = "vendor_id")
    private UUID vendorId;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "storage_path")
    private String storagePath;
    
    @Column(name = "page_count")
    private Integer pageCount;
    
    // Classification confidence score
    @Column(name = "classification_confidence")
    private BigDecimal classificationConfidence;
    
    // Overall extraction confidence
    @Column(name = "extraction_confidence")
    private BigDecimal extractionConfidence;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private List<ExtractedField> extractedFields = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private List<ProcessingError> processingErrors = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "document_metadata")
    private Map<String, String> metadata = new HashMap<>();
    
    
    /**
     * Factory method to create a new document from an upload.
     * This triggers the DocumentUploaded event for the event store.
     */
    public static Document createFromUpload(
            String fileName,
            Long fileSize,
            String mimeType,
            String storagePath,
            UUID vendorId) {
        
        Document document = new Document();
        document.fileName = fileName;
        document.fileSize = fileSize;
        document.mimeType = mimeType;
        document.storagePath = storagePath;
        document.vendorId = vendorId;
        document.status = DocumentStatus.UPLOADED;
        document.stage = ProcessingStage.PENDING_OCR;
        
        // Register the domain event for event sourcing
        document.registerEvent(new DocumentUploadedEvent(
            document.getId(),
            document.getVersion(),
            fileName,
            vendorId
        ));
        
        return document;
    }
    
    /**
     * Start OCR processing for this document.
     * Validates that the document is in the correct state.
     */
    public void startOcrProcessing() {
        if (this.stage != ProcessingStage.PENDING_OCR) {
            throw new IllegalStateException(
                "Document must be in PENDING_OCR stage to start OCR processing"
            );
        }
        
        this.stage = ProcessingStage.OCR_IN_PROGRESS;
        this.status = DocumentStatus.PROCESSING;
        
        registerEvent(new OcrProcessingStartedEvent(
            this.getId(),
            this.getVersion()
        ));
    }
    
    /**
     * Complete OCR processing with extracted text.
     * Moves document to classification stage.
     */
    public void completeOcrProcessing(String extractedText, Integer pageCount) {
        if (this.stage != ProcessingStage.OCR_IN_PROGRESS) {
            throw new IllegalStateException(
                "Document must be in OCR_IN_PROGRESS stage to complete OCR"
            );
        }
        
        this.pageCount = pageCount;
        this.stage = ProcessingStage.PENDING_CLASSIFICATION;
        this.metadata.put("extracted_text_length", String.valueOf(extractedText.length()));
        
        registerEvent(new OcrProcessingCompletedEvent(
            this.getId(),
            this.getVersion(),
            pageCount
        ));
    }
    
    /**
     * Apply AI classification to determine document type.
     * If confidence is below threshold, route to manual classification.
     */
    public void applyClassification(DocumentType type, BigDecimal confidence) {
        this.type = type;
        this.classificationConfidence = confidence;
        
        // Route based on confidence threshold (70% as per requirements)
        if (confidence.compareTo(new BigDecimal("0.70")) >= 0) {
            this.stage = ProcessingStage.PENDING_EXTRACTION;
            registerEvent(new DocumentClassifiedEvent(
                this.getId(),
                this.getVersion(),
                type,
                confidence
            ));
        } else {
            this.stage = ProcessingStage.PENDING_MANUAL_CLASSIFICATION;
            registerEvent(new DocumentNeedsManualClassificationEvent(
                this.getId(),
                this.getVersion(),
                confidence
            ));
        }
    }
    
    /**
     * Add extracted field data from AI processing.
     * Each field includes its own confidence score.
     */
    public void addExtractedField(ExtractedField field) {
        this.extractedFields.add(field);
        recalculateExtractionConfidence();
        
        registerEvent(new FieldExtractedEvent(
            this.getId(),
            this.getVersion(),
            field.getFieldName(),
            field.getConfidence()
        ));
    }
    
    /**
     * Validate extracted data and determine if human review is needed.
     * Uses 80% threshold as specified in requirements.
     */
    public void validateExtraction() {
        BigDecimal threshold = new BigDecimal("0.80");
        
        boolean needsReview = extractedFields.stream()
            .anyMatch(field -> field.getConfidence().compareTo(threshold) < 0);
        
        if (needsReview) {
            this.stage = ProcessingStage.PENDING_VALIDATION;
            this.status = DocumentStatus.PENDING_REVIEW;
            registerEvent(new DocumentNeedsValidationEvent(
                this.getId(),
                this.getVersion(),
                this.extractionConfidence
            ));
        } else {
            this.stage = ProcessingStage.COMPLETED;
            this.status = DocumentStatus.APPROVED;
            registerEvent(new DocumentAutoApprovedEvent(
                this.getId(),
                this.getVersion()
            ));
        }
    }
    
    /**
     * Approve document after human validation
     */
    public void approve(String userId) {
        if (this.stage != ProcessingStage.PENDING_VALIDATION) {
            throw new IllegalStateException("Document must be pending validation to approve");
        }
        
        this.stage = ProcessingStage.COMPLETED;
        this.status = DocumentStatus.APPROVED;
        
        registerEvent(new DocumentApprovedEvent(
            this.getId(),
            this.getVersion(),
            userId
        ));
    }
    
    /**
     * Reject document with reason
     */
    public void reject(String userId, String reason) {
        this.status = DocumentStatus.REJECTED;
        this.metadata.put("rejection_reason", reason);
        this.metadata.put("rejected_by", userId);
        
        registerEvent(new DocumentRejectedEvent(
            this.getId(),
            this.getVersion(),
            userId,
            reason
        ));
    }
    
    /**
     * Correct a field value during validation
     */
    public void correctField(String fieldName, String correctedValue) {
        ExtractedField field = extractedFields.stream()
            .filter(f -> f.getFieldName().equals(fieldName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Field not found: " + fieldName));
        
        String originalValue = field.getValue();
        field.setValue(correctedValue);
        field.setConfidence(new BigDecimal("1.00")); // Human correction has 100% confidence
        
        registerEvent(new FieldCorrectedEvent(
            this.getId(),
            this.getVersion(),
            fieldName,
            originalValue,
            correctedValue
        ));
        
        recalculateExtractionConfidence();
    }
    
    /**
     * Record processing error
     */
    public void recordProcessingError(String errorType, String errorMessage) {
        ProcessingError error = new ProcessingError(errorType, errorMessage);
        this.processingErrors.add(error);
        this.status = DocumentStatus.ERROR;
        
        registerEvent(new DocumentProcessingErrorEvent(
            this.getId(),
            this.getVersion(),
            errorType,
            errorMessage
        ));
    }
    
    private void recalculateExtractionConfidence() {
        if (extractedFields.isEmpty()) {
            this.extractionConfidence = BigDecimal.ZERO;
            return;
        }
        
        BigDecimal sum = extractedFields.stream()
            .map(ExtractedField::getConfidence)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.extractionConfidence = sum.divide(
            new BigDecimal(extractedFields.size()),
            2,
            BigDecimal.ROUND_HALF_UP
        );
    }
    
    @Override
    public void apply(DomainEvent event) {
        // Event replay logic for event sourcing
        // This allows us to reconstruct state from events
        if (event instanceof DocumentUploadedEvent e) {
            this.fileName = e.getFileName();
            this.vendorId = e.getVendorId();
            this.status = DocumentStatus.UPLOADED;
        } else if (event instanceof OcrProcessingCompletedEvent e) {
            this.pageCount = e.getPageCount();
            this.stage = ProcessingStage.PENDING_CLASSIFICATION;
        } else if (event instanceof DocumentClassifiedEvent e) {
            this.type = e.getDocumentType();
            this.classificationConfidence = e.getConfidence();
        } else {
            // Handle other events...
            System.out.println("Unknown event type: " + event.getClass());
        }
    }
    
    // Additional getters needed by services
    public String getStoragePath() {
        return storagePath;
    }
    
    public ProcessingStage getStage() {
        return stage;
    }
    
    public DocumentStatus getStatus() {
        return status;
    }
    
    // Required validate method from AggregateRoot
    @Override
    protected void validate() {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        if (fileSize == null || fileSize <= 0) {
            throw new IllegalArgumentException("File size must be positive");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
    }
}
