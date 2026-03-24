package com.im.backend.repository;

import com.im.backend.entity.StickerEntity;
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

@Repository
public interface StickerRepository extends JpaRepository<StickerEntity, Long> {
    
    // Basic CRUD queries
    Optional<StickerEntity> findByStickerId(String stickerId);
    
    boolean existsByStickerId(String stickerId);
    
    Optional<StickerEntity> findByIdAndIsDeletedFalse(Long id);
    
    Optional<StickerEntity> findByStickerIdAndIsDeletedFalse(String stickerId);
    
    // Sticker pack relationship queries
    List<StickerEntity> findByStickerPackAndIsDeletedFalse(StickerPackEntity stickerPack);
    
    List<StickerEntity> findByStickerPackIdAndIsDeletedFalse(Long packId);
    
    List<StickerEntity> findByStickerPackPackIdAndIsDeletedFalse(String packId);
    
    Optional<StickerEntity> findByStickerPackAndStickerIdAndIsDeletedFalse(StickerPackEntity stickerPack, String stickerId);
    
    // Search queries
    List<StickerEntity> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
    
    List<StickerEntity> findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(String description);
    
    // Type and format queries
    List<StickerEntity> findByIsAnimatedTrueAndIsDeletedFalse();
    
    List<StickerEntity> findByIsAnimatedFalseAndIsDeletedFalse();
    
    List<StickerEntity> findByFileFormatAndIsDeletedFalse(String fileFormat);
    
    List<StickerEntity> findByMimeTypeAndIsDeletedFalse(String mimeType);
    
    // Size-based queries
    List<StickerEntity> findByWidthAndHeightAndIsDeletedFalse(Integer width, Integer height);
    
    List<StickerEntity> findByWidthGreaterThanEqualAndHeightGreaterThanEqualAndIsDeletedFalse(Integer minWidth, Integer minHeight);
    
    List<StickerEntity> findByWidthLessThanEqualAndHeightLessThanEqualAndIsDeletedFalse(Integer maxWidth, Integer maxHeight);
    
    List<StickerEntity> findByFileSizeBytesLessThanEqualAndIsDeletedFalse(Long maxSize);
    
    // Popularity and usage queries
    @Query("SELECT s FROM StickerEntity s WHERE s.isDeleted = false ORDER BY s.totalUses DESC")
    Page<StickerEntity> findTopByUsage(Pageable pageable);
    
    @Query("SELECT s FROM StickerEntity s WHERE s.isDeleted = false ORDER BY s.totalFavorites DESC")
    Page<StickerEntity> findTopByFavorites(Pageable pageable);
    
    @Query("SELECT s FROM StickerEntity s WHERE s.isDeleted = false ORDER BY s.createdAt DESC")
    Page<StickerEntity> findNewest(Pageable pageable);
    
    // Sticker pack specific ordering
    List<StickerEntity> findByStickerPackAndIsDeletedFalseOrderBySortOrderAsc(StickerPackEntity stickerPack);
    
    List<StickerEntity> findByStickerPackIdAndIsDeletedFalseOrderBySortOrderAsc(Long packId);
    
    // Complex search with multiple criteria
    @Query("SELECT s FROM StickerEntity s WHERE " +
           "(s.isDeleted = false) AND " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:isAnimated IS NULL OR s.isAnimated = :isAnimated) AND " +
           "(:fileFormat IS NULL OR s.fileFormat = :fileFormat) AND " +
           "(:minWidth IS NULL OR s.width >= :minWidth) AND " +
           "(:maxWidth IS NULL OR s.width <= :maxWidth) AND " +
           "(:minHeight IS NULL OR s.height >= :minHeight) AND " +
           "(:maxHeight IS NULL OR s.height <= :maxHeight) AND " +
           "(:maxSize IS NULL OR s.fileSizeBytes <= :maxSize)")
    Page<StickerEntity> searchStickers(
            @Param("name") String name,
            @Param("isAnimated") Boolean isAnimated,
            @Param("fileFormat") String fileFormat,
            @Param("minWidth") Integer minWidth,
            @Param("maxWidth") Integer maxWidth,
            @Param("minHeight") Integer minHeight,
            @Param("maxHeight") Integer maxHeight,
            @Param("maxSize") Long maxSize,
            Pageable pageable);
    
    // Full-text search (if using PostgreSQL full-text search)
    @Query(value = "SELECT * FROM stickers WHERE " +
           "to_tsvector('english', name || ' ' || COALESCE(description, '')) @@ " +
           "to_tsquery('english', :query) AND is_deleted = false",
           nativeQuery = true)
    List<StickerEntity> fullTextSearch(@Param("query") String query);
    
    // Analytics queries
    @Query("SELECT COUNT(s) FROM StickerEntity s WHERE s.isDeleted = false")
    Long countActiveStickers();
    
    @Query("SELECT COUNT(s) FROM StickerEntity s WHERE s.isDeleted = false AND s.isAnimated = true")
    Long countAnimatedStickers();
    
    @Query("SELECT COALESCE(SUM(s.totalUses), 0) FROM StickerEntity s WHERE s.isDeleted = false")
    Long sumTotalUses();
    
    @Query("SELECT COALESCE(SUM(s.totalFavorites), 0) FROM StickerEntity s WHERE s.isDeleted = false")
    Long sumTotalFavorites();
    
    @Query("SELECT COALESCE(SUM(s.fileSizeBytes), 0) FROM StickerEntity s WHERE s.isDeleted = false")
    Long sumTotalFileSize();
    
    @Query("SELECT COALESCE(AVG(s.width), 0) FROM StickerEntity s WHERE s.isDeleted = false")
    Double getAverageWidth();
    
    @Query("SELECT COALESCE(AVG(s.height), 0) FROM StickerEntity s WHERE s.isDeleted = false")
    Double getAverageHeight();
    
    // Format statistics
    @Query("SELECT s.fileFormat, COUNT(s) as count FROM StickerEntity s " +
           "WHERE s.isDeleted = false GROUP BY s.fileFormat ORDER BY count DESC")
    List<Object[]> getFileFormatStatistics();
    
    // Sticker pack statistics
    @Query("SELECT s.stickerPack.packId, COUNT(s) as stickerCount, " +
           "COALESCE(SUM(s.totalUses), 0) as totalUses, " +
           "COALESCE(SUM(s.totalFavorites), 0) as totalFavorites " +
           "FROM StickerEntity s WHERE s.isDeleted = false " +
           "GROUP BY s.stickerPack.packId ORDER BY stickerCount DESC")
    List<Object[]> getStickerPackStatistics();
    
    // Time-based statistics
    @Query("SELECT DATE(s.createdAt), COUNT(s) FROM StickerEntity s " +
           "WHERE s.isDeleted = false AND s.createdAt >= :startDate " +
           "GROUP BY DATE(s.createdAt) ORDER BY DATE(s.createdAt)")
    List<Object[]> getCreationStatsByDate(@Param("startDate") LocalDateTime startDate);
    
    // Version and ETag queries
    Optional<StickerEntity> findByIdAndVersion(Long id, Long version);
    
    Optional<StickerEntity> findByStickerIdAndEtag(String stickerId, String etag);
    
    // Bulk operations
    @Query("UPDATE StickerEntity s SET s.isActive = :isActive WHERE s.id IN :ids")
    int bulkUpdateActiveStatus(@Param("ids") List<Long> ids, @Param("isActive") Boolean isActive);
    
    @Query("UPDATE StickerEntity s SET s.isDeleted = true, s.deletedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int bulkDelete(@Param("ids") List<Long> ids);
    
    // Update usage statistics
    @Query("UPDATE StickerEntity s SET s.totalUses = s.totalUses + 1, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :id")
    int incrementUsage(@Param("id") Long id);
    
    @Query("UPDATE StickerEntity s SET s.totalFavorites = s.totalFavorites + 1, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :id")
    int incrementFavorites(@Param("id") Long id);
    
    // Batch increment usage for multiple stickers
    @Query("UPDATE StickerEntity s SET s.totalUses = s.totalUses + 1, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int batchIncrementUsage(@Param("ids") List<Long> ids);
    
    // Find stickers by usage threshold
    @Query("SELECT s FROM StickerEntity s WHERE s.isDeleted = false AND s.totalUses >= :minUses")
    List<StickerEntity> findByMinUsage(@Param("minUses") Long minUses);
    
    @Query("SELECT s FROM StickerEntity s WHERE s.isDeleted = false AND s.totalFavorites >= :minFavorites")
    List<StickerEntity> findByMinFavorites(@Param("minFavorites") Long minFavorites);
    
    // Find related stickers (same pack or similar characteristics)
    @Query("SELECT s FROM StickerEntity s WHERE " +
           "s.isDeleted = false AND " +
           "s.stickerPack = :stickerPack AND " +
           "s.id != :excludeId " +
           "ORDER BY s.sortOrder ASC")
    List<StickerEntity> findRelatedStickers(
            @Param("stickerPack") StickerPackEntity stickerPack,
            @Param("excludeId") Long excludeId);
    
    // Find stickers by checksum (for duplicate detection)
    List<StickerEntity> findByChecksumAndChecksumAlgorithmAndIsDeletedFalse(String checksum, String checksumAlgorithm);
    
    // Find stickers with missing metadata
    @Query("SELECT s FROM StickerEntity s WHERE " +
           "s.isDeleted = false AND " +
           "(s.width IS NULL OR s.height IS NULL OR s.fileSizeBytes IS NULL OR s.mimeType IS NULL)")
    List<StickerEntity> findStickersWithMissingMetadata();
    
    // Pagination with filters
    @Query("SELECT s FROM StickerEntity s WHERE s.isDeleted = false AND s.isActive = true")
    Page<StickerEntity> findAllActive(Pageable pageable);
    
    // Find by multiple IDs
    List<StickerEntity> findByIdInAndIsDeletedFalse(List<Long> ids);
    
    List<StickerEntity> findByStickerIdInAndIsDeletedFalse(List<String> stickerIds);
    
    // Custom query for sticker discovery
    @Query("SELECT s FROM StickerEntity s WHERE " +
           "s.isDeleted = false AND " +
           "s.isActive = true AND " +
           "s.stickerPack.isActive = true AND " +
           "s.stickerPack.isDeleted = false AND " +
           "s.stickerPack.approvalStatus = 'APPROVED' AND " +
           "(:category IS NULL OR s.stickerPack.category = :category) " +
           "ORDER BY (s.totalUses * 0.4 + s.totalFavorites * 0.6) DESC")
    Page<StickerEntity> discoverStickers(@Param("category") String category, Pageable pageable);
    
    // Query for trending stickers (last 30 days)
    @Query("SELECT s FROM StickerEntity s WHERE " +
           "s.isDeleted = false AND " +
           "s.isActive = true AND " +
           "s.stickerPack.isActive = true AND " +
           "s.stickerPack.approvalStatus = 'APPROVED' AND " +
           "s.updatedAt >= :sinceDate " +
           "ORDER BY (s.totalUses / (EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - s.updatedAt)) / 86400 + 1)) DESC")
    Page<StickerEntity> findTrendingStickers(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);
    
    // Find random stickers for discovery
    @Query(value = "SELECT * FROM stickers WHERE is_deleted = false AND is_active = true " +
           "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<StickerEntity> findRandomStickers(@Param("limit") int limit);
}