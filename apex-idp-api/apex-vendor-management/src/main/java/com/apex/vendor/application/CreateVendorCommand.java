package com.apex.vendor.application;

import com.apex.vendor.domain.Address;
import com.apex.vendor.domain.VendorType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Command for creating a new vendor
 */
@Data
public class CreateVendorCommand {
    
    @NotBlank(message = "Vendor name is required")
    private String name;
    
    @NotBlank(message = "Tax ID is required")
    private String taxId;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String phone;
    
    @NotNull(message = "Vendor type is required")
    private VendorType type;
    
    private Address address;
    
    private Integer paymentTermsDays;
    
    private String notes;
}
