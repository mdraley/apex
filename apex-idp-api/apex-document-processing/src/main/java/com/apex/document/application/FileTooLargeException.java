package com.apex.document.application;

/**
 * Exception thrown when uploaded file is too large
 */
public class FileTooLargeException extends RuntimeException {
    
    public FileTooLargeException(String message) {
        super(message);
    }
}
