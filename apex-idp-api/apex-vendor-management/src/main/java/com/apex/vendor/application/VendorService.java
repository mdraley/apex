package com.apex.vendor.application;

import com.apex.core.exceptions.ResourceNotFoundException;
import com.apex.vendor.domain.*;
import com.apex.vendor.infrastructure.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application service for vendor management operations
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VendorService {
    
    private final VendorRepository vendorRepository;
    
    /**
     * Creates a new vendor
     */
    public Vendor createVendor(CreateVendorCommand command) {
        log.info("Creating vendor with name: {}", command.getName());
        
        // Check if vendor with same tax ID already exists
        vendorRepository.findByTaxId(command.getTaxId())
                .ifPresent(existingVendor -> {
                    throw new IllegalArgumentException("Vendor with tax ID " + command.getTaxId() + " already exists");
                });
        
        Vendor vendor = new Vendor(
                command.getName(),
                command.getTaxId(),
                command.getEmail(),
                command.getType()
        );
        
        if (command.getAddress() != null) {
            vendor.setAddress(command.getAddress());
        }
        if (command.getPhone() != null) {
            vendor.setPhone(command.getPhone());
        }
        if (command.getPaymentTermsDays() != null) {
            vendor.setPaymentTermsDays(command.getPaymentTermsDays());
        }
        if (command.getNotes() != null) {
            vendor.setNotes(command.getNotes());
        }
        
        Vendor savedVendor = vendorRepository.save(vendor);
        log.info("Created vendor with ID: {}", savedVendor.getId());
        return savedVendor;
    }
    
    /**
     * Updates vendor information
     */
    public Vendor updateVendor(UUID vendorId, UpdateVendorCommand command) {
        log.info("Updating vendor with ID: {}", vendorId);
        
        Vendor vendor = findVendorById(vendorId);
        
        if (command.getName() != null) {
            vendor.setName(command.getName());
        }
        if (command.getPaymentTermsDays() != null) {
            vendor.setPaymentTermsDays(command.getPaymentTermsDays());
        }
        if (command.getNotes() != null) {
            vendor.setNotes(command.getNotes());
        }
        
        vendor.updateEntity();
        return vendorRepository.save(vendor);
    }
    
    /**
     * Updates vendor contact information
     */
    public Vendor updateVendorContact(UUID vendorId, UpdateVendorContactCommand command) {
        log.info("Updating contact info for vendor ID: {}", vendorId);
        
        Vendor vendor = findVendorById(vendorId);
        vendor.updateContactInfo(command.getEmail(), command.getPhone(), command.getAddress());
        
        return vendorRepository.save(vendor);
    }
    
    /**
     * Activates a vendor
     */
    public void activateVendor(UUID vendorId) {
        log.info("Activating vendor with ID: {}", vendorId);
        
        Vendor vendor = findVendorById(vendorId);
        vendor.activate();
        vendorRepository.save(vendor);
    }
    
    /**
     * Deactivates a vendor
     */
    public void deactivateVendor(UUID vendorId) {
        log.info("Deactivating vendor with ID: {}", vendorId);
        
        Vendor vendor = findVendorById(vendorId);
        vendor.deactivate();
        vendorRepository.save(vendor);
    }
    
    /**
     * Finds vendor by ID
     */
    @Transactional(readOnly = true)
    public Vendor findVendorById(UUID vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", vendorId.toString()));
    }
    
    /**
     * Searches vendors with pagination
     */
    @Transactional(readOnly = true)
    public Page<Vendor> searchVendors(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return vendorRepository.findAll(pageable);
        }
        return vendorRepository.searchVendors(searchTerm.trim(), pageable);
    }
    
    /**
     * Gets vendors by status
     */
    @Transactional(readOnly = true)
    public List<Vendor> getVendorsByStatus(VendorStatus status) {
        return vendorRepository.findByStatus(status);
    }
    
    /**
     * Gets vendor statistics
     */
    @Transactional(readOnly = true)
    public VendorStatistics getVendorStatistics() {
        return VendorStatistics.builder()
                .totalVendors(vendorRepository.count())
                .activeVendors(vendorRepository.countByStatus(VendorStatus.ACTIVE))
                .pendingVendors(vendorRepository.countByStatus(VendorStatus.PENDING))
                .inactiveVendors(vendorRepository.countByStatus(VendorStatus.INACTIVE))
                .build();
    }
}
