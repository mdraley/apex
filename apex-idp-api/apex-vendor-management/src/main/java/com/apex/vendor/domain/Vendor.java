package com.apex.vendor.domain;

import com.apex.core.domain.AggregateRoot;
import com.apex.core.events.DomainEvent;
import com.apex.core.exceptions.BusinessValidationException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Vendor aggregate root representing a healthcare provider or supplier
 */
@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
public class Vendor extends AggregateRoot<Vendor> {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "tax_id", unique = true, nullable = false)
    private String taxId;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Embedded
    private Address address;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VendorStatus status = VendorStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VendorType type;
    
    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays = 30;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    public Vendor(String name, String taxId, String email, VendorType type) {
        this.name = name;
        this.taxId = taxId;
        this.email = email;
        this.type = type;
        this.status = VendorStatus.PENDING;
        validate();
    }
    
    public void activate() {
        if (status == VendorStatus.INACTIVE) {
            throw new BusinessValidationException("Cannot activate an inactive vendor");
        }
        this.status = VendorStatus.ACTIVE;
        updateEntity();
        
        // Publish domain event
        registerEvent(new VendorActivatedEvent(getId(), getVersion(), "system"));
    }
    
    public void deactivate() {
        this.status = VendorStatus.INACTIVE;
        updateEntity();
        
        // Publish domain event
        registerEvent(new VendorDeactivatedEvent(getId(), getVersion(), "system"));
    }
    
    public void updateContactInfo(String email, String phone, Address address) {
        this.email = email;
        this.phone = phone;
        this.address = address;
        updateEntity();
        
        // Publish domain event
        registerEvent(new VendorContactUpdatedEvent(getId(), getVersion(), "system"));
    }
    
    @Override
    protected void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessValidationException("Vendor name cannot be empty");
        }
        if (taxId == null || taxId.trim().isEmpty()) {
            throw new BusinessValidationException("Tax ID cannot be empty");
        }
        if (type == null) {
            throw new BusinessValidationException("Vendor type must be specified");
        }
        if (email != null && !isValidEmail(email)) {
            throw new BusinessValidationException("Invalid email format");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    @Override
    public void apply(DomainEvent event) {
        // Handle domain events specific to vendor
        // This method will be called when events are applied to this aggregate
    }
}
