package com.apex.vendor.api;

import com.apex.vendor.application.*;
import com.apex.vendor.domain.Vendor;
import com.apex.vendor.domain.VendorStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API controller for vendor management
 */
@RestController
@RequestMapping("/v1/vendors")
@Tag(name = "Vendor Management", description = "APIs for managing healthcare vendors and suppliers")
@RequiredArgsConstructor
public class VendorController {
    
    private final VendorService vendorService;
    
    @PostMapping
    @Operation(summary = "Create a new vendor", description = "Creates a new vendor in the system")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Vendor> createVendor(@Valid @RequestBody CreateVendorCommand command) {
        Vendor vendor = vendorService.createVendor(command);
        return new ResponseEntity<>(vendor, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get vendor by ID", description = "Retrieves a specific vendor by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Vendor> getVendor(
            @Parameter(description = "Vendor ID") @PathVariable UUID id) {
        Vendor vendor = vendorService.findVendorById(id);
        return ResponseEntity.ok(vendor);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update vendor", description = "Updates vendor information")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Vendor> updateVendor(
            @Parameter(description = "Vendor ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateVendorCommand command) {
        Vendor vendor = vendorService.updateVendor(id, command);
        return ResponseEntity.ok(vendor);
    }
    
    @PutMapping("/{id}/contact")
    @Operation(summary = "Update vendor contact", description = "Updates vendor contact information")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Vendor> updateVendorContact(
            @Parameter(description = "Vendor ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateVendorContactCommand command) {
        Vendor vendor = vendorService.updateVendorContact(id, command);
        return ResponseEntity.ok(vendor);
    }
    
    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate vendor", description = "Activates a pending vendor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateVendor(
            @Parameter(description = "Vendor ID") @PathVariable UUID id) {
        vendorService.activateVendor(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate vendor", description = "Deactivates an active vendor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateVendor(
            @Parameter(description = "Vendor ID") @PathVariable UUID id) {
        vendorService.deactivateVendor(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search vendors", description = "Searches vendors with pagination")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<Vendor>> searchVendors(
            @Parameter(description = "Search term") @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Vendor> vendors = vendorService.searchVendors(q, pageable);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get vendors by status", description = "Retrieves vendors filtered by status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Vendor>> getVendorsByStatus(
            @Parameter(description = "Vendor status") @PathVariable VendorStatus status) {
        List<Vendor> vendors = vendorService.getVendorsByStatus(status);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get vendor statistics", description = "Retrieves vendor statistics and counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorStatistics> getVendorStatistics() {
        VendorStatistics stats = vendorService.getVendorStatistics();
        return ResponseEntity.ok(stats);
    }
}
