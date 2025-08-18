package com.apex.document.infrastructure.ocr;

/**
 * Result of OCR processing
 */
public class OcrResult {
    private final String text;
    private final int pageCount;
    
    public OcrResult(String text, int pageCount) {
        this.text = text;
        this.pageCount = pageCount;
    }
    
    public String getText() {
        return text;
    }
    
    public int getPageCount() {
        return pageCount;
    }
}
