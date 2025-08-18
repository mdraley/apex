package com.apex.vendor.application;

import lombok.Data;

/**
 * Command for updating vendor information
 */
@Data
public class UpdateVendorCommand {
    
    private String name;
    private Integer paymentTermsDays;
    private String notes;
}
