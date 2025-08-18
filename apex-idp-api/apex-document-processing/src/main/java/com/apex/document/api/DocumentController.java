package com.apex.document.api;

import com.apex.core.api.ApiResponse;
import com.apex.core.security.SecurityUtils;
import com.apex.document.application.DocumentCommandService;
import com.apex.document.application.DocumentQueryService;
import com.apex.document.application.dto.*;
import com.apex.document.domain.DocumentType;
import com.apex.document.infrastructure.websocket.WebSocketNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for document processing operations.
 * Implements the document upload and processing endpoints defined in the user stories.
 */
@Slf4j
@RestController
@RequestMapping("/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Processing", description = "Document upload, processing, and validation endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class DocumentController {
    
    private final DocumentCommandService commandService;
    private final DocumentQueryService queryService;
    private final WebSocketNotificationService notificationService;
    
    /**
     * Upload a single document for processing.
     * Implements Story 2.1 and 2.2 from the epic breakdown.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a document for processing")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'VENDOR')")
    public ApiResponse<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "vendorId", required = false) UUID vendorId,
            @RequestParam(value = "priority", defaultValue = "NORMAL") String priority) {
        
        log.info("Received document upload request: {}", file.getOriginalFilename());
        
        // If vendor is uploading, use their ID from security context
        if (vendorId == null && SecurityUtils.hasRole("VENDOR")) {
            vendorId = SecurityUtils.getCurrentVendorId();
        }
        
        UUID documentId = commandService.uploadDocument(file, vendorId);
        
        // Send real-time notification about upload
        notificationService.notifyDocumentUploaded(documentId);
        
        return ApiResponse.success(
            DocumentUploadResponse.builder()
                .documentId(documentId)
                .fileName(file.getOriginalFilename())
                .status("UPLOADED")
                .message("Document uploaded successfully and queued for processing")
                .build()
        );
    }
    
    /**
     * Upload multiple documents for batch processing.
     * Implements Story 2.3 from the epic breakdown.
     */
    @PostMapping(value = "/upload/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple documents for batch processing")
    @PreAuthorize("hasRole('AP_CLERK')")
    public ApiResponse<BatchUploadResponse> uploadBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "batchName") String batchName,
            @RequestParam(value = "vendorId", required = false) UUID vendorId) {
        
        log.info("Received batch upload request: {} files", files.size());
        
        BatchUploadResult result = commandService.uploadBatch(files, batchName, vendorId);
        
        return ApiResponse.success(
            BatchUploadResponse.builder()
                .batchId(result.getBatchId())
                .totalFiles(files.size())
                .successfulUploads(result.getSuccessfulUploads())
                .failedUploads(result.getFailedUploads())
                .uploadResults(result.getResults())
                .build()
        );
    }
    
    /**
     * Get documents pending validation.
     * Implements Story 5.1 and 5.2 from the epic breakdown.
     */
    @GetMapping("/validation/queue")
    @Operation(summary = "Get documents pending validation")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'SUPERVISOR')")
    public ApiResponse<Page<ValidationQueueDTO>> getValidationQueue(
            @RequestParam(value = "confidenceThreshold", defaultValue = "0.80") BigDecimal threshold,
            Pageable pageable) {
        
        Page<ValidationQueueDTO> queue = queryService.getValidationQueue(pageable);
        return ApiResponse.success(queue);
    }
    
    /**
     * Get document details for validation.
     * Returns document with extracted fields and confidence scores.
     */
    @GetMapping("/{documentId}/validation")
    @Operation(summary = "Get document details for validation")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'SUPERVISOR')")
    public ApiResponse<DocumentValidationDTO> getDocumentForValidation(
            @PathVariable UUID documentId) {
        
        DocumentValidationDTO document = queryService.getDocumentForValidation(documentId.toString());
        
        // Lock document for current user to prevent concurrent editing
        commandService.lockDocumentForValidation(documentId, SecurityUtils.getCurrentUserId().toString());
        
        return ApiResponse.success(document);
    }
    
    /**
     * Submit validation results for a document.
     * Implements Story 5.2 from the epic breakdown.
     */
    @PostMapping("/{documentId}/validate")
    @Operation(summary = "Submit validation results for a document")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'SUPERVISOR')")
    public ApiResponse<Void> validateDocument(
            @PathVariable UUID documentId,
            @RequestBody @Valid ValidationRequest request) {
        
        log.info("Validating document: {} by user: {}", documentId, SecurityUtils.getCurrentUserId());
        
        commandService.validateDocument(documentId, request);
        
        // Send notification about validation completion
        notificationService.notifyValidationComplete(
            SecurityUtils.getCurrentUserId().toString(), 
            documentId.toString()
        );
        
        return ApiResponse.success("Document validated successfully");
    }
    
    /**
     * Get real-time processing status for a document.
     * Used by frontend to show processing progress.
     */
    @GetMapping("/{documentId}/status")
    @Operation(summary = "Get document processing status")
    public ApiResponse<DocumentStatusDTO> getDocumentStatus(@PathVariable UUID documentId) {
        DocumentStatusDTO status = queryService.getDocumentStatus(documentId.toString());
        return ApiResponse.success(status);
    }
    
    /**
     * Search documents using full-text search.
     * Implements search functionality across all processed documents.
     */
    @GetMapping("/search")
    @Operation(summary = "Search documents")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Page<DocumentDTO>> searchDocuments(
            @RequestParam("query") String query,
            @RequestParam(value = "type", required = false) DocumentType type,
            @RequestParam(value = "vendorId", required = false) UUID vendorId,
            @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(value = "dateTo", required = false) LocalDate dateTo,
            Pageable pageable) {
        
        SearchCriteria criteria = SearchCriteria.builder()
            .query(query)
            .documentType(type)
            .dateFrom(dateFrom != null ? dateFrom.toString() : null)
            .dateTo(dateTo != null ? dateTo.toString() : null)
            .build();
        
        Page<DocumentDTO> results = queryService.searchDocuments(criteria, pageable);
        return ApiResponse.success(results);
    }
    
    /**
     * Get processing statistics for dashboard
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get document processing statistics")
    @PreAuthorize("hasAnyRole('AP_CLERK', 'SUPERVISOR', 'ADMIN')")
    public ApiResponse<ProcessingStatistics> getProcessingStatistics() {
        ProcessingStatistics stats = queryService.getProcessingStatistics();
        return ApiResponse.success(stats);
    }
    
    /**
     * Get document preview/download URL
     */
    @GetMapping("/{documentId}/download")
    @Operation(summary = "Get secure download URL for document")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DocumentDownloadResponse> getDocumentDownload(@PathVariable UUID documentId) {
        String downloadUrl = queryService.generateSecureDownloadUrl(documentId.toString());
        return ApiResponse.success(
            DocumentDownloadResponse.builder()
                .documentId(documentId.toString())
                .downloadUrl(downloadUrl)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build()
        );
    }
}
