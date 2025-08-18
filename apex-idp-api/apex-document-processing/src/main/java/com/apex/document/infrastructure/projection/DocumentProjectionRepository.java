package com.apex.document.infrastructure.projection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for document projections (read-side of CQRS)
 */
@Repository
public interface DocumentProjectionRepository extends JpaRepository<DocumentProjection, String> {
    
    List<DocumentProjection> findByUserId(String userId);
    
    List<DocumentProjection> findByUserIdAndStage(String userId, String stage);
    
    @Query("SELECT dp FROM DocumentProjection dp WHERE dp.userId = :userId AND dp.filename LIKE %:filename%")
    List<DocumentProjection> findByUserIdAndFilenameContaining(@Param("userId") String userId, 
                                                               @Param("filename") String filename);
    
    Optional<DocumentProjection> findByUserIdAndId(String userId, String id);
    
    long countByUserId(String userId);
    
    long countByUserIdAndStage(String userId, String stage);
    
    // Additional methods needed by services
    long countByCreatedAtAfter(LocalDateTime dateTime);
    
    @Query("SELECT AVG(dp.confidenceScore) FROM DocumentProjection dp WHERE dp.confidenceScore IS NOT NULL")
    Double calculateAverageConfidence();
    
    @Query("SELECT dp FROM DocumentProjection dp WHERE dp.stage = :stage AND dp.confidenceScore < :threshold")
    org.springframework.data.domain.Page<DocumentProjection> findByStageAndConfidenceScoreLessThan(
        @Param("stage") String stage, 
        @Param("threshold") Double threshold, 
        org.springframework.data.domain.Pageable pageable);
        
    long countByStage(String stage);
}
