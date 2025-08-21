# ADR 0002: Adopt springdoc-openapi 2.8.9 and enable Swagger UI in dev

Date: 2025-08-19

## Status
Accepted

## Context
OpenAPI is required for API discovery and integration. Spring Boot 3 requires springdoc 2.x. In dev, Swagger UI should be publicly viewable to speed iteration.

## Decision
- Use springdoc-openapi starter at 2.8.9 in the API gateway.
- Configure Security to permit `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`, `/v3/api-docs.yaml` in development.

## Consequences
- Swagger UI available at `/api/swagger-ui.html` (context path `/api`).
- In prod, docs remain protected behind auth.
