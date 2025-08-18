package com.apex.core.cqrs;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all queries in the system
 * Implements Query pattern for CQRS architecture
 */
@Data
public abstract class Query {
    private final UUID queryId;
    private final UUID userId;
    private final Instant timestamp;

    protected Query(UUID queryId, UUID userId) {
        this.queryId = queryId;
        this.userId = userId;
        this.timestamp = Instant.now();
    }
}
