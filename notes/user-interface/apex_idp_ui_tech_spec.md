# Apex IDP – UI/UX Design & Technical Specification

## 1. Executive Summary

This document defines the end-to-end UI/UX specifications, backend interaction mappings, and execution plan for the Apex Intelligent Document Processing (IDP) system. It aligns with the provided architecture blueprint (Spring Boot 3 + React 18, Hexagonal Architecture) and is implementation-ready.
⸻
## 2. User Personas & Key Scenarios

**Personas:**
– **AP Clerk** – Uploads invoice batches, validates extracted data, resolves errors.
– **AP Manager** – Monitors processing throughput, reviews exceptions, manages approvals.
– **System Admin** – Manages user permissions, monitors system health, audits data.

**Scenarios:**
1. Upload new batch of invoices for processing.
2. Validate extracted invoice fields and approve batch.
3. Use AI chat to query data with full batch/document context.
4. Monitor batch progress from dashboard.
⸻
## 3. End-to-End User Journey & Screen Map

Step #Screen / StageUser ActionsSystem ActionsAPI EndpointsNotes1DashboardView batch summary statsFetch metrics & batch listGET /api/batches/summary, GET /api/batchesFilter & sort options2Create Batch (Form)Select source type, material, upload filesSave metadata, init batch IDPOST /api/batchesStatus: DRAFT3File UploadSelect & upload invoicesStore files, link to batchPOST /api/batches/{id}/documentsAsync upload4Upload Progress TableMonitor progressProcess OCR/classificationDELETE /api/documents/{id}Status badges5Validation ScreenSelect doc, see original vs extractedFetch extracted dataGET /api/documents/{id}/extracted-dataHighlight low-confidence6Inline EditCorrect values, approveSave changesPUT /api/documents/{id}/extracted-dataChange logs7AI Chat PanelAsk doc/batch questionsQuery AI API with contextPOST /api/ai/chatContext = doc+batch8Batch SubmissionApprove all docs, submitChange status, exportPOST /api/batches/{id}/submitTrigger ERP9Completion/ExportView confirmation, logsLog resultsGET /api/batches/{id}/export-statusDownload package⸻
## 4. Screen-by-Screen UX Spec

(Detailed descriptions of layout, key UI components, interactions, error states for each screen — see journey above.)
⸻
## 5. OpenAI Chat for Invoice Validation

– **Integration**: Embedded panel in validation screen.
– **Context**: Current document fields, extracted data, batch metadata, processing status.
– **API**: POST /api/ai/chat with JSON payload { "batchId": "...", "documentId": "...", "question": "..." }.
⸻
## 6. Backend Interaction Map (Per Screen)

(Per screen mapping of API calls, request/response formats, error codes, and async events.)
⸻
## 7. Data Model Mapping

**Core Entities**: Batch, Document, ExtractedData, User, ChatSession.
⸻
## 8. Front-End Architecture Plan

– **Framework**: React 18 + TypeScript
– **State Mgmt**: Zustand for UI state, TanStack Query for server data
– **API Layer**: Axios with interceptors for auth/error handling
⸻
## 9. Security, Privacy, and Compliance

– Role-based access control
– Audit logging for all edits
– PII masking
– TLS 1.3 for transport security
⸻
## 10. Testing Strategy

– **Unit Tests**: JUnit (backend), Jest/RTL (frontend)
– **Integration Tests**: Postman + Newman CI
– **E2E Tests**: Playwright or Cypress
⸻
## 11. Observability & Analytics

– Spring Boot Actuator
– Centralized logging with ELK
– Prometheus/Grafana dashboards
⸻
## 12. Execution Plan & Milestones

1. UI scaffolding (React + Tailwind + shadcn/ui)
2. API contracts
3. File upload & batch creation
4. OCR/classification integration
5. Validation screen & AI chat
6. Dashboard
7. Testing & deployment
⸻
## 13. Acceptance Criteria (Representative)

– User can upload batch of 50+ invoices
– All low-confidence fields flagged for review
– AI chat answers contextually correct questions
⸻
## 14. Risks & Mitigations

– **Risk**: OCR latency — _Mitigation_: async processing + progress updates
– **Risk**: AI hallucination — _Mitigation_: context restriction + fact-checking
⸻
## 15. Output Format

This spec is maintained in Markdown for direct inclusion in the project repo.
⸻
## Assumptions & Open Questions

– ERP export endpoint contract TBD
– User role definitions to be confirmed