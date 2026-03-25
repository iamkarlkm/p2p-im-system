package com.im.backend.service;

import com.im.backend.entity.DeletedMessageEntity;
import com.im.backend.repository.DeletedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 已删除消息服务
 * 提供已删除消息的管理、审核和清理功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeletedMessageService {

    private final DeletedMessageRepository deletedMessageRepository;

    // ========== 基本CRUD操作 ==========

    /**
     * 记录已删除消息
     */
    @Transactional
    public DeletedMessageEntity recordDeletedMessage(String originalMessageId, String senderId, String receiverId,
                                                    DeletedMessageEntity.ReceiverType receiverType, String deletedByUserId,
                                                    DeletedMessageEntity.DeleteReason deleteReason, String originalContent,
                                                    DeletedMessageEntity.MessageType messageType) {
        
        log.info("Recording deleted message: messageId={}, sender={}, deletedBy={}, reason={}", 
                 originalMessageId, senderId, deletedByUserId, deleteReason);

        DeletedMessageEntity entity = new DeletedMessageEntity(originalMessageId, senderId, receiverId, 
                                                              receiverType, deletedByUserId, deleteReason);
        entity.setOriginalContent(originalContent);
        entity.setMessageType(messageType);
        entity.setContentHash(calculateContentHash(originalContent));
        
        // 设置删除者类型
        if (deleteReason == DeletedMessageEntity.DeleteReason.SYSTEM_ADMIN_DELETE) {
            entity.setDeletedByType(DeletedMessageEntity.DeletedByType.ADMIN);
        } else if (deleteReason == DeletedMessageEntity.DeleteReason.AUTOMATIC_CLEANUP) {
            entity.setDeletedByType(DeletedMessageEntity.DeletedByType.SYSTEM);
        }
        
        // 设置管理员可见性
        entity.setAdminVisible(shouldBeAdminVisible(deleteReason));
        
        // 设置审核状态
        if (entity.needsReview()) {
            entity.setAuditStatus(DeletedMessageEntity.AuditStatus.PENDING);
        } else {
            entity.setAuditStatus(DeletedMessageEntity.AuditStatus.REVIEWED);
        }
        
        // 生成操作日志ID
        entity.setOperationLogId(generateOperationLogId());
        
        DeletedMessageEntity saved = deletedMessageRepository.save(entity);
        log.info("Deleted message recorded with ID: {}", saved.getId());
        
        return saved;
    }

    /**
     * 批量记录已删除消息
     */
    @Transactional
    public List<DeletedMessageEntity> batchRecordDeletedMessages(List<DeletedMessageEntity> messages) {
        log.info("Batch recording {} deleted messages", messages.size());
        
        // 为每条消息设置必要属性
        for (DeletedMessageEntity message : messages) {
            if (message.getDeletedAt() == null) {
                message.setDeletedAt(LocalDateTime.now());
            }
            if (message.getContentHash() == null && message.getOriginalContent() != null) {
                message.setContentHash(calculateContentHash(message.getOriginalContent()));
            }
            if (message.getOperationLogId() == null) {
                message.setOperationLogId(generateOperationLogId());
            }
            message.setExpireDeleteAt(LocalDateTime.now().plusDays(message.getRetentionDays()));
        }
        
        return deletedMessageRepository.saveAll(messages);
    }

    /**
     * 根据原始消息ID查找已删除记录
     */
    public Optional<DeletedMessageEntity> findByOriginalMessageId(String originalMessageId) {
        return deletedMessageRepository.findByOriginalMessageId(originalMessageId);
    }

    /**
     * 获取发送者已删除的消息
     */
    public List<DeletedMessageEntity> getMessagesBySender(String senderId) {
        return deletedMessageRepository.findBySenderId(senderId);
    }

    public Page<DeletedMessageEntity> getMessagesBySender(String senderId, Pageable pageable) {
        return deletedMessageRepository.findBySenderId(senderId, pageable);
    }

    /**
     * 获取接收者已删除的消息
     */
    public List<DeletedMessageEntity> getMessagesByReceiver(String receiverId) {
        return deletedMessageRepository.findByReceiverId(receiverId);
    }

    public Page<DeletedMessageEntity> getMessagesByReceiver(String receiverId, Pageable pageable) {
        return deletedMessageRepository.findByReceiverId(receiverId, pageable);
    }

    /**
     * 获取删除者操作的消息
     */
    public List<DeletedMessageEntity> getMessagesByDeleter(String deletedByUserId) {
        return deletedMessageRepository.findByDeletedByUserId(deletedByUserId);
    }

    public Page<DeletedMessageEntity> getMessagesByDeleter(String deletedByUserId, Pageable pageable) {
        return deletedMessageRepository.findByDeletedByUserId(deletedByUserId, pageable);
    }

    // ========== 审核管理 ==========

    /**
     * 获取待审核消息
     */
    public List<DeletedMessageEntity> getPendingReviewMessages() {
        return deletedMessageRepository.findPendingAdminReviewMessages();
    }

    /**
     * 获取需要审核的消息
     */
    public List<DeletedMessageEntity> getMessagesNeedingReview() {
        return deletedMessageRepository.findMessagesNeedingReview();
    }

    /**
     * 审核消息
     */
    @Transactional
    public DeletedMessageEntity auditMessage(Long messageId, DeletedMessageEntity.AuditStatus status, 
                                            String notes, String auditorId) {
        
        log.info("Auditing deleted message: id={}, status={}, auditor={}", messageId, status, auditorId);
        
        return deletedMessageRepository.findById(messageId)
                .map(entity -> {
                    entity.markAsAudited(status, notes, auditorId);
                    
                    // 如果审核拒绝，可能取消管理员可见
                    if (status == DeletedMessageEntity.AuditStatus.REJECTED) {
                        entity.setAdminVisible(false);
                    }
                    
                    return deletedMessageRepository.save(entity);
                })
                .orElseThrow(() -> new IllegalArgumentException("Deleted message not found: " + messageId));
    }

    /**
     * 批量审核消息
     */
    @Transactional
    public List<DeletedMessageEntity> batchAuditMessages(List<Long> messageIds, DeletedMessageEntity.AuditStatus status,
                                                        String notes, String auditorId) {
        
        log.info("Batch auditing {} messages with status: {}", messageIds.size(), status);
        
        return messageIds.stream()
                .map(id -> {
                    try {
                        return auditMessage(id, status, notes, auditorId);
                    } catch (Exception e) {
                        log.warn("Failed to audit message {}: {}", id, e.getMessage());
                        return null;
                    }
                })
                .filter(entity -> entity != null)
                .toList();
    }

    /**
     * 获取审核统计
     */
    public long countByAuditStatus(DeletedMessageEntity.AuditStatus status) {
        return deletedMessageRepository.countByAuditStatus(status);
    }

    // ========== 删除原因管理 ==========

    /**
     * 按删除原因获取消息
     */
    public List<DeletedMessageEntity> getMessagesByDeleteReason(DeletedMessageEntity.DeleteReason reason) {
        return deletedMessageRepository.findByDeleteReason(reason);
    }

    public Page<DeletedMessageEntity> getMessagesByDeleteReason(DeletedMessageEntity.DeleteReason reason, Pageable pageable) {
        return deletedMessageRepository.findByDeleteReason(reason, pageable);
    }

    /**
     * 获取删除原因统计
     */
    public long countByDeleteReason(DeletedMessageEntity.DeleteReason reason) {
        return deletedMessageRepository.countByDeleteReason(reason);
    }

    // ========== 清理操作 ==========

    /**
     * 标记为彻底删除
     */
    @Transactional
    public DeletedMessageEntity markAsPermanentlyDeleted(Long messageId) {
        log.info("Marking message as permanently deleted: id={}", messageId);
        
        return deletedMessageRepository.findById(messageId)
                .map(entity -> {
                    entity.markAsPermanentlyDeleted();
                    return deletedMessageRepository.save(entity);
                })
                .orElseThrow(() -> new IllegalArgumentException("Deleted message not found: " + messageId));
    }

    /**
     * 批量标记彻底删除
     */
    @Transactional
    public List<DeletedMessageEntity> batchMarkAsPermanentlyDeleted(List<Long> messageIds) {
        log.info("Batch marking {} messages as permanently deleted", messageIds.size());
        
        return messageIds.stream()
                .map(id -> {
                    try {
                        return markAsPermanentlyDeleted(id);
                    } catch (Exception e) {
                        log.warn("Failed to mark message {} as permanently deleted: {}", id, e.getMessage());
                        return null;
                    }
                })
                .filter(entity -> entity != null)
                .toList();
    }

    /**
     * 清理过期消息
     */
    @Transactional
    public int cleanupExpiredMessages() {
        log.info("Starting expired message cleanup");
        
        LocalDateTime now = LocalDateTime.now();
        List<DeletedMessageEntity> expiredMessages = deletedMessageRepository.findExpiredMessages(now);
        
        if (expiredMessages.isEmpty()) {
            log.info("No expired messages found");
            return 0;
        }
        
        // 标记为彻底删除
        expiredMessages.forEach(entity -> {
            entity.markAsPermanentlyDeleted();
            entity.setDeleteReason(DeletedMessageEntity.DeleteReason.AUTOMATIC_CLEANUP);
        });
        
        deletedMessageRepository.saveAll(expiredMessages);
        log.info("Marked {} expired messages as permanently deleted", expiredMessages.size());
        
        return expiredMessages.size();
    }

    /**
     * 清理旧的彻底删除记录
     */
    @Transactional
    public int cleanupOldPermanentlyDeleted() {
        log.info("Starting cleanup of old permanently deleted records");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7); // 保留7天
        List<DeletedMessageEntity> oldRecords = deletedMessageRepository.findOldPermanentlyDeleted(cutoffTime);
        
        if (oldRecords.isEmpty()) {
            log.info("No old permanently deleted records found");
            return 0;
        }
        
        deletedMessageRepository.deleteAll(oldRecords);
        log.info("Deleted {} old permanently deleted records", oldRecords.size());
        
        return oldRecords.size();
    }

    // ========== 统计与分析 ==========

    /**
     * 获取删除统计
     */
    public long countBySender(String senderId) {
        return deletedMessageRepository.countBySender(senderId);
    }

    public long countByReceiver(String receiverId) {
        return deletedMessageRepository.countByReceiver(receiverId);
    }

    public long countByDeleter(String deletedByUserId) {
        return deletedMessageRepository.countByDeleter(deletedByUserId);
    }

    /**
     * 获取时间范围统计
     */
    public long countByDeleteTimeRange(LocalDateTime start, LocalDateTime end) {
        return deletedMessageRepository.countByDeleteTimeRange(start, end);
    }

    /**
     * 获取删除原因分布
     */
    public List<Object[]> getDeleteReasonDistribution(LocalDateTime start, LocalDateTime end) {
        return deletedMessageRepository.countDeleteReasonsByTimeRange(start, end);
    }

    /**
     * 获取删除者类型分布
     */
    public List<Object[]> getDeleteTypeDistribution(LocalDateTime start, LocalDateTime end) {
        return deletedMessageRepository.countDeleteTypesByTimeRange(start, end);
    }

    // ========== 高级查询 ==========

    /**
     * 高级搜索
     */
    public List<DeletedMessageEntity> advancedSearch(String senderId, String receiverId,
                                                    DeletedMessageEntity.DeleteReason deleteReason,
                                                    DeletedMessageEntity.AuditStatus auditStatus,
                                                    LocalDateTime startDate, LocalDateTime endDate) {
        
        return deletedMessageRepository.advancedSearch(senderId, receiverId, deleteReason, 
                                                      auditStatus, startDate, endDate);
    }

    /**
     * 导出数据
     */
    public List<Object[]> getExportData(LocalDateTime start, LocalDateTime end) {
        return deletedMessageRepository.findExportData(start, end);
    }

    /**
     * 获取审计记录
     */
    public List<Object[]> getAuditedRecords() {
        return deletedMessageRepository.findAuditedRecords();
    }

    // ========== 辅助方法 ==========

    private String calculateContentHash(String content) {
        if (content == null) return null;
        // 简单实现，实际应使用安全的哈希算法
        return Integer.toHexString(content.hashCode());
    }

    private boolean shouldBeAdminVisible(DeletedMessageEntity.DeleteReason reason) {
        return reason == DeletedMessageEntity.DeleteReason.CONTENT_VIOLATION ||
               reason == DeletedMessageEntity.DeleteReason.USER_REPORTED ||
               reason == DeletedMessageEntity.DeleteReason.LEGAL_REQUEST ||
               reason == DeletedMessageEntity.DeleteReason.GROUP_ADMIN_DELETE ||
               reason == DeletedMessageEntity.DeleteReason.SYSTEM_ADMIN_DELETE;
    }

    private String generateOperationLogId() {
        return "DEL_LOG_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 获取清理候选消息
     */
    public List<DeletedMessageEntity> getCleanupCandidates() {
        return deletedMessageRepository.findMessagesForCleanup(LocalDateTime.now());
    }

    /**
     * 获取短期保留消息
     */
    public List<DeletedMessageEntity> getMessagesWithShortRetention(int daysRemaining) {
        return deletedMessageRepository.findMessagesWithShortRetention(daysRemaining);
    }

    /**
     * 检查消息是否存在
     */
    public boolean existsByOriginalMessageId(String originalMessageId) {
        return deletedMessageRepository.findByOriginalMessageId(originalMessageId).isPresent();
    }
}