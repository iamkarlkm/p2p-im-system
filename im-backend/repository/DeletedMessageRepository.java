package com.im.backend.repository;

import com.im.backend.entity.DeletedMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 已删除消息仓储接口
 * 提供已删除消息的查询和统计功能
 */
@Repository
public interface DeletedMessageRepository extends JpaRepository<DeletedMessageEntity, Long> {

    // 基本查询

    Optional<DeletedMessageEntity> findByOriginalMessageId(String originalMessageId);

    List<DeletedMessageEntity> findBySenderId(String senderId);

    List<DeletedMessageEntity> findByReceiverId(String receiverId);

    List<DeletedMessageEntity> findByDeletedByUserId(String deletedByUserId);

    List<DeletedMessageEntity> findByDeletedByUserIdAndDeletedByType(String deletedByUserId, DeletedMessageEntity.DeletedByType deletedByType);

    // 按条件查询

    List<DeletedMessageEntity> findByDeleteReason(DeletedMessageEntity.DeleteReason deleteReason);

    List<DeletedMessageEntity> findByReceiverType(DeletedMessageEntity.ReceiverType receiverType);

    List<DeletedMessageEntity> findByAuditStatus(DeletedMessageEntity.AuditStatus auditStatus);

    List<DeletedMessageEntity> findByAdminVisible(boolean adminVisible);

    List<DeletedMessageEntity> findByPermanentlyDeleted(boolean permanentlyDeleted);

    // 时间范围查询

    List<DeletedMessageEntity> findByDeletedAtBetween(LocalDateTime start, LocalDateTime end);

    List<DeletedMessageEntity> findByDeletedAtAfter(LocalDateTime start);

    List<DeletedMessageEntity> findByDeletedAtBefore(LocalDateTime end);

    List<DeletedMessageEntity> findByPermanentDeleteAtBetween(LocalDateTime start, LocalDateTime end);

    List<DeletedMessageEntity> findByExpireDeleteAtBefore(LocalDateTime expireTime);

    // 复合查询

    List<DeletedMessageEntity> findBySenderIdAndDeleteReason(String senderId, DeletedMessageEntity.DeleteReason deleteReason);

    List<DeletedMessageEntity> findByReceiverIdAndReceiverType(String receiverId, DeletedMessageEntity.ReceiverType receiverType);

    List<DeletedMessageEntity> findByDeletedByUserIdAndDeleteReason(String deletedByUserId, DeletedMessageEntity.DeleteReason deleteReason);

    List<DeletedMessageEntity> findByDeleteReasonAndAuditStatus(DeletedMessageEntity.DeleteReason deleteReason, DeletedMessageEntity.AuditStatus auditStatus);

    // 分页查询

    Page<DeletedMessageEntity> findBySenderId(String senderId, Pageable pageable);

    Page<DeletedMessageEntity> findByReceiverId(String receiverId, Pageable pageable);

    Page<DeletedMessageEntity> findByDeletedByUserId(String deletedByUserId, Pageable pageable);

    Page<DeletedMessageEntity> findByDeleteReason(DeletedMessageEntity.DeleteReason deleteReason, Pageable pageable);

    Page<DeletedMessageEntity> findByAuditStatus(DeletedMessageEntity.AuditStatus auditStatus, Pageable pageable);

    Page<DeletedMessageEntity> findByAdminVisible(boolean adminVisible, Pageable pageable);

    // 自定义查询

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.senderId = :senderId AND d.deletedByUserId != :senderId")
    List<DeletedMessageEntity> findMessagesDeletedByOthers(@Param("senderId") String senderId);

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.senderId != :deletedByUserId AND d.deletedByUserId = :deletedByUserId")
    List<DeletedMessageEntity> findMessagesDeletedByUserForOthers(@Param("deletedByUserId") String deletedByUserId);

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.adminVisible = true AND d.auditStatus = 'PENDING'")
    List<DeletedMessageEntity> findPendingAdminReviewMessages();

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.needsReview = true AND d.auditStatus = 'PENDING'")
    List<DeletedMessageEntity> findMessagesNeedingReview();

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.permanentlyDeleted = false AND d.expireDeleteAt < :currentTime")
    List<DeletedMessageEntity> findExpiredMessages(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.operationLogId IS NOT NULL AND d.deletedByType = 'ADMIN'")
    List<DeletedMessageEntity> findAdminDeletedWithLog();

    // 统计查询

    @Query("SELECT COUNT(d) FROM DeletedMessageEntity d WHERE d.senderId = :senderId")
    Long countBySender(@Param("senderId") String senderId);

    @Query("SELECT COUNT(d) FROM DeletedMessageEntity d WHERE d.receiverId = :receiverId")
    Long countByReceiver(@Param("receiverId") String receiverId);

    @Query("SELECT COUNT(d) FROM DeletedMessageEntity d WHERE d.deletedByUserId = :deletedByUserId")
    Long countByDeleter(@Param("deletedByUserId") String deletedByUserId);

    @Query("SELECT COUNT(d) FROM DeletedMessageEntity d WHERE d.deleteReason = :deleteReason")
    Long countByDeleteReason(@Param("deleteReason") DeletedMessageEntity.DeleteReason deleteReason);

    @Query("SELECT COUNT(d) FROM DeletedMessageEntity d WHERE d.auditStatus = :auditStatus")
    Long countByAuditStatus(@Param("auditStatus") DeletedMessageEntity.AuditStatus auditStatus);

    @Query("SELECT COUNT(d) FROM DeletedMessageEntity d WHERE d.adminVisible = :adminVisible")
    Long countByAdminVisible(@Param("adminVisible") boolean adminVisible);

    @Query("SELECT COUNT(d) FROM DeletedMessageEntity d WHERE d.permanentlyDeleted = :permanentlyDeleted")
    Long countByPermanentlyDeleted(@Param("permanentlyDeleted") boolean permanentlyDeleted);

    // 按时间段统计

    @Query("SELECT COUNT(d) FROM DeletedMessageEntity d WHERE d.deletedAt BETWEEN :start AND :end")
    Long countByDeleteTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT d.deleteReason, COUNT(d) FROM DeletedMessageEntity d WHERE d.deletedAt BETWEEN :start AND :end GROUP BY d.deleteReason")
    List<Object[]> countDeleteReasonsByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT d.deletedByType, COUNT(d) FROM DeletedMessageEntity d WHERE d.deletedAt BETWEEN :start AND :end GROUP BY d.deletedByType")
    List<Object[]> countDeleteTypesByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 批量操作查询

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.permanentlyDeleted = false AND d.retentionDays <= :daysRemaining")
    List<DeletedMessageEntity> findMessagesWithShortRetention(@Param("daysRemaining") Integer daysRemaining);

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.auditStatus = 'APPROVED' AND d.permanentlyDeleted = false")
    List<DeletedMessageEntity> findApprovedNonDeletedMessages();

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.auditStatus = 'REJECTED'")
    List<DeletedMessageEntity> findRejectedMessages();

    // 高级查询

    @Query("SELECT d FROM DeletedMessageEntity d WHERE " +
           "(:senderId IS NULL OR d.senderId = :senderId) AND " +
           "(:receiverId IS NULL OR d.receiverId = :receiverId) AND " +
           "(:deleteReason IS NULL OR d.deleteReason = :deleteReason) AND " +
           "(:auditStatus IS NULL OR d.auditStatus = :auditStatus) AND " +
           "d.deletedAt BETWEEN :startDate AND :endDate")
    List<DeletedMessageEntity> advancedSearch(
            @Param("senderId") String senderId,
            @Param("receiverId") String receiverId,
            @Param("deleteReason") DeletedMessageEntity.DeleteReason deleteReason,
            @Param("auditStatus") DeletedMessageEntity.AuditStatus auditStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 清理查询

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.permanentlyDeleted = false AND d.expireDeleteAt < :currentTime")
    List<DeletedMessageEntity> findMessagesForCleanup(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT d FROM DeletedMessageEntity d WHERE d.permanentlyDeleted = true AND d.permanentDeleteAt < :cutoffTime")
    List<DeletedMessageEntity> findOldPermanentlyDeleted(@Param("cutoffTime") LocalDateTime cutoffTime);

    // 导出查询

    @Query("SELECT d.originalMessageId, d.messageType, d.senderId, d.receiverId, d.receiverType, " +
           "d.deletedAt, d.deleteReason, d.deletedByUserId, d.deletedByType, d.auditStatus " +
           "FROM DeletedMessageEntity d WHERE d.deletedAt BETWEEN :start AND :end")
    List<Object[]> findExportData(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 审计查询

    @Query("SELECT d.originalMessageId, d.deleteReason, d.deletedByUserId, d.deletedByType, " +
           "d.deletedAt, d.auditStatus, d.auditedAt, d.auditedByUserId, d.auditNotes " +
           "FROM DeletedMessageEntity d WHERE d.auditStatus != 'PENDING'")
    List<Object[]> findAuditedRecords();
}