package com.apex.vendor.application;

import com.apex.vendor.domain.Address;
import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * Command for updating vendor contact information
 */
@Data
public class UpdateVendorContactCommand {
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String phone;
    
    private Address address;
}
