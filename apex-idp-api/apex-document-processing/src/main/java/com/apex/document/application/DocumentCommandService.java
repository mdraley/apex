package com.apex.document.application;

import com.apex.document.domain.Document;
import com.apex.document.domain.DocumentRepository;
import com.apex.document.domain.DocumentStatus;
import com.apex.document.domain.ProcessingStage;
import com.apex.document.infrastructure.storage.DocumentStorageService;
import com.apex.document.infrastructure.ocr.OcrService;
import com.apex.document.infrastructure.ocr.OcrResult;
import com.apex.document.application.dto.ValidationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import java.util.UUID;

/**
 * Command service handling all write operations for documents.
 * This is the write side of our CQRS implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentCommandService {
    
    private final DocumentRepository documentRepository;
    private final DocumentStorageService storageService;
    private final OcrService ocrService;
    private final DocumentProcessingOrchestrator orchestrator;
    
    // Stub services for now
    private final VirusScanService virusScanService = new VirusScanService();
    private final AIClassificationService aiClassificationService = new AIClassificationService();
    private final IntegrationService integrationService = new IntegrationService();
    private final DocumentLockService documentLockService = new DocumentLockService();
    
    /**
     * Upload a new document and initiate processing pipeline.
     * This is the entry point for document ingestion.
     */
    @Transactional
    public UUID uploadDocument(MultipartFile file, UUID vendorId) {
        log.info("Uploading document: {} for vendor: {}", file.getOriginalFilename(), vendorId);
        
        // Validate file
        validateFile(file);
        
        // Store file in MinIO
        String storagePath = storageService.storeDocument(file);
        
        // Create document aggregate
        Document document = Document.createFromUpload(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType(),
            storagePath,
            vendorId
        );
        
        // Save to repository - this will also publish domain events
        document = documentRepository.save(document);
        
        // Initiate async processing pipeline
        orchestrator.startProcessingPipeline(document.getId());
        
        log.info("Document uploaded successfully with ID: {}", document.getId());
        return document.getId();
    }
    
    /**
     * Process OCR for a document.
     * Called by the processing pipeline orchestrator.
     */
    @Transactional
    public void processOcr(UUID documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        try {
            document.startOcrProcessing();
            documentRepository.save(document);
            
            // Perform OCR using ensemble of engines
            OcrResult result = ocrService.performOcr(document.getStoragePath());
            
            document.completeOcrProcessing(result.getText(), result.getPageCount());
            documentRepository.save(document);
            
            // Continue to next stage
            orchestrator.proceedToClassification(documentId);
            
        } catch (Exception e) {
            log.error("OCR processing failed for document: {}", documentId, e);
            document.recordProcessingError("OCR_FAILED", e.getMessage());
            documentRepository.save(document);
        }
    }
    
    /**
     * Apply AI classification to determine document type.
     */
    @Transactional
    public void classifyDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        ClassificationResult result = aiClassificationService.classify(document);
        
        document.applyClassification(result.getType(), result.getConfidence());
        documentRepository.save(document);
        
        if (document.getStage() == ProcessingStage.PENDING_EXTRACTION) {
            orchestrator.proceedToExtraction(documentId);
        }
    }
    
    /**
     * Validate document after human review.
     * This is called when an AP Clerk approves/corrects extracted data.
     */
    @Transactional
    public void validateDocument(UUID documentId, ValidationRequest request) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        // Apply field corrections
        request.getFieldCorrections().forEach(correction -> {
            document.correctField(correction.getFieldName(), correction.getCorrectedValue());
        });
        
        // Update validation status
        if (request.isApproved()) {
            document.approve(request.getUserId().toString());
        } else {
            document.reject(request.getUserId().toString(), request.getRejectionReason());
        }
        
        documentRepository.save(document);
        
        // If approved, send to ERP integration
        if (document.getStatus() == DocumentStatus.APPROVED) {
            integrationService.sendToErp(documentId);
        }
    }
    
    /**
     * Lock document for validation to prevent concurrent editing
     */
    @Transactional
    public void lockDocumentForValidation(UUID documentId, String userId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        // Implementation would check if document is already locked
        // and set lock with expiration time
        documentLockService.lockDocument(documentId, userId);
    }
    
    private void validateFile(MultipartFile file) {
        // Check file size (50MB limit as per requirements)
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new FileTooLargeException("File exceeds 50MB limit");
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new InvalidFileTypeException("Unsupported file type: " + contentType);
        }
        
        // Virus scanning would go here
        virusScanService.scan(file);
    }
    
    private boolean isValidFileType(String contentType) {
        return contentType != null && (
            contentType.equals("application/pdf") ||
            contentType.startsWith("image/") ||
            contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
            contentType.equals("application/msword")
        );
    }
    
    /**
     * Upload batch of documents
     */
    @Transactional
    public com.apex.document.application.dto.BatchUploadResult uploadBatch(
            List<MultipartFile> files, String vendorIdStr, java.util.UUID userId) {
        
        java.util.UUID vendorId = java.util.UUID.fromString(vendorIdStr);
        List<com.apex.document.application.dto.DocumentUploadResponse> results = new java.util.ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (MultipartFile file : files) {
            try {
                java.util.UUID documentId = uploadDocument(file, vendorId);
                results.add(com.apex.document.application.dto.DocumentUploadResponse.builder()
                    .documentId(documentId)
                    .fileName(file.getOriginalFilename())
                    .status("SUCCESS")
                    .message("Document uploaded successfully")
                    .build());
                successCount++;
            } catch (Exception e) {
                results.add(com.apex.document.application.dto.DocumentUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .status("FAILED")
                    .message("Upload failed: " + e.getMessage())
                    .build());
                failureCount++;
            }
        }
        
        return com.apex.document.application.dto.BatchUploadResult.builder()
            .batchId(java.util.UUID.randomUUID().toString())
            .totalFiles(files.size())
            .successfulUploads(successCount)
            .failedUploads(failureCount)
            .results(results)
            .build();
    }
}

// Stub service classes for compilation
class VirusScanService {
    public void scan(org.springframework.web.multipart.MultipartFile file) {
        // TODO: Implement virus scanning
    }
}

class AIClassificationService {
    public ClassificationResult classify(Document document) {
        // TODO: Implement AI classification
        return new ClassificationResult(com.apex.document.domain.DocumentType.INVOICE, new java.math.BigDecimal("0.85"));
    }
}

class ClassificationResult {
    private final com.apex.document.domain.DocumentType type;
    private final java.math.BigDecimal confidence;
    
    public ClassificationResult(com.apex.document.domain.DocumentType type, java.math.BigDecimal confidence) {
        this.type = type;
        this.confidence = confidence;
    }
    
    public com.apex.document.domain.DocumentType getType() { return type; }
    public java.math.BigDecimal getConfidence() { return confidence; }
}

class IntegrationService {
    public void sendToErp(UUID documentId) {
        // TODO: Implement ERP integration
    }
}

class DocumentLockService {
    public void lockDocument(UUID documentId, String userId) {
        // TODO: Implement document locking
    }
}
