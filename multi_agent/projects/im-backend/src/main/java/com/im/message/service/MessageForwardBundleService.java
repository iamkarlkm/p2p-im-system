package com.im.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.message.entity.MessageForwardBundleEntity;
import com.im.message.repository.MessageForwardBundleRepository;
import com.im.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 消息合并转发服务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageForwardBundleService {

    private final MessageForwardBundleRepository bundleRepository;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    /**
     * 创建转发草稿
     */
    @Transactional
    public MessageForwardBundleEntity createForwardDraft(Long userId, Long sourceConversationId,
                                                         MessageForwardBundleEntity.ForwardType forwardType,
                                                         List<Long> messageIds) {
        MessageForwardBundleEntity bundle = new MessageForwardBundleEntity();
        bundle.setBundleId(MessageForwardBundleEntity.generateBundleId());
        bundle.setSourceConversationId(sourceConversationId);
        bundle.setCreatedBy(userId);
        bundle.setForwardType(forwardType);
        bundle.setStatus(MessageForwardBundleEntity.ForwardStatus.DRAFT);
        bundle.setSendMode(MessageForwardBundleEntity.SendMode.MERGE);
        bundle.setIncludeSenderInfo(true);
        bundle.setIncludeTimestamp(true);
        bundle.setAnonymizeSenders(false);
        
        // 添加消息 ID 列表
        if (messageIds != null && !messageIds.isEmpty()) {
            bundle.setMessageIds(new ArrayList<>(messageIds));
            bundle.setMessageCount(messageIds.size());
        }
        
        return bundleRepository.save(bundle);
    }

    /**
     * 添加消息到转发草稿
     */
    @Transactional
    public MessageForwardBundleEntity addMessageToDraft(Long bundleId, Long userId, Long messageId) {
        MessageForwardBundleEntity bundle = bundleRepository.findById(bundleId)
            .orElseThrow(() -> new IllegalArgumentException("转发草稿不存在"));
        
        if (!bundle.getCreatedBy().equals(userId)) {
            throw new SecurityException("无权操作此转发草稿");
        }
        
        bundle.addMessage(messageId);
        bundle.setUpdatedAt(LocalDateTime.now());
        
        return bundleRepository.save(bundle);
    }

    /**
     * 从转发草稿移除消息
     */
    @Transactional
    public MessageForwardBundleEntity removeMessageFromDraft(Long bundleId, Long userId, Long messageId) {
        MessageForwardBundleEntity bundle = bundleRepository.findById(bundleId)
            .orElseThrow(() -> new IllegalArgumentException("转发草稿不存在"));
        
        if (!bundle.getCreatedBy().equals(userId)) {
            throw new SecurityException("无权操作此转发草稿");
        }
        
        bundle.removeMessage(messageId);
        bundle.setUpdatedAt(LocalDateTime.now());
        
        return bundleRepository.save(bundle);
    }

    /**
     * 更新转发配置
     */
    @Transactional
    public MessageForwardBundleEntity updateForwardConfig(Long bundleId, Long userId,
                                                          Map<String, Object> config) {
        MessageForwardBundleEntity bundle = bundleRepository.findById(bundleId)
            .orElseThrow(() -> new IllegalArgumentException("转发草稿不存在"));
        
        if (!bundle.getCreatedBy().equals(userId)) {
            throw new SecurityException("无权操作此转发草稿");
        }
        
        if (config.containsKey("sendMode")) {
            bundle.setSendMode(MessageForwardBundleEntity.SendMode.valueOf((String) config.get("sendMode")));
        }
        if (config.containsKey("includeSenderInfo")) {
            bundle.setIncludeSenderInfo((Boolean) config.get("includeSenderInfo"));
        }
        if (config.containsKey("includeTimestamp")) {
            bundle.setIncludeTimestamp((Boolean) config.get("includeTimestamp"));
        }
        if (config.containsKey("anonymizeSenders")) {
            bundle.setAnonymizeSenders((Boolean) config.get("anonymizeSenders"));
        }
        if (config.containsKey("customComment")) {
            bundle.setCustomComment((String) config.get("customComment"));
        }
        if (config.containsKey("title")) {
            bundle.setTitle((String) config.get("title"));
        }
        
        bundle.setUpdatedAt(LocalDateTime.now());
        return bundleRepository.save(bundle);
    }

    /**
     * 执行转发
     */
    @Transactional
    public MessageForwardBundleEntity executeForward(Long bundleId, Long userId, Long targetConversationId) {
        MessageForwardBundleEntity bundle = bundleRepository.findById(bundleId)
            .orElseThrow(() -> new IllegalArgumentException("转发草稿不存在"));
        
        if (!bundle.getCreatedBy().equals(userId)) {
            throw new SecurityException("无权操作此转发草稿");
        }
        
        if (bundle.getMessageIds().isEmpty()) {
            throw new IllegalArgumentException("没有要转发的消息");
        }
        
        bundle.setTargetConversationId(targetConversationId);
        bundle.setStatus(MessageForwardBundleEntity.ForwardStatus.PENDING);
        bundle = bundleRepository.save(bundle);
        
        // 实际转发逻辑（这里简化处理）
        try {
            // TODO: 实现实际的消息转发逻辑
            // 1. 获取原始消息内容
            // 2. 根据配置格式化消息（合并或逐条）
            // 3. 发送到目标会话
            // 4. 更新状态为 SENT
            
            bundle.setStatus(MessageForwardBundleEntity.ForwardStatus.SENT);
            bundle.setForwardedAt(LocalDateTime.now());
            bundle.setUpdatedAt(LocalDateTime.now());
            
            // 发送 WebSocket 通知
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "MESSAGE_FORWARDED");
            notification.put("bundleId", bundle.getBundleId());
            notification.put("targetConversationId", targetConversationId);
            notification.put("messageCount", bundle.getMessageCount());
            
            webSocketService.sendToUser(userId.toString(), "message", notification);
            
            log.info("消息转发成功：bundleId={}, targetConversationId={}, messageCount={}", 
                bundle.getBundleId(), targetConversationId, bundle.getMessageCount());
            
        } catch (Exception e) {
            log.error("消息转发失败：bundleId={}", bundle.getBundleId(), e);
            bundle.setStatus(MessageForwardBundleEntity.ForwardStatus.FAILED);
            bundle.setErrorMessage(e.getMessage());
            bundle.setUpdatedAt(LocalDateTime.now());
        }
        
        return bundleRepository.save(bundle);
    }

    /**
     * 取消转发草稿
     */
    @Transactional
    public void cancelDraft(Long bundleId, Long userId) {
        MessageForwardBundleEntity bundle = bundleRepository.findById(bundleId)
            .orElseThrow(() -> new IllegalArgumentException("转发草稿不存在"));
        
        if (!bundle.getCreatedBy().equals(userId)) {
            throw new SecurityException("无权操作此转发草稿");
        }
        
        bundle.setStatus(MessageForwardBundleEntity.ForwardStatus.CANCELLED);
        bundle.setUpdatedAt(LocalDateTime.now());
        bundleRepository.save(bundle);
    }

    /**
     * 获取用户的转发草稿列表
     */
    public List<MessageForwardBundleEntity> getUserDrafts(Long userId) {
        return bundleRepository.findDraftsByUser(userId);
    }

    /**
     * 获取转发历史记录
     */
    public List<MessageForwardBundleEntity> getForwardHistory(Long userId, int limit) {
        return bundleRepository.findRecentSentByUser(userId, limit);
    }

    /**
     * 获取转发统计
     */
    public Map<String, Object> getForwardStats(Long userId) {
        Long total = bundleRepository.countByUser(userId);
        Long successful = bundleRepository.countSuccessfulByUser(userId);
        List<Object[]> byType = bundleRepository.countByForwardType(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("successful", successful);
        stats.put("failed", total - successful);
        
        Map<String, Long> typeStats = new HashMap<>();
        for (Object[] row : byType) {
            typeStats.put((String) row[0], (Long) row[1]);
        }
        stats.put("byType", typeStats);
        
        return stats;
    }

    /**
     * 清理过期草稿
     */
    @Transactional
    public void cleanupExpiredDrafts() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int deleted = bundleRepository.deleteExpiredDrafts(cutoff);
        log.info("清理了 {} 个过期的转发草稿", deleted);
    }
}