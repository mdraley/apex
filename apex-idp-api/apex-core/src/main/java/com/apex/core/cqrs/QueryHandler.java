package com.apex.core.cqrs;

/**
 * Interface for query handlers in CQRS architecture
 */
public interface QueryHandler<T extends Query, R> {
    R handle(T query);
}
