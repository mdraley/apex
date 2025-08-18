package com.apex.core.exceptions;

/**
 * Exception thrown when business validation rules are violated
 */
public class BusinessValidationException extends DomainException {
    
    public BusinessValidationException(String message) {
        super(message);
    }
    
    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getErrorCode() {
        return "BUSINESS_VALIDATION_FAILED";
    }
}
