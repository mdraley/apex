package com.apex.core.exceptions;

/**
 * Base exception for all domain-related exceptions
 */
public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Returns the error code for this exception
     */
    public abstract String getErrorCode();
}
