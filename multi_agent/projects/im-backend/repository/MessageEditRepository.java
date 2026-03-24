package com.im.system.repository;

import com.im.system.entity.MessageEditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 消息编辑仓储 - 消息编辑版本的数据访问层
 */
@Repository
public interface MessageEditRepository extends JpaRepository<MessageEditEntity, UUID> {
    
    // Basic CRUD queries
    Optional<MessageEditEntity> findByIdAndStatus(UUID id, String status);
    
    List<MessageEditEntity> findByMessageId(UUID messageId);
    
    List<MessageEditEntity> findByOriginalMessageId(UUID originalMessageId);
    
    List<MessageEditEntity> findByUserId(UUID userId);
    
    List<MessageEditEntity> findByConversationId(UUID conversationId);
    
    // Version management queries
    Optional<MessageEditEntity> findByMessageIdAndVersion(UUID messageId, Integer version);
    
    Optional<MessageEditEntity> findByMessageIdAndIsLatestTrue(UUID messageId);
    
    Integer countByMessageId(UUID messageId);
    
    Integer countByOriginalMessageId(UUID originalMessageId);
    
    List<MessageEditEntity> findByMessageIdAndStatus(UUID messageId, String status);
    
    List<MessageEditEntity> findByMessageIdAndStatusOrderByVersionDesc(UUID messageId, String status);
    
    // Search by edit type
    List<MessageEditEntity> findByEditType(String editType);
    
    List<MessageEditEntity> findByMessageIdAndEditType(UUID messageId, String editType);
    
    List<MessageEditEntity> findByUserIdAndEditType(UUID userId, String editType);
    
    // Search by version range
    List<MessageEditEntity> findByMessageIdAndVersionBetween(UUID messageId, Integer startVersion, Integer endVersion);
    
    List<MessageEditEntity> findByMessageIdAndVersionGreaterThanEqual(UUID messageId, Integer minVersion);
    
    List<MessageEditEntity> findByMessageIdAndVersionLessThanEqual(UUID messageId, Integer maxVersion);
    
    // Latest version queries
    List<MessageEditEntity> findByIsLatestTrue();
    
    List<MessageEditEntity> findByIsLatestTrueAndStatus(String status);
    
    List<MessageEditEntity> findByConversationIdAndIsLatestTrue(UUID conversationId);
    
    List<MessageEditEntity> findByUserIdAndIsLatestTrue(UUID userId);
    
    List<MessageEditEntity> findByUserIdAndConversationIdAndIsLatestTrue(UUID userId, UUID conversationId);
    
    // Search by content hash
    Optional<MessageEditEntity> findByContentHash(String contentHash);
    
    List<MessageEditEntity> findByOriginalContentHash(String originalContentHash);
    
    // Search by metadata
    List<MessageEditEntity> findByMetadataContaining(String metadataKeyword);
    
    List<MessageEditEntity> findByEditReasonContaining(String reasonKeyword);
    
    // Search by tags
    List<MessageEditEntity> findByTagsContaining(String tag);
    
    // Time-based queries
    List<MessageEditEntity> findByCreatedAtAfter(LocalDateTime after);
    
    List<MessageEditEntity> findByCreatedAtBefore(LocalDateTime before);
    
    List<MessageEditEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<MessageEditEntity> findByUpdatedAtAfter(LocalDateTime after);
    
    List<MessageEditEntity> findByUpdatedAtBefore(LocalDateTime before);
    
    List<MessageEditEntity> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Audit and status queries
    List<MessageEditEntity> findByAuditStatus(String auditStatus);
    
    List<MessageEditEntity> findByAuditStatusAndCreatedAtAfter(String auditStatus, LocalDateTime after);
    
    List<MessageEditEntity> findByAuditorId(UUID auditorId);
    
    List<MessageEditEntity> findByStatus(String status);
    
    List<MessageEditEntity> findByStatusAndCreatedAtAfter(String status, LocalDateTime after);
    
    // Privacy queries
    List<MessageEditEntity> findByPrivacyLevel(String privacyLevel);
    
    List<MessageEditEntity> findByUserIdAndPrivacyLevel(UUID userId, String privacyLevel);
    
    // Platform and device queries
    List<MessageEditEntity> findByPlatform(String platform);
    
    List<MessageEditEntity> findByDeviceId(String deviceId);
    
    List<MessageEditEntity> findByUserIdAndPlatform(UUID userId, String platform);
    
    // Sync status queries
    List<MessageEditEntity> findBySyncStatus(String syncStatus);
    
    List<MessageEditEntity> findBySyncStatusAndUpdatedAtBefore(String syncStatus, LocalDateTime before);
    
    List<MessageEditEntity> findByConflictResolution(String conflictResolution);
    
    // Expiration queries
    List<MessageEditEntity> findByExpiresAtBefore(LocalDateTime before);
    
    List<MessageEditEntity> findByExpiresAtAfter(LocalDateTime after);
    
    List<MessageEditEntity> findByVersionExpiresAtBefore(LocalDateTime before);
    
    List<MessageEditEntity> findByVersionExpiresAtAfter(LocalDateTime after);
    
    // Statistical queries
    @Query("SELECT COUNT(e) FROM MessageEditEntity e WHERE e.messageId = :messageId")
    Long countEditsByMessageId(@Param("messageId") UUID messageId);
    
    @Query("SELECT MAX(e.version) FROM MessageEditEntity e WHERE e.messageId = :messageId")
    Optional<Integer> findMaxVersionByMessageId(@Param("messageId") UUID messageId);
    
    @Query("SELECT COUNT(e) FROM MessageEditEntity e WHERE e.userId = :userId AND e.status = 'ACTIVE'")
    Long countActiveEditsByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT COUNT(e) FROM MessageEditEntity e WHERE e.conversationId = :conversationId AND e.status = 'ACTIVE'")
    Long countActiveEditsByConversationId(@Param("conversationId") UUID conversationId);
    
    @Query("SELECT e.editType, COUNT(e) FROM MessageEditEntity e GROUP BY e.editType")
    List<Object[]> countByEditType();
    
    @Query("SELECT e.platform, COUNT(e) FROM MessageEditEntity e GROUP BY e.platform")
    List<Object[]> countByPlatform();
    
    @Query("SELECT e.auditStatus, COUNT(e) FROM MessageEditEntity e GROUP BY e.auditStatus")
    List<Object[]> countByAuditStatus();
    
    // Bulk operations
    @Modifying
    @Query("UPDATE MessageEditEntity e SET e.isLatest = false WHERE e.messageId = :messageId")
    int markAllAsNotLatestByMessageId(@Param("messageId") UUID messageId);
    
    @Modifying
    @Query("UPDATE MessageEditEntity e SET e.status = :newStatus WHERE e.id IN :ids")
    int updateStatusByIds(@Param("ids") List<UUID> ids, @Param("newStatus") String newStatus);
    
    @Modifying
    @Query("UPDATE MessageEditEntity e SET e.auditStatus = :auditStatus, e.auditorId = :auditorId, " +
           "e.auditNotes = :auditNotes, e.auditTimestamp = :auditTimestamp WHERE e.id IN :ids")
    int updateAuditStatusByIds(@Param("ids") List<UUID> ids, @Param("auditStatus") String auditStatus,
                               @Param("auditorId") UUID auditorId, @Param("auditNotes") String auditNotes,
                               @Param("auditTimestamp") LocalDateTime auditTimestamp);
    
    @Modifying
    @Query("UPDATE MessageEditEntity e SET e.syncStatus = :syncStatus WHERE e.syncStatus = :oldSyncStatus AND e.updatedAt < :before")
    int updateSyncStatusByTime(@Param("oldSyncStatus") String oldSyncStatus, 
                               @Param("syncStatus") String syncStatus, 
                               @Param("before") LocalDateTime before);
    
    @Modifying
    @Query("UPDATE MessageEditEntity e SET e.status = 'ARCHIVED', e.archivedAt = :archivedAt " +
           "WHERE e.status = 'ACTIVE' AND e.updatedAt < :before")
    int archiveOldEdits(@Param("before") LocalDateTime before, @Param("archivedAt") LocalDateTime archivedAt);
    
    @Modifying
    @Query("UPDATE MessageEditEntity e SET e.status = 'DELETED', e.deletedAt = :deletedAt " +
           "WHERE e.status IN ('ACTIVE', 'ARCHIVED') AND e.expiresAt < :now")
    int deleteExpiredEdits(@Param("now") LocalDateTime now, @Param("deletedAt") LocalDateTime deletedAt);
    
    // Complex queries with joins
    @Query("SELECT e FROM MessageEditEntity e JOIN MessageEntity m ON e.messageId = m.id " +
           "WHERE m.conversationId = :conversationId AND e.status = 'ACTIVE'")
    List<MessageEditEntity> findByConversationIdWithMessage(@Param("conversationId") UUID conversationId);
    
    @Query("SELECT e FROM MessageEditEntity e WHERE e.userId = :userId AND " +
           "EXISTS (SELECT 1 FROM MessageEntity m WHERE m.id = e.messageId AND m.conversationId = :conversationId)")
    List<MessageEditEntity> findByUserIdAndConversationId(@Param("userId") UUID userId, 
                                                         @Param("conversationId") UUID conversationId);
    
    // Pagination queries
    @Query("SELECT e FROM MessageEditEntity e WHERE e.messageId = :messageId ORDER BY e.version DESC")
    List<MessageEditEntity> findEditsByMessageIdOrderByVersionDesc(@Param("messageId") UUID messageId);
    
    @Query("SELECT e FROM MessageEditEntity e WHERE e.userId = :userId ORDER BY e.createdAt DESC")
    List<MessageEditEntity> findEditsByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);
    
    @Query("SELECT e FROM MessageEditEntity e WHERE e.conversationId = :conversationId ORDER BY e.createdAt DESC")
    List<MessageEditEntity> findEditsByConversationIdOrderByCreatedAtDesc(@Param("conversationId") UUID conversationId);
    
    // Search with multiple criteria
    @Query("SELECT e FROM MessageEditEntity e WHERE " +
           "(:messageId IS NULL OR e.messageId = :messageId) AND " +
           "(:userId IS NULL OR e.userId = :userId) AND " +
           "(:conversationId IS NULL OR e.conversationId = :conversationId) AND " +
           "(:editType IS NULL OR e.editType = :editType) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:auditStatus IS NULL OR e.auditStatus = :auditStatus) AND " +
           "(:platform IS NULL OR e.platform = :platform) AND " +
           "(:startDate IS NULL OR e.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR e.createdAt <= :endDate) " +
           "ORDER BY e.createdAt DESC")
    List<MessageEditEntity> searchEdits(@Param("messageId") UUID messageId,
                                        @Param("userId") UUID userId,
                                        @Param("conversationId") UUID conversationId,
                                        @Param("editType") String editType,
                                        @Param("status") String status,
                                        @Param("auditStatus") String auditStatus,
                                        @Param("platform") String platform,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    // Content search (for full-text search)
    @Query("SELECT e FROM MessageEditEntity e WHERE e.content LIKE %:keyword% OR e.editReason LIKE %:keyword%")
    List<MessageEditEntity> searchByContentOrReason(@Param("keyword") String keyword);
    
    @Query("SELECT e FROM MessageEditEntity e WHERE e.originalContent LIKE %:keyword%")
    List<MessageEditEntity> searchByOriginalContent(@Param("keyword") String keyword);
    
    // Performance monitoring queries
    @Query("SELECT COUNT(e) FROM MessageEditEntity e WHERE e.createdAt >= :start AND e.createdAt < :end")
    Long countEditsInTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT e.userId, COUNT(e) FROM MessageEditEntity e " +
           "WHERE e.createdAt >= :start AND e.createdAt < :end " +
           "GROUP BY e.userId ORDER BY COUNT(e) DESC")
    List<Object[]> countEditsByUserInTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT e.platform, COUNT(e) FROM MessageEditEntity e " +
           "WHERE e.createdAt >= :start AND e.createdAt < :end " +
           "GROUP BY e.platform ORDER BY COUNT(e) DESC")
    List<Object[]> countEditsByPlatformInTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Cleanup queries
    @Modifying
    @Query("DELETE FROM MessageEditEntity e WHERE e.status = 'DELETED' AND e.deletedAt < :before")
    int permanentlyDeleteOldDeletedEdits(@Param("before") LocalDateTime before);
    
    @Modifying
    @Query("DELETE FROM MessageEditEntity e WHERE e.versionExpiresAt IS NOT NULL AND e.versionExpiresAt < :now")
    int deleteExpiredVersions(@Param("now") LocalDateTime now);
}