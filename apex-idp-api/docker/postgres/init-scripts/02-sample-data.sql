-- 02-sample-data.sql
-- Insert sample data for development environment

-- Insert sample users (password is 'password123' hashed with bcrypt)
INSERT INTO user_management.users (id, username, email, password_hash, first_name, last_name, role, is_active, is_email_verified) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'admin', 'admin@apex-idp.com', '$2a$10$N3K.2.H9Q8PpZZu.K8P0dOQo1R5FXPzCl3t.fH7DhOo6LPr4Jx7SG', 'System', 'Administrator', 'ADMIN', true, true),
('550e8400-e29b-41d4-a716-446655440001', 'user1', 'user1@apex-idp.com', '$2a$10$N3K.2.H9Q8PpZZu.K8P0dOQo1R5FXPzCl3t.fH7DhOo6LPr4Jx7SG', 'John', 'Doe', 'USER', true, true),
('550e8400-e29b-41d4-a716-446655440002', 'manager1', 'manager1@apex-idp.com', '$2a$10$N3K.2.H9Q8PpZZu.K8P0dOQo1R5FXPzCl3t.fH7DhOo6LPr4Jx7SG', 'Jane', 'Smith', 'MANAGER', true, true)
ON CONFLICT (id) DO NOTHING;

-- Insert sample vendors
INSERT INTO vendor_management.vendors (id, name, code, email, phone, address, contact_person, status, created_by) VALUES
('660e8400-e29b-41d4-a716-446655440000', 'Acme Corporation', 'ACME001', 'contact@acme.com', '+1-555-0123', '123 Business St, New York, NY 10001', 'Alice Johnson', 'ACTIVE', 'admin'),
('660e8400-e29b-41d4-a716-446655440001', 'Global Tech Solutions', 'GTS002', 'info@globaltech.com', '+1-555-0456', '456 Tech Avenue, San Francisco, CA 94102', 'Bob Wilson', 'ACTIVE', 'admin'),
('660e8400-e29b-41d4-a716-446655440002', 'Supply Chain Partners', 'SCP003', 'sales@supplychain.com', '+1-555-0789', '789 Supply Road, Chicago, IL 60601', 'Carol Davis', 'ACTIVE', 'admin'),
('660e8400-e29b-41d4-a716-446655440003', 'International Logistics', 'IL004', 'contact@intllogistics.com', '+1-555-0987', '987 Logistics Way, Miami, FL 33101', 'David Brown', 'INACTIVE', 'admin')
ON CONFLICT (id) DO NOTHING;

-- Insert sample documents
INSERT INTO document_processing.documents (id, vendor_id, filename, original_filename, file_size, mime_type, storage_path, document_type, status, validation_status, metadata, created_by) VALUES
('770e8400-e29b-41d4-a716-446655440000', '660e8400-e29b-41d4-a716-446655440000', 'invoice_001_processed.pdf', 'ACME_Invoice_2024_001.pdf', 245760, 'application/pdf', '/documents/acme/invoice_001_processed.pdf', 'INVOICE', 'PROCESSED', 'VALID', '{"invoice_number": "INV-2024-001", "amount": 1250.00, "currency": "USD"}', 'user1'),
('770e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', 'purchase_order_002.pdf', 'GTS_PO_2024_002.pdf', 189432, 'application/pdf', '/documents/gts/purchase_order_002.pdf', 'PURCHASE_ORDER', 'PROCESSING', 'PENDING', '{"po_number": "PO-2024-002", "items_count": 15}', 'user1'),
('770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440002', 'contract_003.docx', 'SCP_Contract_2024_003.docx', 567890, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', '/documents/scp/contract_003.docx', 'CONTRACT', 'UPLOADED', 'PENDING', '{"contract_type": "service_agreement", "duration_months": 12}', 'manager1')
ON CONFLICT (id) DO NOTHING;

-- Insert sample domain events
INSERT INTO audit.domain_events (aggregate_id, aggregate_type, event_type, event_data, created_by) VALUES
('660e8400-e29b-41d4-a716-446655440000', 'Vendor', 'VendorCreated', '{"vendorId": "660e8400-e29b-41d4-a716-446655440000", "name": "Acme Corporation", "code": "ACME001", "status": "ACTIVE"}', 'admin'),
('660e8400-e29b-41d4-a716-446655440001', 'Vendor', 'VendorCreated', '{"vendorId": "660e8400-e29b-41d4-a716-446655440001", "name": "Global Tech Solutions", "code": "GTS002", "status": "ACTIVE"}', 'admin'),
('770e8400-e29b-41d4-a716-446655440000', 'Document', 'DocumentUploaded', '{"documentId": "770e8400-e29b-41d4-a716-446655440000", "vendorId": "660e8400-e29b-41d4-a716-446655440000", "filename": "ACME_Invoice_2024_001.pdf", "documentType": "INVOICE"}', 'user1'),
('770e8400-e29b-41d4-a716-446655440000', 'Document', 'DocumentProcessed', '{"documentId": "770e8400-e29b-41d4-a716-446655440000", "status": "PROCESSED", "validationStatus": "VALID", "processingTime": 5.2}', 'system')
ON CONFLICT (id) DO NOTHING;

-- Update sequences to avoid conflicts with manual inserts
SELECT setval('audit.domain_events_sequence_number_seq', 1000, true);
