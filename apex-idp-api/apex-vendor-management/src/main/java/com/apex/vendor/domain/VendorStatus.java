package com.apex.vendor.domain;

/**
 * Vendor status enumeration
 */
public enum VendorStatus {
    PENDING,    // Newly created, awaiting approval
    ACTIVE,     // Approved and active
    INACTIVE,   // Deactivated
    SUSPENDED   // Temporarily suspended
}
