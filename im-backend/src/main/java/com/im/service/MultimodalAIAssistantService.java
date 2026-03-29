package com.im.service;

import com.im.entity.AIConversationSessionEntity;
import com.im.entity.MultimodalAIAssistantEntity;
import com.im.repository.AIConversationSessionRepository;
import com.im.repository.MultimodalAIAssistantRepository;
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
 * 多模态AI助手服务
 * 提供AI助手的创建、管理、会话处理等功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultimodalAIAssistantService {

    private final MultimodalAIAssistantRepository assistantRepository;
    private final AIConversationSessionRepository sessionRepository;

    /**
     * 创建新的AI助手
     */
    @Transactional
    public MultimodalAIAssistantEntity createAssistant(String userId, String name, 
            String description, List<MultimodalAIAssistantEntity.ModalityType> modalities) {
        MultimodalAIAssistantEntity assistant = new MultimodalAIAssistantEntity();
        assistant.setAssistantId(UUID.randomUUID().toString().replace("-", ""));
        assistant.setUserId(userId);
        assistant.setName(name);
        assistant.setDescription(description);
        assistant.setSupportedModalities(modalities);
        assistant.setStatus(MultimodalAIAssistantEntity.AssistantStatus.ACTIVE);
        
        MultimodalAIAssistantEntity saved = assistantRepository.save(assistant);
        log.info("创建AI助手成功: userId={}, assistantId={}, name={}", 
            userId, saved.getAssistantId(), name);
        return saved;
    }

    /**
     * 获取用户的AI助手列表
     */
    public List<MultimodalAIAssistantEntity> getUserAssistants(String userId) {
        return assistantRepository.findByUserIdAndStatusNot(userId, 
            MultimodalAIAssistantEntity.AssistantStatus.DISABLED);
    }

    /**
     * 分页获取用户的AI助手
     */
    public Page<MultimodalAIAssistantEntity> getUserAssistantsPaged(String userId, Pageable pageable) {
        return assistantRepository.findByUserId(userId, pageable);
    }

    /**
     * 根据ID获取AI助手
     */
    public Optional<MultimodalAIAssistantEntity> getAssistantById(String assistantId) {
        return assistantRepository.findByAssistantId(assistantId);
    }

    /**
     * 更新AI助手信息
     */
    @Transactional
    public MultimodalAIAssistantEntity updateAssistant(String assistantId, String name,
            String description, List<MultimodalAIAssistantEntity.ModalityType> modalities,
            MultimodalAIAssistantEntity.ResponseStyle style, Double temperature) {
        
        MultimodalAIAssistantEntity assistant = assistantRepository.findByAssistantId(assistantId)
            .orElseThrow(() -> new RuntimeException("AI助手不存在: " + assistantId));
        
        if (name != null) assistant.setName(name);
        if (description != null) assistant.setDescription(description);
        if (modalities != null) assistant.setSupportedModalities(modalities);
        if (style != null) assistant.setResponseStyle(style);
        if (temperature != null) assistant.setTemperature(temperature);
        
        assistant.setUpdatedAt(LocalDateTime.now());
        
        log.info("更新AI助手: assistantId={}", assistantId);
        return assistantRepository.save(assistant);
    }

    /**
     * 删除AI助手
     */
    @Transactional
    public void deleteAssistant(String assistantId) {
        MultimodalAIAssistantEntity assistant = assistantRepository.findByAssistantId(assistantId)
            .orElseThrow(() -> new RuntimeException("AI助手不存在: " + assistantId));
        
        assistant.setStatus(MultimodalAIAssistantEntity.AssistantStatus.DISABLED);
        assistantRepository.save(assistant);
        
        log.info("删除AI助手: assistantId={}", assistantId);
    }

    /**
     * 创建新的对话会话
     */
    @Transactional
    public AIConversationSessionEntity createSession(String userId, String assistantId, 
            String title, AIConversationSessionEntity.ModalityType modality) {
        
        // 验证助手存在且可用
        MultimodalAIAssistantEntity assistant = assistantRepository.findByAssistantId(assistantId)
            .orElseThrow(() -> new RuntimeException("AI助手不存在: " + assistantId));
        
        if (assistant.getStatus() != MultimodalAIAssistantEntity.AssistantStatus.ACTIVE) {
            throw new RuntimeException("AI助手当前不可用");
        }
        
        // 验证助手支持该模态
        if (!assistant.supportsModality(
            MultimodalAIAssistantEntity.ModalityType.valueOf(modality.name()))) {
            throw new RuntimeException("该助手不支持此模态类型: " + modality);
        }
        
        AIConversationSessionEntity session = new AIConversationSessionEntity();
        session.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        session.setContextId(UUID.randomUUID().toString().replace("-", ""));
        session.setUserId(userId);
        session.setAssistantId(assistantId);
        session.setTitle(title != null ? title : "新会话");
        session.setCurrentModality(modality);
        session.setStatus(AIConversationSessionEntity.SessionStatus.ACTIVE);
        
        AIConversationSessionEntity saved = sessionRepository.save(session);
        
        // 更新助手统计
        assistant.updateStats(
            assistant.getTotalSessions() + 1,
            assistant.getTotalMessages(),
            assistant.getAvgResponseTimeMs()
        );
        assistantRepository.save(assistant);
        
        log.info("创建会话成功: userId={}, sessionId={}, assistantId={}", 
            userId, saved.getSessionId(), assistantId);
        return saved;
    }

    /**
     * 获取用户的所有会话
     */
    public List<AIConversationSessionEntity> getUserSessions(String userId) {
        return sessionRepository.findByUserIdOrderByLastMessageAtDesc(userId);
    }

    /**
     * 获取特定助手的会话
     */
    public List<AIConversationSessionEntity> getAssistantSessions(String assistantId) {
        return sessionRepository.findByAssistantIdOrderByCreatedAtDesc(assistantId);
    }

    /**
     * 获取会话详情
     */
    public Optional<AIConversationSessionEntity> getSessionById(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    /**
     * 结束会话
     */
    @Transactional
    public void endSession(String sessionId) {
        AIConversationSessionEntity session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
        
        session.endSession();
        sessionRepository.save(session);
        
        log.info("结束会话: sessionId={}", sessionId);
    }

    /**
     * 更新会话评分
     */
    @Transactional
    public void rateSession(String sessionId, Integer rating, String feedback) {
        AIConversationSessionEntity session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
        
        session.setRating(rating);
        session.setUserFeedback(feedback);
        sessionRepository.save(session);
        
        // 更新助手满意度评分
        MultimodalAIAssistantEntity assistant = assistantRepository
            .findByAssistantId(session.getAssistantId()).orElse(null);
        if (assistant != null) {
            assistant.updateSatisfactionScore(rating.doubleValue());
            assistantRepository.save(assistant);
        }
        
        log.info("会话评分: sessionId={}, rating={}", sessionId, rating);
    }

    /**
     * 归档会话
     */
    @Transactional
    public void archiveSession(String sessionId) {
        AIConversationSessionEntity session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
        
        session.setIsArchived(true);
        session.setStatus(AIConversationSessionEntity.SessionStatus.ARCHIVED);
        sessionRepository.save(session);
        
        log.info("归档会话: sessionId={}", sessionId);
    }

    /**
     * 切换会话收藏状态
     */
    @Transactional
    public void toggleFavoriteSession(String sessionId) {
        AIConversationSessionEntity session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
        
        session.setIsFavorite(!session.getIsFavorite());
        sessionRepository.save(session);
        
        log.info("切换会话收藏: sessionId={}, favorite={}", sessionId, session.getIsFavorite());
    }

    /**
     * 获取活跃会话统计
     */
    public long getActiveSessionCount(String assistantId) {
        return sessionRepository.countByAssistantIdAndStatus(assistantId, 
            AIConversationSessionEntity.SessionStatus.ACTIVE);
    }

    /**
     * 获取助手性能统计
     */
    public AssistantStats getAssistantStats(String assistantId) {
        MultimodalAIAssistantEntity assistant = assistantRepository.findByAssistantId(assistantId)
            .orElseThrow(() -> new RuntimeException("AI助手不存在: " + assistantId));
        
        long totalSessions = sessionRepository.countByAssistantId(assistantId);
        long activeSessions = sessionRepository.countByAssistantIdAndStatus(assistantId,
            AIConversationSessionEntity.SessionStatus.ACTIVE);
        
        return new AssistantStats(
            totalSessions,
            activeSessions,
            assistant.getTotalMessages(),
            assistant.getAvgResponseTimeMs(),
            assistant.getSatisfactionScore()
        );
    }

    /**
     * 清理过期会话
     */
    @Transactional
    public int cleanupExpiredSessions(int days) {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(days);
        List<AIConversationSessionEntity> expiredSessions = 
            sessionRepository.findByStatusAndLastMessageAtBefore(
                AIConversationSessionEntity.SessionStatus.ACTIVE, expiryDate);
        
        int count = 0;
        for (AIConversationSessionEntity session : expiredSessions) {
            session.setStatus(AIConversationSessionEntity.SessionStatus.EXPIRED);
            sessionRepository.save(session);
            count++;
        }
        
        log.info("清理过期会话: count={}, days={}", count, days);
        return count;
    }

    /**
     * 助手统计数据类
     */
    public static class AssistantStats {
        private final long totalSessions;
        private final long activeSessions;
        private final long totalMessages;
        private final double avgResponseTimeMs;
        private final double satisfactionScore;

        public AssistantStats(long totalSessions, long activeSessions, long totalMessages,
                double avgResponseTimeMs, double satisfactionScore) {
            this.totalSessions = totalSessions;
            this.activeSessions = activeSessions;
            this.totalMessages = totalMessages;
            this.avgResponseTimeMs = avgResponseTimeMs;
            this.satisfactionScore = satisfactionScore;
        }

        // Getters
        public long getTotalSessions() { return totalSessions; }
        public long getActiveSessions() { return activeSessions; }
        public long getTotalMessages() { return totalMessages; }
        public double getAvgResponseTimeMs() { return avgResponseTimeMs; }
        public double getSatisfactionScore() { return satisfactionScore; }
    }
}
