package com.im.system.service;

import com.im.system.entity.MessageEditEntity;
import com.im.system.repository.MessageEditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 消息编辑服务 - 处理消息编辑和版本管理的业务逻辑
 */
@Service
public class MessageEditService {
    
    @Autowired
    private MessageEditRepository messageEditRepository;
    
    /**
     * 创建新的消息编辑记录
     */
    @Transactional
    public MessageEditEntity createMessageEdit(MessageEditEntity messageEdit) {
        // Validate input
        if (messageEdit.getMessageId() == null || messageEdit.getUserId() == null) {
            throw new IllegalArgumentException("Message ID and User ID are required");
        }
        
        // Set default values
        if (messageEdit.getVersion() == null) {
            // Get next version number
            Integer maxVersion = messageEditRepository.findMaxVersionByMessageId(messageEdit.getMessageId())
                    .orElse(0);
            messageEdit.setVersion(maxVersion + 1);
        }
        
        if (messageEdit.getIsLatest() == null) {
            messageEdit.setIsLatest(true);
        }
        
        if (messageEdit.getEditType() == null) {
            messageEdit.setEditType(messageEdit.getVersion() == 1 ? "CREATE" : "EDIT");
        }
        
        if (messageEdit.getStatus() == null) {
            messageEdit.setStatus("ACTIVE");
        }
        
        if (messageEdit.getPrivacyLevel() == null) {
            messageEdit.setPrivacyLevel("STANDARD");
        }
        
        if (messageEdit.getSyncStatus() == null) {
            messageEdit.setSyncStatus("SYNCED");
        }
        
        // If this is the latest version, mark previous versions as not latest
        if (Boolean.TRUE.equals(messageEdit.getIsLatest())) {
            messageEditRepository.markAllAsNotLatestByMessageId(messageEdit.getMessageId());
        }
        
        // Calculate content hash if not provided
        if (messageEdit.getContentHash() == null && messageEdit.getContent() != null) {
            messageEdit.setContentHash(calculateContentHash(messageEdit.getContent()));
        }
        
        // Save the edit
        return messageEditRepository.save(messageEdit);
    }
    
    /**
     * 获取消息的所有编辑版本
     */
    public List<MessageEditEntity> getMessageEdits(UUID messageId) {
        return messageEditRepository.findByMessageIdOrderByVersionDesc(messageId);
    }
    
    /**
     * 获取消息的特定版本
     */
    public Optional<MessageEditEntity> getMessageEditByVersion(UUID messageId, Integer version) {
        return messageEditRepository.findByMessageIdAndVersion(messageId, version);
    }
    
    /**
     * 获取消息的最新版本
     */
    public Optional<MessageEditEntity> getLatestMessageEdit(UUID messageId) {
        return messageEditRepository.findByMessageIdAndIsLatestTrue(messageId);
    }
    
    /**
     * 更新消息编辑记录
     */
    @Transactional
    public MessageEditEntity updateMessageEdit(UUID editId, MessageEditEntity updatedEdit) {
        Optional<MessageEditEntity> existingOpt = messageEditRepository.findById(editId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Message edit not found: " + editId);
        }
        
        MessageEditEntity existing = existingOpt.get();
        
        // Update fields if provided
        if (updatedEdit.getContent() != null) {
            existing.setContent(updatedEdit.getContent());
            existing.setContentHash(calculateContentHash(updatedEdit.getContent()));
        }
        
        if (updatedEdit.getEditReason() != null) {
            existing.setEditReason(updatedEdit.getEditReason());
        }
        
        if (updatedEdit.getMetadata() != null) {
            existing.setMetadata(updatedEdit.getMetadata());
        }
        
        if (updatedEdit.getStatus() != null) {
            existing.setStatus(updatedEdit.getStatus());
        }
        
        if (updatedEdit.getAuditStatus() != null) {
            existing.setAuditStatus(updatedEdit.getAuditStatus());
        }
        
        if (updatedEdit.getAuditNotes() != null) {
            existing.setAuditNotes(updatedEdit.getAuditNotes());
        }
        
        if (updatedEdit.getAuditorId() != null) {
            existing.setAuditorId(updatedEdit.getAuditorId());
        }
        
        if (updatedEdit.getAuditTimestamp() != null) {
            existing.setAuditTimestamp(updatedEdit.getAuditTimestamp());
        }
        
        if (updatedEdit.getSyncStatus() != null) {
            existing.setSyncStatus(updatedEdit.getSyncStatus());
        }
        
        if (updatedEdit.getConflictResolution() != null) {
            existing.setConflictResolution(updatedEdit.getConflictResolution());
        }
        
        if (updatedEdit.getConflictDetails() != null) {
            existing.setConflictDetails(updatedEdit.getConflictDetails());
        }
        
        if (updatedEdit.getTags() != null) {
            existing.setTags(updatedEdit.getTags());
        }
        
        if (updatedEdit.getCustomFields() != null) {
            existing.setCustomFields(updatedEdit.getCustomFields());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        return messageEditRepository.save(existing);
    }
    
    /**
     * 删除消息编辑记录（软删除）
     */
    @Transactional
    public void deleteMessageEdit(UUID editId) {
        Optional<MessageEditEntity> existingOpt = messageEditRepository.findById(editId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Message edit not found: " + editId);
        }
        
        MessageEditEntity existing = existingOpt.get();
        existing.setStatus("DELETED");
        existing.setDeletedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());
        
        // If this was the latest version, find the next latest
        if (Boolean.TRUE.equals(existing.getIsLatest())) {
            // Find the next active version and mark it as latest
            List<MessageEditEntity> otherVersions = messageEditRepository
                    .findByMessageIdAndStatusOrderByVersionDesc(existing.getMessageId(), "ACTIVE");
            
            for (MessageEditEntity version : otherVersions) {
                if (!version.getId().equals(editId)) {
                    version.setIsLatest(true);
                    messageEditRepository.save(version);
                    break;
                }
            }
        }
        
        messageEditRepository.save(existing);
    }
    
    /**
     * 恢复已删除的消息编辑记录
     */
    @Transactional
    public void restoreMessageEdit(UUID editId) {
        Optional<MessageEditEntity> existingOpt = messageEditRepository.findById(editId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Message edit not found: " + editId);
        }
        
        MessageEditEntity existing = existingOpt.get();
        existing.setStatus("ACTIVE");
        existing.setDeletedAt(null);
        existing.setUpdatedAt(LocalDateTime.now());
        
        // Mark as latest if no other active version is latest
        Optional<MessageEditEntity> latestOpt = messageEditRepository
                .findByMessageIdAndIsLatestTrue(existing.getMessageId());
        
        if (latestOpt.isEmpty() || !"ACTIVE".equals(latestOpt.get().getStatus())) {
            existing.setIsLatest(true);
            messageEditRepository.markAllAsNotLatestByMessageId(existing.getMessageId());
        }
        
        messageEditRepository.save(existing);
    }
    
    /**
     * 审核消息编辑
     */
    @Transactional
    public MessageEditEntity auditMessageEdit(UUID editId, String auditStatus, UUID auditorId, String auditNotes) {
        Optional<MessageEditEntity> existingOpt = messageEditRepository.findById(editId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Message edit not found: " + editId);
        }
        
        MessageEditEntity existing = existingOpt.get();
        existing.setAuditStatus(auditStatus);
        existing.setAuditorId(auditorId);
        existing.setAuditNotes(auditNotes);
        existing.setAuditTimestamp(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());
        
        return messageEditRepository.save(existing);
    }
    
    /**
     * 批量审核消息编辑
     */
    @Transactional
    public int batchAuditMessageEdits(List<UUID> editIds, String auditStatus, UUID auditorId, String auditNotes) {
        return messageEditRepository.updateAuditStatusByIds(editIds, auditStatus, auditorId, auditNotes, LocalDateTime.now());
    }
    
    /**
     * 搜索消息编辑记录
     */
    public List<MessageEditEntity> searchMessageEdits(UUID messageId, UUID userId, UUID conversationId,
                                                     String editType, String status, String auditStatus,
                                                     String platform, LocalDateTime startDate,
                                                     LocalDateTime endDate) {
        return messageEditRepository.searchEdits(messageId, userId, conversationId, editType, status,
                                                auditStatus, platform, startDate, endDate);
    }
    
    /**
     * 获取用户的编辑统计
     */
    public Long getUserEditCount(UUID userId) {
        return messageEditRepository.countActiveEditsByUserId(userId);
    }
    
    /**
     * 获取会话的编辑统计
     */
    public Long getConversationEditCount(UUID conversationId) {
        return messageEditRepository.countActiveEditsByConversationId(conversationId);
    }
    
    /**
     * 归档旧的消息编辑记录
     */
    @Transactional
    public int archiveOldEdits(LocalDateTime before) {
        return messageEditRepository.archiveOldEdits(before, LocalDateTime.now());
    }
    
    /**
     * 删除过期的消息编辑记录
     */
    @Transactional
    public int deleteExpiredEdits(LocalDateTime now) {
        return messageEditRepository.deleteExpiredEdits(now, LocalDateTime.now());
    }
    
    /**
     * 永久删除已标记为删除的记录
     */
    @Transactional
    public int permanentlyDeleteOldDeletedEdits(LocalDateTime before) {
        return messageEditRepository.permanentlyDeleteOldDeletedEdits(before);
    }
    
    /**
     * 删除过期的版本
     */
    @Transactional
    public int deleteExpiredVersions(LocalDateTime now) {
        return messageEditRepository.deleteExpiredVersions(now);
    }
    
    /**
     * 获取编辑统计报表
     */
    public List<Object[]> getEditStatisticsByEditType() {
        return messageEditRepository.countByEditType();
    }
    
    /**
     * 获取平台统计报表
     */
    public List<Object[]> getEditStatisticsByPlatform() {
        return messageEditRepository.countByPlatform();
    }
    
    /**
     * 获取审核状态统计报表
     */
    public List<Object[]> getEditStatisticsByAuditStatus() {
        return messageEditRepository.countByAuditStatus();
    }
    
    /**
     * 获取时间段内的编辑统计
     */
    public List<Object[]> getEditStatisticsByUserInTimeRange(LocalDateTime start, LocalDateTime end) {
        return messageEditRepository.countEditsByUserInTimeRange(start, end);
    }
    
    /**
     * 获取时间段内的平台统计
     */
    public List<Object[]> getEditStatisticsByPlatformInTimeRange(LocalDateTime start, LocalDateTime end) {
        return messageEditRepository.countEditsByPlatformInTimeRange(start, end);
    }
    
    /**
     * 计算内容哈希值
     */
    private String calculateContentHash(String content) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // Fallback to simple hash
            return Integer.toHexString(content.hashCode());
        }
    }
    
    /**
     * 检查编辑是否与原始内容有差异
     */
    public boolean hasContentChanged(String originalContent, String newContent) {
        if (originalContent == null && newContent == null) return false;
        if (originalContent == null || newContent == null) return true;
        return !originalContent.equals(newContent);
    }
    
    /**
     * 计算编辑差异
     */
    public String calculateEditDiff(String originalContent, String newContent) {
        if (!hasContentChanged(originalContent, newContent)) {
            return null;
        }
        
        // Simple diff calculation (in production, use a proper diff library)
        try {
            // JSON diff structure
            return String.format("{\"originalLength\": %d, \"newLength\": %d, \"delta\": %d, \"changed\": true}",
                    originalContent != null ? originalContent.length() : 0,
                    newContent != null ? newContent.length() : 0,
                    newContent.length() - (originalContent != null ? originalContent.length() : 0));
        } catch (Exception e) {
            return "{\"error\": \"Failed to calculate diff\"}";
        }
    }
    
    /**
     * 验证编辑权限
     */
    public boolean canEditMessage(UUID userId, UUID messageId, MessageEditEntity existingEdit) {
        // Check if user owns the message
        if (existingEdit != null && existingEdit.getUserId().equals(userId)) {
            return true;
        }
        
        // Check admin permissions (implement based on your role system)
        // For now, return false for non-owners
        return false;
    }
    
    /**
     * 处理编辑冲突
     */
    @Transactional
    public MessageEditEntity resolveEditConflict(UUID editId, String conflictResolution, String conflictDetails) {
        Optional<MessageEditEntity> existingOpt = messageEditRepository.findById(editId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Message edit not found: " + editId);
        }
        
        MessageEditEntity existing = existingOpt.get();
        existing.setConflictResolution(conflictResolution);
        existing.setConflictDetails(conflictDetails);
        existing.setSyncStatus("RESOLVED");
        existing.setUpdatedAt(LocalDateTime.now());
        
        return messageEditRepository.save(existing);
    }
    
    /**
     * 同步编辑状态
     */
    @Transactional
    public int syncPendingEdits(String newSyncStatus, LocalDateTime before) {
        return messageEditRepository.updateSyncStatusByTime("PENDING", newSyncStatus, before);
    }
    
    /**
     * 获取需要同步的编辑记录
     */
    public List<MessageEditEntity> getPendingSyncEdits(LocalDateTime before) {
        return messageEditRepository.findBySyncStatusAndUpdatedAtBefore("PENDING", before);
    }
}