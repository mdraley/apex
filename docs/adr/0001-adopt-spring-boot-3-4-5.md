# ADR 0001: Adopt Spring Boot 3.4.5 and Spring Cloud 2024.0.2

Date: 2025-08-19

## Status
Accepted

## Context
We need a modern baseline for Jakarta EE 10, native image readiness, and alignment with current springdoc, security, and data starters across the multi-module reactor.

## Decision
- Use Spring Boot 3.4.5 across all modules.
- Use Spring Cloud 2024.0.2 for compatible cloud components and BOM.
- Target Java 17.

## Consequences
- Jakarta namespace migration is complete.
- Consistent dependency management via parent/BOM.
- Requires recent Maven and JDK in CI.
