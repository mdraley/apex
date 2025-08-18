package com.apex.core.events;

/**
 * Interface for publishing domain events.
 * Implementations will handle both local and external event publishing.
 */
public interface EventPublisher {
    
    /**
     * Publishes a domain event locally within the application
     */
    void publishLocal(DomainEvent event);
    
    /**
     * Publishes a domain event externally (e.g., to Kafka)
     */
    void publishExternal(DomainEvent event);
    
    /**
     * Publishes a domain event both locally and externally
     */
    default void publish(DomainEvent event) {
        publishLocal(event);
        if (event.isPublishable()) {
            publishExternal(event);
        }
    }
}
