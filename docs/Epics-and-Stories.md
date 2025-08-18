After thoroughly analyzing all the provided documentation for the Apex IDP platform, I can see this is a sophisticated healthcare-focused intelligent document processing system designed to revolutionize invoice management for rural hospitals. The platform combines cutting-edge AI/ML capabilities with enterprise-grade security and seamless healthcare system integrations.

Let me break down the project into well-structured Jira epics and user stories that will enable your engineering team to deliver this platform incrementally while maintaining full vertical slice functionality.

## Epic 1: Authentication & Authorization Foundation

**Epic Description:** Implement secure JWT-based authentication system with role-based access control, session management, and user profile management. This foundational epic ensures all users can securely access the platform with appropriate permissions.

### User Stories:

**Story 1.1: User Login Flow - API**
- **Title:** API - Implement JWT Authentication Endpoints
- **Description:** As a user, I want to securely log into the system using my credentials so that I can access platform features appropriate to my role
- **Acceptance Criteria:**
  - POST /api/auth/login endpoint validates credentials against database
  - Generates JWT token with role claims and 15-minute expiration
  - Creates session in Redis with user metadata
  - Returns user profile data with role information
  - Implements password hashing with bcrypt
  - Handles invalid credentials with appropriate error messages
- **Label:** API

**Story 1.2: User Login Flow - UI**
- **Title:** UI - Create Login Page and Authentication Flow
- **Description:** As a user, I want a secure and intuitive login interface so that I can easily access the platform
- **Acceptance Criteria:**
  - Responsive login form with email/password fields
  - Client-side validation for required fields
  - Loading states during authentication
  - Error message display for failed attempts
  - Successful login redirects to role-appropriate dashboard
  - JWT stored securely in memory (not localStorage)
- **Label:** UI

**Story 1.3: Role-Based Dashboard Routing**
- **Title:** Implement Role-Based Navigation System
- **Description:** As an authenticated user, I want to be automatically directed to my role-specific dashboard so that I can access relevant features immediately
- **Acceptance Criteria:**
  - API middleware validates JWT and extracts role claims
  - UI router guards protect role-specific routes
  - AP Clerks route to /validation dashboard
  - CFOs route to /executive dashboard
  - Vendors route to /vendor portal
  - IT Admins route to /admin panel
- **Label:** Shared

**Story 1.4: Session Management & Token Refresh**
- **Title:** Implement Secure Session Handling
- **Description:** As a user, I want my session to remain active during work but timeout for security so that my account remains protected
- **Acceptance Criteria:**
  - API implements refresh token endpoint
  - UI automatically refreshes tokens before expiration
  - Session timeout after 30 minutes of inactivity
  - Logout endpoint invalidates session in Redis
  - All API calls include JWT in Authorization header
- **Label:** Shared

## Epic 2: Document Ingestion & Upload System

**Epic Description:** Build multi-channel document ingestion capabilities supporting drag-and-drop upload, batch processing, email gateway integration, and file validation with virus scanning.

### User Stories:

**Story 2.1: Single Document Upload - API**
- **Title:** API - Document Upload and Storage Service
- **Description:** As an AP Clerk, I want to upload invoice documents so that they can be processed automatically
- **Acceptance Criteria:**
  - POST /api/documents/upload endpoint accepts multipart form data
  - Validates file types (PDF, JPG, PNG, TIFF) up to 50MB
  - Virus scanning with ClamAV or equivalent
  - Stores documents in MinIO/S3 with unique identifiers
  - Creates document record in PostgreSQL
  - Returns upload confirmation with document ID
- **Label:** API

**Story 2.2: Single Document Upload - UI**
- **Title:** UI - Drag-and-Drop Upload Interface
- **Description:** As an AP Clerk, I want an intuitive drag-and-drop interface to upload documents easily
- **Acceptance Criteria:**
  - Drag-and-drop zone with visual feedback
  - File browser alternative option
  - Upload progress bar with percentage
  - File validation messages before upload
  - Success confirmation with document preview
  - Error handling for invalid files
- **Label:** UI

**Story 2.3: Batch Document Upload**
- **Title:** Support Bulk Document Processing
- **Description:** As an AP Clerk, I want to upload multiple documents at once so that I can process invoices efficiently
- **Acceptance Criteria:**
  - API accepts multiple files in single request
  - UI displays grid of uploading documents
  - Individual progress tracking per file
  - Batch metadata assignment capability
  - Parallel upload with configurable concurrency
  - Batch success/failure summary
- **Label:** Shared

**Story 2.4: Email Gateway Integration**
- **Title:** Process Documents from Email Attachments
- **Description:** As a vendor, I want to submit invoices via email so that I don't need portal access
- **Acceptance Criteria:**
  - API polls configured email inbox via IMAP
  - Extracts and validates attachments
  - Associates documents with vendor based on sender
  - Sends automated confirmation email
  - Quarantines suspicious attachments
  - UI displays email-sourced documents in queue
- **Label:** Shared

## Epic 3: OCR & Document Processing Engine

**Epic Description:** Implement AI-powered OCR processing using ensemble models (Tesseract, LayoutLMv3, DocTR) with confidence scoring and automatic document enhancement.

### User Stories:

**Story 3.1: OCR Processing Pipeline - API**
- **Title:** API - Implement Multi-Model OCR Engine
- **Description:** As a system, I want to extract text from documents accurately so that data can be validated and processed
- **Acceptance Criteria:**
  - Implements Tesseract for baseline OCR
  - Integrates LayoutLMv3 for layout understanding
  - Uses DocTR for complex document reconstruction
  - Calculates confidence scores per extracted field
  - Handles multi-page documents
  - Stores OCR results with bounding box coordinates
- **Label:** API

**Story 3.2: Processing Status Dashboard - UI**
- **Title:** UI - Real-Time OCR Processing Monitor
- **Description:** As an AP Clerk, I want to see the processing status of uploaded documents so that I know when they're ready for review
- **Acceptance Criteria:**
  - WebSocket connection for real-time updates
  - Processing stages visualization (Upload → OCR → Classification → Extraction)
  - Progress percentage display
  - Estimated completion time
  - Error status with retry options
  - Queue position indicator
- **Label:** UI

**Story 3.3: Document Image Enhancement**
- **Title:** Automatic Document Quality Improvement
- **Description:** As a system, I want to enhance document images before OCR so that extraction accuracy improves
- **Acceptance Criteria:**
  - API implements automatic deskewing
  - Noise reduction and contrast adjustment
  - Orientation detection and correction
  - Resolution enhancement for low-quality scans
  - UI shows before/after preview
  - Manual override options for adjustments
- **Label:** Shared

## Epic 4: AI Classification & Data Extraction

**Epic Description:** Build intelligent document classification system with automated field extraction, confidence scoring, and structured data output for various document types.

### User Stories:

**Story 4.1: Document Classification Service - API**
- **Title:** API - AI-Powered Document Type Detection
- **Description:** As a system, I want to automatically classify document types so that appropriate extraction rules are applied
- **Acceptance Criteria:**
  - Implements transformer-based classification model
  - Supports invoice, purchase order, statement, contract types
  - Returns classification with confidence score
  - Falls back to manual classification if confidence < 70%
  - Learns from user corrections via feedback loop
  - Handles multi-document PDFs with splitting
- **Label:** API

**Story 4.2: Classification Review Interface - UI**
- **Title:** UI - Manual Classification Override
- **Description:** As an AP Clerk, I want to review and correct document classifications so that extraction accuracy improves
- **Acceptance Criteria:**
  - Displays AI classification with confidence percentage
  - Dropdown to select correct document type
  - Visual indicators for low-confidence classifications
  - Bulk classification for similar documents
  - Classification history tracking
  - Feedback submission for model improvement
- **Label:** UI

**Story 4.3: Field Extraction Engine**
- **Title:** Extract Structured Data from Documents
- **Description:** As a system, I want to extract key invoice fields automatically so that manual data entry is minimized
- **Acceptance Criteria:**
  - API extracts header fields (vendor, date, invoice number, totals)
  - Extracts line items with quantities and prices
  - Named Entity Recognition for financial data
  - Confidence scoring per extracted field
  - UI displays extracted data with field highlighting
  - Bounding box overlay on document image
- **Label:** Shared

**Story 4.4: Table Extraction for Line Items**
- **Title:** Process Complex Invoice Tables
- **Description:** As an AP Clerk, I want line items extracted accurately from invoice tables so that I can validate order details
- **Acceptance Criteria:**
  - API detects and extracts table structures
  - Preserves row-column relationships
  - Handles merged cells and complex layouts
  - Calculates line totals and validates math
  - UI displays extracted table in editable grid
  - Add/remove/edit line items capability
- **Label:** Shared

## Epic 5: Human-in-the-Loop Validation

**Epic Description:** Create comprehensive validation workflow allowing AP clerks to review, correct, and approve extracted data with confidence-based routing and annotation tools.

### User Stories:

**Story 5.1: Validation Queue Management - API**
- **Title:** API - Intelligent Work Queue Distribution
- **Description:** As an AP Clerk, I want documents routed to me based on confidence and priority so that I focus on items needing attention
- **Acceptance Criteria:**
  - Routes documents with confidence < 80% to validation queue
  - Prioritizes based on invoice amount and due date
  - Implements document locking during review
  - Tracks validation time per document
  - Supports supervisor escalation workflow
  - Maintains validation history and audit trail
- **Label:** API

**Story 5.2: Split-Screen Validation Interface - UI**
- **Title:** UI - Document Validation Workspace
- **Description:** As an AP Clerk, I want to validate extracted data while viewing the original document so that I can ensure accuracy
- **Acceptance Criteria:**
  - Split-screen layout with document viewer and form fields
  - Zoom/pan controls for document image
  - Field-by-field navigation with keyboard shortcuts
  - Confidence indicators (green/yellow/red) per field
  - Editable fields with validation rules
  - Save draft and complete validation actions
- **Label:** UI

**Story 5.3: Document Annotation System**
- **Title:** Add Comments and Annotations to Documents
- **Description:** As an AP Clerk, I want to annotate documents with notes and highlights so that issues are clearly communicated
- **Acceptance Criteria:**
  - API stores annotations with coordinates and user info
  - UI provides highlighting tool with color options
  - Comment bubbles with threaded discussions
  - Drawing tools for arrows and rectangles
  - Annotation list panel with filters
  - Export annotated PDF capability
- **Label:** Shared

**Story 5.4: Batch Validation Workflow**
- **Title:** Validate Multiple Documents Efficiently
- **Description:** As an AP Clerk, I want to validate similar documents in batches so that repetitive work is minimized
- **Acceptance Criteria:**
  - API supports bulk approval for high-confidence documents
  - Batch statistics display (total, approved, rejected)
  - Filter to show only low-confidence items
  - Apply common corrections across batch
  - UI provides batch action buttons
  - Undo capability for batch operations
- **Label:** Shared

## Epic 6: Vendor Management Portal

**Epic Description:** Build self-service vendor portal for invoice submission, payment tracking, profile management, and communication with hospital AP departments.

### User Stories:

**Story 6.1: Vendor Registration & Profile - API**
- **Title:** API - Vendor Master Data Management
- **Description:** As a vendor, I want to register and maintain my company profile so that the hospital has accurate information
- **Acceptance Criteria:**
  - POST /api/vendors/register with company details
  - W-9 and banking information secure storage
  - Tax ID duplicate checking
  - Email verification workflow
  - Profile update endpoints with audit logging
  - Document upload for certifications
- **Label:** API

**Story 6.2: Vendor Portal Dashboard - UI**
- **Title:** UI - Vendor Self-Service Interface
- **Description:** As a vendor, I want a dashboard to manage all interactions with the hospital so that I have visibility into our relationship
- **Acceptance Criteria:**
  - Responsive vendor portal landing page
  - Invoice submission interface
  - Payment history and status tracking
  - Profile management section
  - Document library for contracts/agreements
  - Communication history panel
- **Label:** UI

**Story 6.3: Invoice Submission by Vendors**
- **Title:** Vendor Direct Invoice Upload
- **Description:** As a vendor, I want to submit invoices directly through the portal so that processing is faster
- **Acceptance Criteria:**
  - API validates vendor authentication before upload
  - Enforces vendor-specific validation rules
  - Auto-populates vendor information
  - UI provides submission confirmation
  - Email notification on status changes
  - Resubmission capability for rejected invoices
- **Label:** Shared

**Story 6.4: Payment Status Tracking**
- **Title:** Real-Time Payment Visibility for Vendors
- **Description:** As a vendor, I want to track payment status for submitted invoices so that I can manage cash flow
- **Acceptance Criteria:**
  - API provides payment status endpoints
  - Real-time updates via WebSocket
  - Payment history with date and amount
  - Outstanding balance calculation
  - UI displays status badges (submitted, approved, paid)
  - Export payment report functionality
- **Label:** Shared

## Epic 7: Executive Dashboard & Analytics

**Epic Description:** Create comprehensive CFO dashboard with real-time financial metrics, cash flow modeling, payment prioritization tools, and strategic vendor insights.

### User Stories:

**Story 7.1: Financial Metrics API**
- **Title:** API - Real-Time AP Analytics Engine
- **Description:** As a CFO, I want real-time financial metrics so that I can make informed decisions
- **Acceptance Criteria:**
  - Calculates total AP liability in real-time
  - Generates aging analysis (30/60/90 days)
  - Vendor spend analytics with trends
  - Cash flow projections based on payment terms
  - Department-wise spending breakdown
  - API responses cached with 1-minute TTL
- **Label:** API

**Story 7.2: Executive Dashboard UI**
- **Title:** UI - CFO Mission Control Dashboard
- **Description:** As a CFO, I want a comprehensive dashboard so that I have complete visibility into AP operations
- **Acceptance Criteria:**
  - Responsive grid layout with customizable widgets
  - Real-time KPI cards with sparklines
  - Interactive charts using Recharts/D3.js
  - Drill-down capability to transaction details
  - Mobile-optimized tablet view
  - Export to PDF for board presentations
- **Label:** UI

**Story 7.3: Payment Scenario Modeling**
- **Title:** Cash Flow Optimization Tools
- **Description:** As a CFO, I want to model payment scenarios so that I can optimize cash flow under constraints
- **Acceptance Criteria:**
  - API calculates payment impact scenarios
  - Vendor criticality scoring algorithm
  - What-if analysis with multiple variables
  - UI provides interactive scenario builder
  - Drag-and-drop payment prioritization
  - Save and compare scenarios
- **Label:** Shared

**Story 7.4: Vendor Risk Assessment Dashboard**
- **Title:** Monitor and Assess Vendor Risks
- **Description:** As a CFO, I want to assess vendor risks so that I can mitigate financial exposure
- **Acceptance Criteria:**
  - API calculates multi-factor risk scores
  - Financial, operational, compliance risk vectors
  - Historical performance metrics
  - UI displays risk heat map
  - Alert configuration for risk thresholds
  - Vendor comparison and benchmarking
- **Label:** Shared

## Epic 8: ERP/EHR Integration Layer

**Epic Description:** Implement seamless bi-directional integration with CPSI TruBridge and eClinicalWorks systems for automated data synchronization and financial reporting.

### User Stories:

**Story 8.1: CPSI TruBridge Export - API**
- **Title:** API - Generate CPSI-Compatible Export Files
- **Description:** As an IT Admin, I want to export approved invoices to CPSI so that our ERP stays synchronized
- **Acceptance Criteria:**
  - Generates CSV files matching CPSI schema
  - Validates data integrity before export
  - Implements retry logic for failed exports
  - Maintains export history and logs
  - Supports batch and real-time modes
  - Reconciliation report generation
- **Label:** API

**Story 8.2: Integration Monitoring Dashboard - UI**
- **Title:** UI - ERP Sync Status Monitor
- **Description:** As an IT Admin, I want to monitor integration health so that I can ensure data flows correctly
- **Acceptance Criteria:**
  - Real-time sync status indicators
  - Error queue with detailed messages
  - Successful transfer history
  - Manual retry interface
  - Data mapping configuration UI
  - Integration test tools
- **Label:** UI

**Story 8.3: eClinicalWorks FHIR Integration**
- **Title:** Connect with EHR for Department Mapping
- **Description:** As a system, I want to integrate with eClinicalWorks so that costs can be allocated to departments
- **Acceptance Criteria:**
  - API implements FHIR R4 client
  - OAuth 2.0 authentication flow
  - Department and cost center synchronization
  - Patient volume correlation (PHI compliant)
  - UI shows department mapping interface
  - Manual override for mappings
- **Label:** Shared

**Story 8.4: Webhook Event Distribution**
- **Title:** Real-Time Event Notifications
- **Description:** As an integrated system, I want to receive real-time updates so that data stays synchronized
- **Acceptance Criteria:**
  - API publishes events to configured webhooks
  - Supports multiple webhook endpoints
  - Implements circuit breaker pattern
  - Retry with exponential backoff
  - UI webhook configuration interface
  - Test webhook functionality
- **Label:** Shared

## Epic 9: Contract Lifecycle Management

**Epic Description:** Build intelligent contract management system with AI-powered term extraction, automated renewal alerts, and comprehensive contract analytics.

### User Stories:

**Story 9.1: Contract Repository - API**
- **Title:** API - Secure Contract Storage Service
- **Description:** As a Procurement Manager, I want to store all vendor contracts centrally so that I can manage obligations effectively
- **Acceptance Criteria:**
  - Secure document storage with encryption
  - Version control for contract amendments
  - Full-text search using Elasticsearch
  - Metadata extraction and tagging
  - Access control based on user roles
  - Contract categorization system
- **Label:** API

**Story 9.2: Contract Upload & Management - UI**
- **Title:** UI - Contract Repository Interface
- **Description:** As a Procurement Manager, I want to upload and organize contracts so that I can find them easily
- **Acceptance Criteria:**
  - Drag-and-drop contract upload
  - Folder structure for organization
  - Advanced search with filters
  - Contract preview with viewer
  - Version comparison tool
  - Bulk operations support
- **Label:** UI

**Story 9.3: AI Term Extraction**
- **Title:** Automatically Extract Contract Terms
- **Description:** As a Procurement Manager, I want key terms extracted automatically so that I don't miss important dates
- **Acceptance Criteria:**
  - API uses NLP to extract key dates and terms
  - Identifies renewal, termination, notice periods
  - Extracts payment terms and penalties
  - Confidence scoring for extracted terms
  - UI displays extracted terms for validation
  - Manual override capability
- **Label:** Shared

**Story 9.4: Renewal Alert System**
- **Title:** Proactive Contract Renewal Management
- **Description:** As a Procurement Manager, I want alerts before contracts expire so that I can negotiate renewals timely
- **Acceptance Criteria:**
  - API calculates alert dates based on notice periods
  - Configurable alert thresholds (30/60/90 days)
  - Multi-channel notifications (email, dashboard)
  - Escalation for missed alerts
  - UI shows renewal calendar view
  - Snooze and acknowledge functionality
- **Label:** Shared

## Epic 10: Notification & Communication System

**Epic Description:** Implement real-time notification system using WebSockets for instant updates, alerts, and cross-team communication within the platform.

### User Stories:

**Story 10.1: WebSocket Infrastructure - API**
- **Title:** API - Real-Time Event Broadcasting
- **Description:** As a user, I want to receive instant notifications so that I stay informed of important events
- **Acceptance Criteria:**
  - Implements WebSocket server with Socket.io
  - Handles connection management and reconnection
  - Room-based broadcasting for user groups
  - Message persistence for offline users
  - Rate limiting to prevent spam
  - Heartbeat mechanism for connection health
- **Label:** API

**Story 10.2: Notification Center - UI**
- **Title:** UI - Universal Notification Hub
- **Description:** As a user, I want to see all notifications in one place so that I don't miss important updates
- **Acceptance Criteria:**
  - Bell icon with unread count badge
  - Dropdown panel with notification list
  - Mark as read/unread functionality
  - Filter by notification type
  - Click-through to relevant screens
  - Toast notifications for urgent items
- **Label:** UI

**Story 10.3: Email Notification Service**
- **Title:** Automated Email Alerts
- **Description:** As a user, I want email notifications for important events so that I'm informed even when offline
- **Acceptance Criteria:**
  - API integrates with email service (SendGrid/SES)
  - Configurable notification preferences per user
  - Email templates for different event types
  - Unsubscribe links in compliance with regulations
  - UI preference management interface
  - Digest option for non-urgent notifications
- **Label:** Shared

**Story 10.4: In-App Messaging System**
- **Title:** Team Communication Features
- **Description:** As an AP Clerk, I want to communicate with colleagues about documents so that issues are resolved quickly
- **Acceptance Criteria:**
  - API supports user-to-user messaging
  - Thread-based conversations per document
  - @mentions for user notifications
  - Message history and search
  - UI chat interface within document context
  - Read receipts and typing indicators
- **Label:** Shared

## Epic 11: System Administration & Configuration

**Epic Description:** Build comprehensive admin panel for user management, system configuration, audit log review, and platform health monitoring.

### User Stories:

**Story 11.1: User Management - API**
- **Title:** API - User Administration Endpoints
- **Description:** As an IT Admin, I want to manage user accounts so that access control is maintained
- **Acceptance Criteria:**
  - CRUD operations for user accounts
  - Role assignment and permission management
  - Password reset functionality
  - Account activation/deactivation
  - Bulk user import from CSV
  - Activity tracking per user
- **Label:** API

**Story 11.2: Admin Dashboard - UI**
- **Title:** UI - System Administration Portal
- **Description:** As an IT Admin, I want a centralized admin interface so that I can manage the platform efficiently
- **Acceptance Criteria:**
  - User management grid with search/filter
  - Role and permission matrix editor
  - System configuration panels
  - Audit log viewer with filters
  - System health monitoring widgets
  - Backup and restore interface
- **Label:** UI

**Story 11.3: Audit Trail System**
- **Title:** Comprehensive Activity Logging
- **Description:** As a Compliance Officer, I want complete audit trails so that I can meet regulatory requirements
- **Acceptance Criteria:**
  - API logs all user actions with timestamps
  - Immutable event store using Event Sourcing
  - Detailed change tracking with before/after values
  - IP address and session tracking
  - UI provides searchable audit log interface
  - Export audit reports for compliance
- **Label:** Shared

**Story 11.4: System Configuration Management**
- **Title:** Platform Settings and Customization
- **Description:** As an IT Admin, I want to configure system settings so that the platform meets our organization's needs
- **Acceptance Criteria:**
  - API provides configuration endpoints
  - Confidence thresholds adjustment
  - Workflow customization options
  - Integration settings management
  - UI configuration forms with validation
  - Configuration backup and restore
- **Label:** Shared

## Epic 12: Performance Monitoring & Optimization

**Epic Description:** Implement comprehensive monitoring, logging, and performance optimization infrastructure to ensure platform reliability and scalability.

### User Stories:

**Story 12.1: Application Metrics Collection - API**
- **Title:** API - Performance Monitoring Infrastructure
- **Description:** As a DevOps Engineer, I want to monitor application performance so that I can ensure optimal operation
- **Acceptance Criteria:**
  - Integrates Prometheus metrics collection
  - Custom metrics for business operations
  - Request/response time tracking
  - Database query performance monitoring
  - Resource utilization metrics
  - Error rate tracking and alerting
- **Label:** API

**Story 12.2: Monitoring Dashboard - UI**
- **Title:** UI - Operations Performance Dashboard
- **Description:** As a DevOps Engineer, I want to visualize system performance so that I can identify issues quickly
- **Acceptance Criteria:**
  - Grafana integration for metrics visualization
  - Real-time performance graphs
  - Alert status indicators
  - Historical trend analysis
  - Custom dashboard creation
  - Mobile-responsive monitoring view
- **Label:** UI

**Story 12.3: Distributed Tracing System**
- **Title:** End-to-End Request Tracing
- **Description:** As a DevOps Engineer, I want to trace requests across services so that I can debug issues effectively
- **Acceptance Criteria:**
  - Implements Jaeger for distributed tracing
  - Correlation IDs across all services
  - Trace visualization with timing breakdowns
  - Error propagation tracking
  - UI integration for trace viewing
  - Performance bottleneck identification
- **Label:** Shared

**Story 12.4: Auto-Scaling Configuration**
- **Title:** Dynamic Resource Scaling
- **Description:** As a system, I want to scale automatically based on load so that performance remains consistent
- **Acceptance Criteria:**
  - Kubernetes HPA configuration
  - Custom metrics for scaling decisions
  - Load testing to validate scaling
  - Cost optimization rules
  - UI shows current scaling status
  - Manual scaling override options
- **Label:** Shared

---

These epics and user stories provide a comprehensive roadmap for building the Apex IDP platform. Each story represents an independently testable and shippable unit of work that contributes to the overall system functionality. The vertical slicing ensures that each feature can be developed, tested, and deployed incrementally while maintaining system integrity.

The prioritization follows the logical dependencies: authentication and document upload form the foundation, followed by core processing capabilities, then validation workflows, and finally advanced features like analytics and integrations. This approach allows for early value delivery while building toward the complete vision outlined in your requirements documents.