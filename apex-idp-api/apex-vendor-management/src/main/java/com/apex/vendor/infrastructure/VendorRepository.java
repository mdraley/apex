package com.apex.vendor.infrastructure;

import com.apex.vendor.domain.Vendor;
import com.apex.vendor.domain.VendorStatus;
import com.apex.vendor.domain.VendorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Vendor entities
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    
    Optional<Vendor> findByTaxId(String taxId);
    
    List<Vendor> findByStatus(VendorStatus status);
    
    List<Vendor> findByType(VendorType type);
    
    Page<Vendor> findByStatusAndType(VendorStatus status, VendorType type, Pageable pageable);
    
    @Query("SELECT v FROM Vendor v WHERE " +
           "LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "v.taxId LIKE CONCAT('%', :searchTerm, '%')")
    Page<Vendor> searchVendors(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.status = :status")
    long countByStatus(@Param("status") VendorStatus status);
}
