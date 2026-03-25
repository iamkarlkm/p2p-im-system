package com.im.backend.repository;

import com.im.backend.entity.StickerPackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StickerPackRepository extends JpaRepository<StickerPackEntity, Long> {
    
    // Basic CRUD queries
    Optional<StickerPackEntity> findByPackId(String packId);
    
    boolean existsByPackId(String packId);
    
    Optional<StickerPackEntity> findByIdAndIsDeletedFalse(Long id);
    
    Optional<StickerPackEntity> findByPackIdAndIsDeletedFalse(String packId);
    
    // Search by various criteria
    List<StickerPackEntity> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
    
    List<StickerPackEntity> findByAuthorContainingIgnoreCaseAndIsDeletedFalse(String author);
    
    List<StickerPackEntity> findByCategoryAndIsDeletedFalse(String category);
    
    List<StickerPackEntity> findByIsOfficialTrueAndIsDeletedFalse();
    
    List<StickerPackEntity> findByIsFeaturedTrueAndIsDeletedFalse();
    
    List<StickerPackEntity> findByIsFreeTrueAndIsDeletedFalse();
    
    List<StickerPackEntity> findByIsActiveTrueAndIsDeletedFalse();
    
    List<StickerPackEntity> findByApprovalStatusAndIsDeletedFalse(String approvalStatus);
    
    // Tag-based queries
    @Query("SELECT sp FROM StickerPackEntity sp JOIN sp.tags t WHERE t IN :tags AND sp.isDeleted = false")
    List<StickerPackEntity> findByTagsInAndIsDeletedFalse(@Param("tags") Set<String> tags);
    
    @Query("SELECT sp FROM StickerPackEntity sp WHERE :tag MEMBER OF sp.tags AND sp.isDeleted = false")
    List<StickerPackEntity> findByTagAndIsDeletedFalse(@Param("tag") String tag);
    
    // User-related queries
    List<StickerPackEntity> findByCreatedByUserIdAndIsDeletedFalse(String userId);
    
    List<StickerPackEntity> findByCreatedByUserIdAndApprovalStatusAndIsDeletedFalse(String userId, String approvalStatus);
    
    List<StickerPackEntity> findByApprovedByUserIdAndIsDeletedFalse(String userId);
    
    // Date-based queries
    List<StickerPackEntity> findByCreatedAtAfterAndIsDeletedFalse(LocalDateTime date);
    
    List<StickerPackEntity> findByUpdatedAtAfterAndIsDeletedFalse(LocalDateTime date);
    
    List<StickerPackEntity> findByPublishedAtAfterAndIsDeletedFalse(LocalDateTime date);
    
    List<StickerPackEntity> findByFeaturedAtAfterAndIsDeletedFalse(LocalDateTime date);
    
    // Statistics and ranking queries
    @Query("SELECT sp FROM StickerPackEntity sp WHERE sp.isDeleted = false ORDER BY sp.totalDownloads DESC")
    Page<StickerPackEntity> findTopByDownloads(Pageable pageable);
    
    @Query("SELECT sp FROM StickerPackEntity sp WHERE sp.isDeleted = false ORDER BY sp.totalLikes DESC")
    Page<StickerPackEntity> findTopByLikes(Pageable pageable);
    
    @Query("SELECT sp FROM StickerPackEntity sp WHERE sp.isDeleted = false ORDER BY sp.averageRating DESC")
    Page<StickerPackEntity> findTopByRating(Pageable pageable);
    
    @Query("SELECT sp FROM StickerPackEntity sp WHERE sp.isDeleted = false AND sp.publishedAt IS NOT NULL ORDER BY sp.publishedAt DESC")
    Page<StickerPackEntity> findNewest(Pageable pageable);
    
    // Complex search with multiple criteria
    @Query("SELECT sp FROM StickerPackEntity sp WHERE " +
           "(sp.isDeleted = false) AND " +
           "(sp.isActive = true) AND " +
           "(:name IS NULL OR LOWER(sp.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:author IS NULL OR LOWER(sp.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:category IS NULL OR sp.category = :category) AND " +
           "(:isOfficial IS NULL OR sp.isOfficial = :isOfficial) AND " +
           "(:isFeatured IS NULL OR sp.isFeatured = :isFeatured) AND " +
           "(:isFree IS NULL OR sp.isFree = :isFree) AND " +
           "(:minRating IS NULL OR sp.averageRating >= :minRating) AND " +
           "(:minDownloads IS NULL OR sp.totalDownloads >= :minDownloads)")
    Page<StickerPackEntity> searchStickerPacks(
            @Param("name") String name,
            @Param("author") String author,
            @Param("category") String category,
            @Param("isOfficial") Boolean isOfficial,
            @Param("isFeatured") Boolean isFeatured,
            @Param("isFree") Boolean isFree,
            @Param("minRating") Float minRating,
            @Param("minDownloads") Long minDownloads,
            Pageable pageable);
    
    // Full-text search (if using PostgreSQL full-text search)
    @Query(value = "SELECT * FROM sticker_packs WHERE " +
           "to_tsvector('english', name || ' ' || COALESCE(description, '') || ' ' || author) @@ " +
           "to_tsquery('english', :query) AND is_deleted = false",
           nativeQuery = true)
    List<StickerPackEntity> fullTextSearch(@Param("query") String query);
    
    // Analytics queries
    @Query("SELECT COUNT(sp) FROM StickerPackEntity sp WHERE sp.isDeleted = false")
    Long countActiveStickerPacks();
    
    @Query("SELECT COUNT(sp) FROM StickerPackEntity sp WHERE sp.isDeleted = false AND sp.isOfficial = true")
    Long countOfficialStickerPacks();
    
    @Query("SELECT COUNT(sp) FROM StickerPackEntity sp WHERE sp.isDeleted = false AND sp.isFeatured = true")
    Long countFeaturedStickerPacks();
    
    @Query("SELECT COUNT(sp) FROM StickerPackEntity sp WHERE sp.isDeleted = false AND sp.isFree = true")
    Long countFreeStickerPacks();
    
    @Query("SELECT COUNT(sp) FROM StickerPackEntity sp WHERE sp.isDeleted = false AND sp.approvalStatus = 'PENDING'")
    Long countPendingStickerPacks();
    
    @Query("SELECT COALESCE(SUM(sp.totalDownloads), 0) FROM StickerPackEntity sp WHERE sp.isDeleted = false")
    Long sumTotalDownloads();
    
    @Query("SELECT COALESCE(SUM(sp.totalLikes), 0) FROM StickerPackEntity sp WHERE sp.isDeleted = false")
    Long sumTotalLikes();
    
    @Query("SELECT COALESCE(AVG(sp.averageRating), 0) FROM StickerPackEntity sp WHERE sp.isDeleted = false")
    Double getAverageRating();
    
    // Category statistics
    @Query("SELECT sp.category, COUNT(sp) as count FROM StickerPackEntity sp " +
           "WHERE sp.isDeleted = false GROUP BY sp.category ORDER BY count DESC")
    List<Object[]> getCategoryStatistics();
    
    // Time-based statistics
    @Query("SELECT DATE(sp.createdAt), COUNT(sp) FROM StickerPackEntity sp " +
           "WHERE sp.isDeleted = false AND sp.createdAt >= :startDate " +
           "GROUP BY DATE(sp.createdAt) ORDER BY DATE(sp.createdAt)")
    List<Object[]> getCreationStatsByDate(@Param("startDate") LocalDateTime startDate);
    
    // Version and ETag queries
    Optional<StickerPackEntity> findByIdAndVersion(Long id, Long version);
    
    Optional<StickerPackEntity> findByPackIdAndEtag(String packId, String etag);
    
    // Bulk operations
    @Query("UPDATE StickerPackEntity sp SET sp.isActive = :isActive WHERE sp.id IN :ids")
    int bulkUpdateActiveStatus(@Param("ids") List<Long> ids, @Param("isActive") Boolean isActive);
    
    @Query("UPDATE StickerPackEntity sp SET sp.isDeleted = true, sp.deletionReason = :reason, sp.deletedAt = CURRENT_TIMESTAMP WHERE sp.id IN :ids")
    int bulkDelete(@Param("ids") List<Long> ids, @Param("reason") String reason);
    
    @Query("UPDATE StickerPackEntity sp SET sp.approvalStatus = :status, sp.approvedByUserId = :userId, sp.publishedAt = CASE WHEN :status = 'APPROVED' THEN CURRENT_TIMESTAMP ELSE sp.publishedAt END WHERE sp.id IN :ids")
    int bulkUpdateApprovalStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("userId") String userId);
    
    // Pagination with filters
    @Query("SELECT sp FROM StickerPackEntity sp WHERE sp.isDeleted = false AND sp.isActive = true")
    Page<StickerPackEntity> findAllActive(Pageable pageable);
    
    @Query("SELECT sp FROM StickerPackEntity sp WHERE sp.isDeleted = false AND sp.approvalStatus = 'APPROVED'")
    Page<StickerPackEntity> findAllApproved(Pageable pageable);
    
    // Find by multiple IDs
    List<StickerPackEntity> findByIdInAndIsDeletedFalse(List<Long> ids);
    
    // Custom query for recommendation engine
    @Query("SELECT sp FROM StickerPackEntity sp WHERE " +
           "sp.isDeleted = false AND " +
           "sp.isActive = true AND " +
           "sp.approvalStatus = 'APPROVED' AND " +
           "sp.category IN :categories AND " +
           "sp.id NOT IN :excludedIds " +
           "ORDER BY (sp.totalDownloads * 0.3 + sp.totalLikes * 0.4 + sp.averageRating * 0.3) DESC")
    Page<StickerPackEntity> findRecommendations(
            @Param("categories") List<String> categories,
            @Param("excludedIds") List<Long> excludedIds,
            Pageable pageable);
    
    // Query for trending sticker packs (last 7 days)
    @Query("SELECT sp FROM StickerPackEntity sp WHERE " +
           "sp.isDeleted = false AND " +
           "sp.isActive = true AND " +
           "sp.approvalStatus = 'APPROVED' AND " +
           "sp.updatedAt >= :sinceDate " +
           "ORDER BY (sp.totalDownloads / (EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - sp.updatedAt)) / 86400 + 1)) DESC")
    Page<StickerPackEntity> findTrending(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);
}