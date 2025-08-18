package com.apex.core.cqrs;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all commands in the system
 * Implements Command pattern for CQRS architecture
 */
@Data
public abstract class Command {
    private final UUID commandId;
    private final UUID userId;
    private final Instant timestamp;

    protected Command(UUID commandId, UUID userId) {
        this.commandId = commandId;
        this.userId = userId;
        this.timestamp = Instant.now();
    }
}
