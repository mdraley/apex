-- 01-init-database.sql
-- Initialize the Apex IDP database with proper extensions and schemas

-- Enable necessary PostgreSQL extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create schemas for different bounded contexts
CREATE SCHEMA IF NOT EXISTS vendor_management;
CREATE SCHEMA IF NOT EXISTS document_processing;
CREATE SCHEMA IF NOT EXISTS user_management;
CREATE SCHEMA IF NOT EXISTS audit;

-- Create audit table for event sourcing
CREATE TABLE IF NOT EXISTS audit.domain_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    aggregate_id VARCHAR(255) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    event_data JSONB NOT NULL,
    event_version INTEGER NOT NULL DEFAULT 1,
    occurred_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    sequence_number BIGSERIAL
);

-- Create index for efficient event retrieval
CREATE INDEX IF NOT EXISTS idx_domain_events_aggregate 
ON audit.domain_events(aggregate_id, aggregate_type);

CREATE INDEX IF NOT EXISTS idx_domain_events_type 
ON audit.domain_events(event_type);

CREATE INDEX IF NOT EXISTS idx_domain_events_occurred_at 
ON audit.domain_events(occurred_at);

-- Create users table in user_management schema
CREATE TABLE IF NOT EXISTS user_management.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP WITH TIME ZONE,
    version INTEGER DEFAULT 0
);

-- Create vendors table in vendor_management schema
CREATE TABLE IF NOT EXISTS vendor_management.vendors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    contact_person VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0
);

-- Create documents table in document_processing schema
CREATE TABLE IF NOT EXISTS document_processing.documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vendor_id UUID REFERENCES vendor_management.vendors(id),
    filename VARCHAR(500) NOT NULL,
    original_filename VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(255),
    storage_path VARCHAR(1000),
    document_type VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'UPLOADED',
    processing_started_at TIMESTAMP WITH TIME ZONE,
    processing_completed_at TIMESTAMP WITH TIME ZONE,
    validation_status VARCHAR(50),
    validation_errors JSONB,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON user_management.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON user_management.users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON user_management.users(role);

CREATE INDEX IF NOT EXISTS idx_vendors_code ON vendor_management.vendors(code);
CREATE INDEX IF NOT EXISTS idx_vendors_status ON vendor_management.vendors(status);
CREATE INDEX IF NOT EXISTS idx_vendors_name ON vendor_management.vendors(name);

CREATE INDEX IF NOT EXISTS idx_documents_vendor_id ON document_processing.documents(vendor_id);
CREATE INDEX IF NOT EXISTS idx_documents_status ON document_processing.documents(status);
CREATE INDEX IF NOT EXISTS idx_documents_document_type ON document_processing.documents(document_type);
CREATE INDEX IF NOT EXISTS idx_documents_created_at ON document_processing.documents(created_at);

-- Grant permissions to the apex_user
GRANT USAGE ON SCHEMA vendor_management TO apex_user;
GRANT USAGE ON SCHEMA document_processing TO apex_user;
GRANT USAGE ON SCHEMA user_management TO apex_user;
GRANT USAGE ON SCHEMA audit TO apex_user;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA vendor_management TO apex_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA document_processing TO apex_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA user_management TO apex_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA audit TO apex_user;

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA vendor_management TO apex_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA document_processing TO apex_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA user_management TO apex_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA audit TO apex_user;
