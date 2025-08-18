package com.apex.vendor.domain;

import com.apex.core.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event published when a vendor is deactivated
 */
@Getter
public class VendorDeactivatedEvent extends DomainEvent {
    
    private final UUID vendorId;
    
    public VendorDeactivatedEvent(UUID aggregateId, Long aggregateVersion, String userId) {
        super(aggregateId, aggregateVersion, userId);
        this.vendorId = aggregateId;
    }
    
    @Override
    public Object getEventData() {
        return new VendorEventData(vendorId, "DEACTIVATED");
    }
    
    @Getter
    private static class VendorEventData {
        private final UUID vendorId;
        private final String action;
        
        public VendorEventData(UUID vendorId, String action) {
            this.vendorId = vendorId;
            this.action = action;
        }
    }
}
