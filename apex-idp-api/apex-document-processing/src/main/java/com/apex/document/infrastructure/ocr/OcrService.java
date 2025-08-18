package com.apex.document.infrastructure.ocr;

import org.springframework.stereotype.Service;

/**
 * Service for OCR processing
 */
@Service
public class OcrService {
    
    public OcrResult performOcr(String storagePath) {
        // TODO: Implement OCR processing
        return new OcrResult("Extracted text content", 1);
    }
}
