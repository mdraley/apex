package com.apex.document.application;

/**
 * Exception thrown when uploaded file type is not supported
 */
public class InvalidFileTypeException extends RuntimeException {
    
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
