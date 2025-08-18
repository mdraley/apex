package com.apex.document.infrastructure.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for document storage operations
 */
@Service
public class DocumentStorageService {
    
    public String storeDocument(MultipartFile file) {
        // TODO: Implement MinIO storage
        return "storage/path/" + file.getOriginalFilename();
    }
    
    public String generatePresignedUrl(String storagePath) {
        // TODO: Implement MinIO presigned URL generation
        return "http://localhost:9000/" + storagePath;
    }
}
