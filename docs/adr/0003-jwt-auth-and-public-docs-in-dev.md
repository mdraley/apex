# ADR 0003: JWT authentication with public docs/health in development

Date: 2025-08-19

## Status
Accepted

## Context
We need stateless security suitable for SPA clients and WebSocket auth. Developers need frictionless access to health and docs in dev.

## Decision
- Use JWT bearer tokens validated by a `JwtRequestFilter`.
- Permit unauthenticated access to `/actuator/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`, `/v3/api-docs.yaml` in the development profile.

## Consequences
- SPA can authenticate via login endpoint to obtain JWT.
- Swagger works without auth locally; production remains locked down.
