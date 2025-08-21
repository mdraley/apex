# ADR 0005: Redis for cache/sessions and Kafka for domain events

Date: 2025-08-19

## Status
Accepted

## Context
We need fast, distributed caching (tokens/sessions) and an event backbone for decoupled processing and analytics.

## Decision
- Use Redis for caching and optional session storage under `spring.data.redis.*`.
- Use Kafka for domain events; configure consumer/producer under the `kafka` section.

## Consequences
- Enables scalable read performance and async workflows.
- Requires docker services in dev and managed services or clusters in prod.
