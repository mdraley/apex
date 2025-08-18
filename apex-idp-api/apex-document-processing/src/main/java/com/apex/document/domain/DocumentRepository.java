package com.apex.document.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Document aggregate
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
}
