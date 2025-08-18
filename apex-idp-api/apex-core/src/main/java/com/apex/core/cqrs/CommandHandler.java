package com.apex.core.cqrs;

/**
 * Interface for command handlers in CQRS architecture
 */
public interface CommandHandler<T extends Command> {
    void handle(T command);
}
