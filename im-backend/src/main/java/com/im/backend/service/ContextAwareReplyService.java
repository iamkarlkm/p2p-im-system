package com.im.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.backend.entity.ContextAwareReplyEntity;
import com.im.backend.repository.ContextAwareReplyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 上下文感知智能回复生成器服务
 * 提供业务逻辑处理
 */
@Service
@Slf4j
public class ContextAwareReplyService {
    
    @Autowired
    private ContextAwareReplyRepository replyRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 基础CRUD操作
    
    /**
     * 创建回复记录
     */
    @Transactional
    public ContextAwareReplyEntity createReply(ContextAwareReplyEntity reply) {
        try {
            // 生成索引键
            reply.setIndexKey(reply.generateIndexKey());
            
            // 设置默认值
            if (reply.getStatus() == null) {
                reply.setStatus(ContextAwareReplyEntity.Status.GENERATED);
            }
            if (reply.getUsed() == null) {
                reply.setUsed(false);
            }
            
            // 设置过期时间（默认24小时后）
            if (reply.getExpiresAt() == null) {
                reply.setExpiresAt(LocalDateTime.now().plusHours(24));
            }
            
            ContextAwareReplyEntity saved = replyRepository.save(reply);
            log.info("创建智能回复记录成功，ID: {}, 用户: {}, 意图: {}", 
                    saved.getId(), saved.getUserId(), saved.getDetectedIntent());
            return saved;
        } catch (Exception e) {
            log.error("创建智能回复记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建回复记录失败", e);
        }
    }
    
    /**
     * 批量创建回复记录
     */
    @Transactional
    public List<ContextAwareReplyEntity> createReplies(List<ContextAwareReplyEntity> replies) {
        List<ContextAwareReplyEntity> savedReplies = new ArrayList<>();
        for (ContextAwareReplyEntity reply : replies) {
            savedReplies.add(createReply(reply));
        }
        return savedReplies;
    }
    
    /**
     * 根据ID获取回复记录
     */
    public Optional<ContextAwareReplyEntity> getReplyById(Long id) {
        return replyRepository.findById(id);
    }
    
    /**
     * 更新回复记录
     */
    @Transactional
    public ContextAwareReplyEntity updateReply(Long id, ContextAwareReplyEntity updateData) {
        return replyRepository.findById(id).map(existing -> {
            // 更新字段
            if (updateData.getSessionId() != null) {
                existing.setSessionId(updateData.getSessionId());
            }
            if (updateData.getTriggerMessageId() != null) {
                existing.setTriggerMessageId(updateData.getTriggerMessageId());
            }
            if (updateData.getTriggerMessageContent() != null) {
                existing.setTriggerMessageContent(updateData.getTriggerMessageContent());
            }
            if (updateData.getContextSummary() != null) {
                existing.setContextSummary(updateData.getContextSummary());
            }
            if (updateData.getDetectedIntent() != null) {
                existing.setDetectedIntent(updateData.getDetectedIntent());
            }
            if (updateData.getIntentConfidence() != null) {
                existing.setIntentConfidence(updateData.getIntentConfidence());
            }
            if (updateData.getReplyCandidates() != null) {
                existing.setReplyCandidates(updateData.getReplyCandidates());
            }
            if (updateData.getSelectedReply() != null) {
                existing.setSelectedReply(updateData.getSelectedReply());
            }
            if (updateData.getRecommendedEmojis() != null) {
                existing.setRecommendedEmojis(updateData.getRecommendedEmojis());
            }
            if (updateData.getLanguageStyle() != null) {
                existing.setLanguageStyle(updateData.getLanguageStyle());
            }
            if (updateData.getReplyLength() != null) {
                existing.setReplyLength(updateData.getReplyLength());
            }
            if (updateData.getSensitivityCheckResult() != null) {
                existing.setSensitivityCheckResult(updateData.getSensitivityCheckResult());
            }
            if (updateData.getSensitivityPassed() != null) {
                existing.setSensitivityPassed(updateData.getSensitivityPassed());
            }
            if (updateData.getPersonalizationFeatures() != null) {
                existing.setPersonalizationFeatures(updateData.getPersonalizationFeatures());
            }
            if (updateData.getUserFeedbackScore() != null) {
                existing.setUserFeedbackScore(updateData.getUserFeedbackScore());
            }
            if (updateData.getUserFeedbackComment() != null) {
                existing.setUserFeedbackComment(updateData.getUserFeedbackComment());
            }
            if (updateData.getUsed() != null) {
                existing.setUsed(updateData.getUsed());
            }
            if (updateData.getGenerationTimeMs() != null) {
                existing.setGenerationTimeMs(updateData.getGenerationTimeMs());
            }
            if (updateData.getModelVersion() != null) {
                existing.setModelVersion(updateData.getModelVersion());
            }
            if (updateData.getGenerationOptions() != null) {
                existing.setGenerationOptions(updateData.getGenerationOptions());
            }
            if (updateData.getStatus() != null) {
                existing.setStatus(updateData.getStatus());
            }
            if (updateData.getExpiresAt() != null) {
                existing.setExpiresAt(updateData.getExpiresAt());
            }
            
            // 重新生成索引键
            existing.setIndexKey(existing.generateIndexKey());
            
            ContextAwareReplyEntity updated = replyRepository.save(existing);
            log.info("更新智能回复记录成功，ID: {}", id);
            return updated;
        }).orElseThrow(() -> new RuntimeException("回复记录不存在，ID: " + id));
    }
    
    /**
     * 删除回复记录
     */
    @Transactional
    public void deleteReply(Long id) {
        if (replyRepository.existsById(id)) {
            replyRepository.deleteById(id);
            log.info("删除智能回复记录成功，ID: {}", id);
        } else {
            throw new RuntimeException("回复记录不存在，ID: " + id);
        }
    }
    
    /**
     * 批量删除回复记录
     */
    @Transactional
    public void deleteReplies(List<Long> ids) {
        for (Long id : ids) {
            deleteReply(id);
        }
    }
    
    // 查询操作
    
    /**
     * 查询用户回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByUser(String userId) {
        return replyRepository.findByUserId(userId);
    }
    
    /**
     * 分页查询用户回复记录
     */
    public Page<ContextAwareReplyEntity> getRepliesByUser(String userId, Pageable pageable) {
        return replyRepository.findByUserId(userId, pageable);
    }
    
    /**
     * 查询会话回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesBySession(String sessionId) {
        return replyRepository.findBySessionId(sessionId);
    }
    
    /**
     * 查询用户和会话回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByUserAndSession(String userId, String sessionId) {
        return replyRepository.findByUserIdAndSessionId(userId, sessionId);
    }
    
    /**
     * 查询触发消息的回复记录
     */
    public Optional<ContextAwareReplyEntity> getReplyByTriggerMessage(String triggerMessageId) {
        return replyRepository.findByTriggerMessageId(triggerMessageId);
    }
    
    /**
     * 查询指定状态的回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByStatus(String status) {
        return replyRepository.findByStatus(status);
    }
    
    /**
     * 查询已使用的回复记录
     */
    public List<ContextAwareReplyEntity> getUsedReplies() {
        return replyRepository.findByUsedTrue();
    }
    
    /**
     * 查询高质量的回复记录
     */
    public List<ContextAwareReplyEntity> getHighQualityReplies() {
        return replyRepository.findHighQualityReplies();
    }
    
    /**
     * 查询高置信度的回复记录
     */
    public List<ContextAwareReplyEntity> getHighConfidenceReplies() {
        return replyRepository.findHighConfidenceReplies();
    }
    
    // 意图相关查询
    
    /**
     * 查询指定意图的回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByIntent(String intent) {
        return replyRepository.findByDetectedIntent(intent);
    }
    
    /**
     * 查询用户指定意图的回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByUserAndIntent(String userId, String intent) {
        return replyRepository.findByUserIdAndDetectedIntent(userId, intent);
    }
    
    /**
     * 查询用户最常用的意图
     */
    public Map<String, Long> getUserTopIntents(String userId) {
        List<Object[]> results = replyRepository.findTopIntentsByUser(userId);
        Map<String, Long> intentCounts = new LinkedHashMap<>();
        for (Object[] result : results) {
            if (result[0] != null && result[1] != null) {
                intentCounts.put(result[0].toString(), ((Number) result[1]).longValue());
            }
        }
        return intentCounts;
    }
    
    // 语言风格相关查询
    
    /**
     * 查询指定语言风格的回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByLanguageStyle(String languageStyle) {
        return replyRepository.findByLanguageStyle(languageStyle);
    }
    
    /**
     * 查询用户指定语言风格的回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByUserAndLanguageStyle(String userId, String languageStyle) {
        return replyRepository.findByUserIdAndLanguageStyle(userId, languageStyle);
    }
    
    /**
     * 查询用户最常用的语言风格
     */
    public Map<String, Long> getUserTopLanguageStyles(String userId) {
        List<Object[]> results = replyRepository.findTopLanguageStylesByUser(userId);
        Map<String, Long> styleCounts = new LinkedHashMap<>();
        for (Object[] result : results) {
            if (result[0] != null && result[1] != null) {
                styleCounts.put(result[0].toString(), ((Number) result[1]).longValue());
            }
        }
        return styleCounts;
    }
    
    // 时间范围查询
    
    /**
     * 查询时间范围内的回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByDateRange(LocalDateTime start, LocalDateTime end) {
        return replyRepository.findByCreatedAtBetween(start, end);
    }
    
    /**
     * 查询用户时间范围内的回复记录
     */
    public List<ContextAwareReplyEntity> getRepliesByUserAndDateRange(String userId, LocalDateTime start, LocalDateTime end) {
        return replyRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }
    
    // 统计操作
    
    /**
     * 统计用户回复记录数量
     */
    public long countRepliesByUser(String userId) {
        return replyRepository.countByUserId(userId);
    }
    
    /**
     * 统计会话回复记录数量
     */
    public long countRepliesBySession(String sessionId) {
        return replyRepository.countBySessionId(sessionId);
    }
    
    /**
     * 统计指定状态回复记录数量
     */
    public long countRepliesByStatus(String status) {
        return replyRepository.countByStatus(status);
    }
    
    /**
     * 统计已使用回复记录数量
     */
    public long countUsedReplies() {
        return replyRepository.countByUsedTrue();
    }
    
    /**
     * 统计高质量回复记录数量
     */
    public long countHighQualityReplies() {
        return replyRepository.countHighQualityReplies();
    }
    
    /**
     * 获取意图分布统计
     */
    public Map<String, Long> getIntentDistribution() {
        List<Object[]> results = replyRepository.countByIntentGroup();
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Object[] result : results) {
            if (result[0] != null && result[1] != null) {
                distribution.put(result[0].toString(), ((Number) result[1]).longValue());
            }
        }
        return distribution;
    }
    
    /**
     * 获取语言风格分布统计
     */
    public Map<String, Long> getLanguageStyleDistribution() {
        List<Object[]> results = replyRepository.countByLanguageStyleGroup();
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Object[] result : results) {
            if (result[0] != null && result[1] != null) {
                distribution.put(result[0].toString(), ((Number) result[1]).longValue());
            }
        }
        return distribution;
    }
    
    /**
     * 获取平均反馈评分
     */
    public Double getAverageFeedbackScore() {
        return replyRepository.averageFeedbackScore();
    }
    
    /**
     * 获取平均生成时间
     */
    public Double getAverageGenerationTime() {
        return replyRepository.averageGenerationTime();
    }
    
    // 高级操作
    
    /**
     * 搜索回复记录
     */
    public List<ContextAwareReplyEntity> searchReplies(String keyword) {
        List<ContextAwareReplyEntity> results = new ArrayList<>();
        results.addAll(replyRepository.searchByContextSummary(keyword));
        results.addAll(replyRepository.searchByTriggerMessageContent(keyword));
        results.addAll(replyRepository.searchBySelectedReply(keyword));
        
        // 去重
        return results.stream()
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 标记回复为已使用
     */
    @Transactional
    public void markAsUsed(Long id) {
        replyRepository.findById(id).ifPresent(reply -> {
            reply.setUsed(true);
            reply.setStatus(ContextAwareReplyEntity.Status.SELECTED);
            replyRepository.save(reply);
            log.info("标记回复为已使用，ID: {}", id);
        });
    }
    
    /**
     * 批量标记为已使用
     */
    @Transactional
    public void markMultipleAsUsed(List<Long> ids) {
        replyRepository.markAsUsedByIds(ids);
        log.info("批量标记 {} 条回复为已使用", ids.size());
    }
    
    /**
     * 提交用户反馈
     */
    @Transactional
    public void submitFeedback(Long id, Integer score, String comment) {
        replyRepository.findById(id).ifPresent(reply -> {
            reply.setUserFeedbackScore(score);
            reply.setUserFeedbackComment(comment);
            replyRepository.save(reply);
            log.info("提交用户反馈，ID: {}, 评分: {}, 评论: {}", id, score, comment);
        });
    }
    
    /**
     * 获取最近N条用户回复
     */
    public List<ContextAwareReplyEntity> getRecentRepliesByUser(String userId, int limit) {
        return replyRepository.findRecentByUserId(userId, limit);
    }
    
    /**
     * 清理过期回复
     */
    @Transactional
    public int cleanupExpiredReplies() {
        int deleted = replyRepository.deleteExpiredReplies(LocalDateTime.now());
        log.info("清理了 {} 条过期回复", deleted);
        return deleted;
    }
    
    /**
     * 清理低质量回复
     */
    @Transactional
    public int cleanupLowQualityReplies() {
        int deleted = replyRepository.cleanupLowQualityReplies();
        log.info("清理了 {} 条低质量回复", deleted);
        return deleted;
    }
    
    /**
     * 获取回复候选列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getReplyCandidates(Long id) {
        return replyRepository.findById(id).map(reply -> {
            try {
                if (reply.getReplyCandidates() != null) {
                    return objectMapper.readValue(reply.getReplyCandidates(), List.class);
                }
            } catch (Exception e) {
                log.error("解析回复候选列表失败: {}", e.getMessage());
            }
            return Collections.<String>emptyList();
        }).orElse(Collections.emptyList());
    }
    
    /**
     * 获取推荐的表情符号列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getRecommendedEmojis(Long id) {
        return replyRepository.findById(id).map(reply -> {
            try {
                if (reply.getRecommendedEmojis() != null) {
                    return objectMapper.readValue(reply.getRecommendedEmojis(), List.class);
                }
            } catch (Exception e) {
                log.error("解析推荐表情符号失败: {}", e.getMessage());
            }
            return Collections.<String>emptyList();
        }).orElse(Collections.emptyList());
    }
    
    /**
     * 获取个性化特征
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPersonalizationFeatures(Long id) {
        return replyRepository.findById(id).map(reply -> {
            try {
                if (reply.getPersonalizationFeatures() != null) {
                    return objectMapper.readValue(reply.getPersonalizationFeatures(), Map.class);
                }
            } catch (Exception e) {
                log.error("解析个性化特征失败: {}", e.getMessage());
            }
            return Collections.<String, Object>emptyMap();
        }).orElse(Collections.emptyMap());
    }
    
    /**
     * 获取生成选项
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getGenerationOptions(Long id) {
        return replyRepository.findById(id).map(reply -> {
            try {
                if (reply.getGenerationOptions() != null) {
                    return objectMapper.readValue(reply.getGenerationOptions(), Map.class);
                }
            } catch (Exception e) {
                log.error("解析生成选项失败: {}", e.getMessage());
            }
            return Collections.<String, Object>emptyMap();
        }).orElse(Collections.emptyMap());
    }
    
    /**
     * 生成智能回复
     */
    public ContextAwareReplyEntity generateReply(String userId, String sessionId, 
                                                 String triggerMessageContent, 
                                                 Map<String, Object> context) {
        try {
            // 这里应该是AI模型调用，现在用模拟实现
            ContextAwareReplyEntity reply = new ContextAwareReplyEntity();
            reply.setUserId(userId);
            reply.setSessionId(sessionId);
            reply.setTriggerMessageContent(triggerMessageContent);
            reply.setContextSummary(extractContextSummary(context));
            reply.setDetectedIntent(detectIntent(triggerMessageContent));
            reply.setIntentConfidence(Math.random() * 0.5 + 0.5); // 模拟置信度
            
            // 生成回复候选
            List<String> candidates = generateReplyCandidates(triggerMessageContent, reply.getDetectedIntent());
            reply.setReplyCandidates(objectMapper.writeValueAsString(candidates));
            
            // 生成推荐表情符号
            List<String> emojis = generateRecommendedEmojis(reply.getDetectedIntent());
            reply.setRecommendedEmojis(objectMapper.writeValueAsString(emojis));
            
            // 设置默认语言风格和长度
            reply.setLanguageStyle(ContextAwareReplyEntity.LanguageStyle.FRIENDLY);
            reply.setReplyLength(ContextAwareReplyEntity.ReplyLength.MEDIUM);
            
            // 敏感性检查
            reply.setSensitivityPassed(true);
            reply.setSensitivityCheckResult("{\"passed\": true, \"reason\": \"No sensitive content detected\"}");
            
            // 个性化特征（模拟）
            Map<String, Object> personalization = new HashMap<>();
            personalization.put("preferredStyle", "friendly");
            personalization.put("emojiFrequency", "medium");
            personalization.put("formalityLevel", "casual");
            reply.setPersonalizationFeatures(objectMapper.writeValueAsString(personalization));
            
            // 生成选项
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.7);
            options.put("maxTokens", 100);
            options.put("numCandidates", 3);
            reply.setGenerationOptions(objectMapper.writeValueAsString(options));
            
            reply.setGenerationTimeMs(500L); // 模拟生成时间
            reply.setModelVersion("v1.0.0");
            
            return createReply(reply);
        } catch (Exception e) {
            log.error("生成智能回复失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成回复失败", e);
        }
    }
    
    /**
     * 提取上下文摘要
     */
    private String extractContextSummary(Map<String, Object> context) {
        // 模拟实现
        return "近期对话模式分析完成，用户偏好友好风格，情绪状态稳定";
    }
    
    /**
     * 检测意图
     */
    private String detectIntent(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("你好") || lower.contains("hi") || lower.contains("hello")) {
            return ContextAwareReplyEntity.Intent.GREETING;
        } else if (lower.contains("谢谢") || lower.contains("感谢") || lower.contains("thanks")) {
            return ContextAwareReplyEntity.Intent.APPRECIATION;
        } else if (lower.contains("?") || lower.contains("吗") || lower.contains("什么")) {
            return ContextAwareReplyEntity.Intent.QUESTION;
        } else if (lower.contains("工作") || lower.contains("项目") || lower.contains("会议")) {
            return ContextAwareReplyEntity.Intent.BUSINESS;
        } else {
            return ContextAwareReplyEntity.Intent.SOCIAL;
        }
    }
    
    /**
     * 生成回复候选
     */
    private List<String> generateReplyCandidates(String message, String intent) {
        List<String> candidates = new ArrayList<>();
        
        switch (intent) {
            case ContextAwareReplyEntity.Intent.GREETING:
                candidates.add("你好！很高兴见到你。");
                candidates.add("嗨！今天过得怎么样？");
                candidates.add("哈喽！有什么我可以帮忙的吗？");
                break;
            case ContextAwareReplyEntity.Intent.QUESTION:
                candidates.add("这是个好问题，让我想想怎么回答。");
                candidates.add("关于这个问题，我认为...");
                candidates.add("我不太确定，需要更多信息才能回答。");
                break;
            case ContextAwareReplyEntity.Intent.APPRECIATION:
                candidates.add("不客气，这是应该的！");
                candidates.add("很高兴能帮到你！");
                candidates.add("随时为你效劳！");
                break;
            default:
                candidates.add("我明白了。");
                candidates.add("好的，收到。");
                candidates.add("嗯，继续。");
                break;
        }
        
        return candidates;
    }
    
    /**
     * 生成推荐表情符号
     */
    private List<String> generateRecommendedEmojis(String intent) {
        List<String> emojis = new ArrayList<>();
        
        switch (intent) {
            case ContextAwareReplyEntity.Intent.GREETING:
                emojis.add("👋");
                emojis.add("😊");
                emojis.add("🙌");
                break;
            case ContextAwareReplyEntity.Intent.QUESTION:
                emojis.add("🤔");
                emojis.add("💭");
                emojis.add("❓");
                break;
            case ContextAwareReplyEntity.Intent.APPRECIATION:
                emojis.add("🙏");
                emojis.add("😄");
                emojis.add("👍");
                break;
            default:
                emojis.add("✅");
                emojis.add("👌");
                emojis.add("💯");
                break;
        }
        
        return emojis;
    }
}