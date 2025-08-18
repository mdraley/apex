package com.apex.vendor.application;

import lombok.Builder;
import lombok.Data;

/**
 * Vendor statistics DTO
 */
@Data
@Builder
public class VendorStatistics {
    private long totalVendors;
    private long activeVendors;
    private long pendingVendors;
    private long inactiveVendors;
}
