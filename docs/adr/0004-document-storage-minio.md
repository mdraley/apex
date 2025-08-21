# ADR 0004: Document storage using MinIO (S3 API) in docker/prod

Date: 2025-08-19

## Status
Accepted

## Context
Documents (PDF, images) require object storage, versioning, and compatibility with S3 SDKs. Local development prefers a lightweight, dockerized solution.

## Decision
- Use MinIO for docker/prod profiles via S3-compatible clients.
- For development, allow local filesystem storage as an alternative for convenience.

## Consequences
- Uniform S3 API across environments.
- Credentials/secrets managed via profile-specific configuration.
