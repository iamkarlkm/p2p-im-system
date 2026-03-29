package com.im.service;

import com.im.entity.ViewOnceMessageEntity;
import com.im.repository.ViewOnceMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 一次性媒体消息服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewOnceMessageService {
    
    private final ViewOnceMessageRepository repository;
    
    /**
     * 创建一次性媒体消息
     */
    @Transactional
    public ViewOnceMessageEntity createViewOnceMessage(
            String messageId,
            String conversationId,
            String senderId,
            String receiverId,
            String mediaType,
            String mediaUrl,
            Long mediaSize,
            String mimeType,
            String encryptionKey,
            LocalDateTime expireAt,
            Boolean screenshotDetection,
            String metadata) {
        
        ViewOnceMessageEntity entity = ViewOnceMessageEntity.builder()
                .messageId(messageId)
                .conversationId(conversationId)
                .senderId(senderId)
                .receiverId(receiverId)
                .mediaType(mediaType)
                .mediaUrl(mediaUrl)
                .mediaSize(mediaSize)
                .mimeType(mimeType)
                .encryptionKey(encryptionKey)
                .expireAt(expireAt)
                .screenshotDetection(screenshotDetection != null ? screenshotDetection : false)
                .metadata(metadata)
                .viewed(false)
                .destroyed(false)
                .active(true)
                .build();
        
        return repository.save(entity);
    }
    
    /**
     * 获取一次性媒体消息（验证接收者权限）
     */
    public Optional<ViewOnceMessageEntity> getViewOnceMessage(String messageId, String receiverId) {
        return repository.findByMessageIdAndReceiverId(messageId, receiverId);
    }
    
    /**
     * 标记消息为已查看
     */
    @Transactional
    public boolean markAsViewed(String messageId, String receiverId, String ip, String deviceId) {
        Optional<ViewOnceMessageEntity> optEntity = repository.findByMessageIdAndReceiverId(messageId, receiverId);
        
        if (optEntity.isEmpty()) {
            log.warn("ViewOnce message not found or not authorized: messageId={}, receiverId={}", messageId, receiverId);
            return false;
        }
        
        ViewOnceMessageEntity entity = optEntity.get();
        
        // 检查是否已经查看过
        if (entity.getViewed()) {
            log.info("ViewOnce message already viewed: messageId={}", messageId);
            return false;
        }
        
        // 检查是否已销毁
        if (entity.getDestroyed()) {
            log.warn("ViewOnce message already destroyed: messageId={}", messageId);
            return false;
        }
        
        // 标记为已查看
        entity.markAsViewed(ip, deviceId);
        repository.save(entity);
        
        log.info("ViewOnce message marked as viewed: messageId={}, receiverId={}", messageId, receiverId);
        
        // 触发销毁流程（可选：查看后立即销毁，或保留一段时间后销毁）
        // scheduleDestruction(entity);
        
        return true;
    }
    
    /**
     * 销毁一次性媒体消息
     */
    @Transactional
    public boolean destroyMessage(String messageId, String reason) {
        Optional<ViewOnceMessageEntity> optEntity = repository.findByMessageId(messageId);
        
        if (optEntity.isEmpty()) {
            return false;
        }
        
        ViewOnceMessageEntity entity = optEntity.get();
        
        // 如果已经销毁，跳过
        if (entity.getDestroyed()) {
            log.info("ViewOnce message already destroyed: messageId={}", messageId);
            return false;
        }
        
        entity.markAsDestroyed(reason);
        repository.save(entity);
        
        log.info("ViewOnce message destroyed: messageId={}, reason={}", messageId, reason);
        
        // TODO: 调用文件存储服务删除实际媒体文件
        // fileStorageService.delete(entity.getMediaUrl());
        
        return true;
    }
    
    /**
     * 批量销毁消息
     */
    @Transactional
    public int destroyMessages(List<String> messageIds, String reason) {
        int count = repository.markAsDestroyed(messageIds, LocalDateTime.now(), reason);
        log.info("Batch destroyed {} ViewOnce messages, reason={}", count, reason);
        return count;
    }
    
    /**
     * 记录截图
     */
    @Transactional
    public boolean recordScreenshot(String messageId, String timestamp, String details) {
        Optional<ViewOnceMessageEntity> optEntity = repository.findByMessageId(messageId);
        
        if (optEntity.isEmpty()) {
            return false;
        }
        
        ViewOnceMessageEntity entity = optEntity.get();
        
        // 检查是否启用了截图检测
        if (!entity.getScreenshotDetection()) {
            log.debug("Screenshot detection not enabled for message: {}", messageId);
            return false;
        }
        
        entity.addScreenshotRecord(timestamp, details);
        repository.save(entity);
        
        log.info("Screenshot recorded for ViewOnce message: messageId={}", messageId);
        
        // 如果启用了截图后销毁，可以在这里触发
        // destroyMessage(messageId, "SCREENSHOT_DETECTED");
        
        return true;
    }
    
    /**
     * 获取会话的所有一次性媒体消息
     */
    public List<ViewOnceMessageEntity> getConversationViewOnceMessages(String conversationId) {
        return repository.findByConversationIdAndActiveTrueOrderByCreatedAtDesc(conversationId);
    }
    
    /**
     * 获取用户收到的一次性媒体消息列表
     */
    public List<ViewOnceMessageEntity> getUserViewOnceMessages(String receiverId) {
        return repository.findByReceiverIdAndActiveTrueOrderByCreatedAtDesc(receiverId);
    }
    
    /**
     * 获取用户未查看的一次性媒体消息
     */
    public List<ViewOnceMessageEntity> getUnviewedMessages(String receiverId) {
        return repository.findByReceiverIdAndViewedFalseAndActiveTrueOrderByCreatedAtDesc(receiverId);
    }
    
    /**
     * 获取用户的一次性媒体统计
     */
    public ViewOnceStats getUserStats(String userId) {
        long totalReceived = repository.countByReceiverIdAndActiveTrue(userId);
        long totalViewed = repository.countByReceiverIdAndViewedTrueAndActiveTrue(userId);
        
        return new ViewOnceStats(userId, totalReceived, totalViewed, totalReceived - totalViewed);
    }
    
    /**
     * 获取会话的一次性媒体统计
     */
    public ConversationViewOnceStats getConversationStats(String conversationId) {
        long total = repository.countByConversationIdAndActiveTrue(conversationId);
        long unviewed = repository.countByConversationIdAndViewedFalseAndActiveTrue(conversationId);
        
        return new ConversationViewOnceStats(conversationId, total, unviewed);
    }
    
    /**
     * 处理过期的消息
     */
    @Transactional
    public int processExpiredMessages() {
        List<ViewOnceMessageEntity> expiredMessages = repository.findByExpireAtBeforeAndActiveTrue(LocalDateTime.now());
        
        int count = 0;
        for (ViewOnceMessageEntity entity : expiredMessages) {
            if (destroyMessage(entity.getMessageId(), "EXPIRED")) {
                count++;
            }
        }
        
        log.info("Processed {} expired ViewOnce messages", count);
        return count;
    }
    
    /**
     * 清理已销毁的消息记录
     */
    @Transactional
    public int cleanupDestroyedMessages(int retentionDays) {
        LocalDateTime before = LocalDateTime.now().minusDays(retentionDays);
        int count = repository.deleteDestroyedMessagesBefore(before);
        log.info("Cleaned up {} destroyed ViewOnce message records older than {} days", count, retentionDays);
        return count;
    }
    
    /**
     * 检查消息是否已被查看
     */
    public boolean isMessageViewed(String messageId) {
        return repository.existsByMessageIdAndViewedTrue(messageId);
    }
    
    /**
     * 检查消息是否激活
     */
    public boolean isMessageActive(String messageId) {
        return repository.existsByMessageIdAndActiveTrue(messageId);
    }
    
    // 统计内部类
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ViewOnceStats {
        private String userId;
        private long totalReceived;
        private long totalViewed;
        private long totalUnviewed;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ConversationViewOnceStats {
        private String conversationId;
        private long totalMessages;
        private long unviewedCount;
    }
}
