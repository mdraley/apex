# apex-document-processing

Purpose: Document ingestion, parsing (Apache Tika), storage (MinIO), and processing orchestration.

Endpoints:
- Ingest document
- Retrieve document metadata/content
- Processing status/WebSocket updates

Dependencies:
- `apex-core`
- MinIO client, Apache Tika, Spring Web/WebSocket, JPA

Data:
- PostgreSQL tables for document metadata and processing state
- MinIO buckets for object storage

Testing:
- Unit: services and parsers
- Slice: controller and repository
- Integration: MinIO + Postgres via Testcontainers (as applicable)
