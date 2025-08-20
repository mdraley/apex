# apex-vendor-management

Purpose: Manage vendors, onboarding, and validation.

Endpoints:
- CRUD for vendors
- Search and status updates

Dependencies:
- `apex-core`, Spring Web, JPA, Validation

Data:
- PostgreSQL tables for vendors and relationships

Testing:
- Unit: services and validators
- Slice: controller and repository
- Integration: Postgres via Testcontainers
