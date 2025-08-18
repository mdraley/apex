package com.apex.document.application.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO for document download response
 */
@Data
@Builder
public class DocumentDownloadResponse {
    private String documentId;
    private String filename;
    private String contentType;
    private long fileSize;
    private String downloadUrl;
    private LocalDateTime expiresAt;
    private String checksum;
    
    // Constructors
    public DocumentDownloadResponse() {}
    
    public DocumentDownloadResponse(String documentId, String filename, String contentType,
                                  long fileSize, String downloadUrl, LocalDateTime expiresAt,
                                  String checksum) {
        this.documentId = documentId;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
        this.expiresAt = expiresAt;
        this.checksum = checksum;
    }
    
    // Getters and setters
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
}
