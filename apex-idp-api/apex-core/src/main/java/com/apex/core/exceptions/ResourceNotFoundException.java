package com.apex.core.exceptions;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends DomainException {
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s with identifier '%s' not found", resourceType, identifier));
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    @Override
    public String getErrorCode() {
        return "RESOURCE_NOT_FOUND";
    }
}
