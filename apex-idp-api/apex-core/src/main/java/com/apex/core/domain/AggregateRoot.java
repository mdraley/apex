package com.apex.core.domain;

import com.apex.core.events.DomainEvent;
import com.apex.core.security.SecurityContextHolder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.domain.DomainEvents;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Base class for all aggregate roots in the system.
 * Implements event sourcing pattern to maintain complete audit trail
 * as required for HIPAA compliance and healthcare regulations.
 */
@MappedSuperclass
@Getter
@Setter
@Slf4j
public abstract class AggregateRoot<T extends AggregateRoot<T>> 
        extends AbstractAggregateRoot<T> {
    
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Version
    private Long version;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
    
    @Column(name = "modified_at")
    private Instant modifiedAt;
    
    @Column(name = "modified_by")
    private String modifiedBy;
    
    @Transient
    private final List<DomainEvent> domainEventsList = new ArrayList<>();
    
    protected AggregateRoot() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
    
    /**
     * Registers a domain event that will be published when the aggregate is saved.
     * This ensures our event store maintains a complete history of all changes.
     */
    protected void registerEvent(DomainEvent event) {
        domainEventsList.add(event);
        // This will be published via Spring's @DomainEvents mechanism
        registerEvent((Object) event);
    }
    
    /**
     * Apply an event to reconstruct aggregate state.
     * Used when replaying events from the event store.
     */
    public abstract void apply(DomainEvent event);
    
    @DomainEvents
    protected Collection<Object> domainEvents() {
        return new ArrayList<>(domainEventsList);
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.createdBy = getCurrentUserId();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = Instant.now();
        this.modifiedBy = getCurrentUserId();
    }
    
    private String getCurrentUserId() {
        try {
            return SecurityContextHolder.getCurrentUserId();
        } catch (Exception e) {
            log.warn("Could not get current user from security context, using 'system'", e);
            return "system";
        }
    }
    
    /**
     * Override in subclasses to provide entity-specific business rules
     */
    protected void validate() {
        // Default implementation - override in subclasses
    }
    
    /**
     * Template method for safe entity updates
     */
    public final void updateEntity() {
        validate();
        onUpdate();
    }
}
