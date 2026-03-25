package com.im.backend.service;

import com.im.backend.entity.SmartMessageSummaryEntity;
import com.im.backend.enums.SummaryQuality;
import com.im.backend.enums.SummaryStatus;
import com.im.backend.enums.SummaryType;
import com.im.backend.repository.SmartMessageSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能消息摘要服务
 * 基于 BART-mini/T5-small 轻量级摘要模型的消息摘要服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmartMessageSummaryService {

    private final SmartMessageSummaryRepository summaryRepository;
    
    // ==================== CRUD 操作 ====================
    
    /**
     * 创建新摘要
     */
    @Transactional
    public SmartMessageSummaryEntity createSummary(SmartMessageSummaryEntity summary) {
        log.info("创建智能消息摘要: sessionId={}, userId={}, type={}", 
                summary.getSessionId(), summary.getUserId(), summary.getSummaryType());
        
        // 验证必要字段
        validateSummary(summary);
        
        // 设置默认值
        if (summary.getStatus() == null) {
            summary.setStatus(SummaryStatus.PENDING);
        }
        if (summary.getQuality() == null) {
            summary.setQuality(SummaryQuality.MEDIUM);
        }
        if (summary.getQualityScore() == null) {
            summary.setQualityScore(70);
        }
        if (summary.getVersion() == null) {
            summary.setVersion(1);
        }
        if (summary.getSummaryLength() == null && summary.getSummaryContent() != null) {
            summary.setSummaryLength(summary.getSummaryContent().length());
        }
        if (summary.getGeneratedAt() == null) {
            summary.setGeneratedAt(LocalDateTime.now());
        }
        
        return summaryRepository.save(summary);
    }
    
    /**
     * 根据ID获取摘要
     */
    public Optional<SmartMessageSummaryEntity> getSummaryById(Long id) {
        return summaryRepository.findById(id);
    }
    
    /**
     * 获取摘要（检查用户权限）
     */
    public Optional<SmartMessageSummaryEntity> getSummaryByIdAndUser(Long id, String userId) {
        if (!summaryRepository.existsByIdAndUserId(id, userId)) {
            log.warn("用户 {} 无权访问摘要 {}", userId, id);
            return Optional.empty();
        }
        return summaryRepository.findById(id);
    }
    
    /**
     * 更新摘要
     */
    @Transactional
    public SmartMessageSummaryEntity updateSummary(Long id, SmartMessageSummaryEntity updateData) {
        log.info("更新智能消息摘要: id={}", id);
        
        SmartMessageSummaryEntity existing = summaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("摘要不存在: " + id));
        
        // 更新字段（仅更新允许的字段）
        if (updateData.getSummaryContent() != null) {
            existing.setSummaryContent(updateData.getSummaryContent());
            existing.setSummaryLength(updateData.getSummaryContent().length());
        }
        if (updateData.getStatus() != null) {
            existing.setStatus(updateData.getStatus());
        }
        if (updateData.getQuality() != null) {
            existing.setQuality(updateData.getQuality());
        }
        if (updateData.getQualityScore() != null) {
            existing.setQualityScore(updateData.getQualityScore());
        }
        if (updateData.getSummaryStyle() != null) {
            existing.setSummaryStyle(updateData.getSummaryStyle());
        }
        if (updateData.getKeyPoints() != null) {
            existing.setKeyPoints(updateData.getKeyPoints());
        }
        if (updateData.getMetadata() != null) {
            existing.setMetadata(updateData.getMetadata());
        }
        if (updateData.getUserRating() != null) {
            existing.setUserRating(updateData.getUserRating());
        }
        if (updateData.getUserFeedback() != null) {
            existing.setUserFeedback(updateData.getUserFeedback());
        }
        if (updateData.getIsFavorite() != null) {
            existing.setIsFavorite(updateData.getIsFavorite());
        }
        if (updateData.getOfflineCached() != null) {
            existing.setOfflineCached(updateData.getOfflineCached());
            if (updateData.getCacheExpiryTime() != null) {
                existing.setCacheExpiryTime(updateData.getCacheExpiryTime());
            }
        }
        if (updateData.getTags() != null) {
            existing.setTags(updateData.getTags());
        }
        if (updateData.getBusinessData() != null) {
            existing.setBusinessData(updateData.getBusinessData());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        return summaryRepository.save(existing);
    }
    
    /**
     * 删除摘要（逻辑删除）
     */
    @Transactional
    public void deleteSummary(Long id) {
        log.info("删除智能消息摘要: id={}", id);
        
        SmartMessageSummaryEntity existing = summaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("摘要不存在: " + id));
        
        existing.setDeleted(true);
        existing.setDeletedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());
        summaryRepository.save(existing);
    }
    
    /**
     * 物理删除摘要（仅管理员）
     */
    @Transactional
    public void hardDeleteSummary(Long id) {
        log.info("物理删除智能消息摘要: id={}", id);
        summaryRepository.deleteById(id);
    }
    
    // ==================== 批量操作 ====================
    
    /**
     * 批量创建摘要
     */
    @Transactional
    public List<SmartMessageSummaryEntity> createSummaries(List<SmartMessageSummaryEntity> summaries) {
        log.info("批量创建 {} 个智能消息摘要", summaries.size());
        
        return summaries.stream()
                .map(this::createSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * 批量更新摘要状态
     */
    @Transactional
    public int updateSummariesStatus(List<Long> ids, SummaryStatus status) {
        log.info("批量更新摘要状态: ids={}, status={}", ids.size(), status);
        return summaryRepository.updateStatusByIds(ids, status);
    }
    
    /**
     * 批量更新摘要质量
     */
    @Transactional
    public int updateSummariesQuality(List<Long> ids, SummaryQuality quality, Integer score) {
        log.info("批量更新摘要质量: ids={}, quality={}, score={}", ids.size(), quality, score);
        return summaryRepository.updateQualityByIds(ids, quality, score);
    }
    
    /**
     * 批量标记为已读
     */
    @Transactional
    public int markSummariesAsRead(List<Long> ids) {
        log.info("批量标记摘要为已读: ids={}", ids.size());
        return summaryRepository.markAsReadByIds(ids);
    }
    
    /**
     * 批量标记为喜欢
     */
    @Transactional
    public int markSummariesAsFavorite(List<Long> ids, boolean favorite) {
        log.info("批量标记摘要为喜欢: ids={}, favorite={}", ids.size(), favorite);
        return summaryRepository.markAsFavoriteByIds(ids, favorite);
    }
    
    /**
     * 批量逻辑删除
     */
    @Transactional
    public int softDeleteSummaries(List<Long> ids) {
        log.info("批量逻辑删除摘要: ids={}", ids.size());
        return summaryRepository.softDeleteByIds(ids);
    }
    
    // ==================== 查询方法 ====================
    
    /**
     * 查询用户的所有摘要
     */
    public List<SmartMessageSummaryEntity> getUserSummaries(String userId) {
        return summaryRepository.findByUserId(userId);
    }
    
    /**
     * 分页查询用户摘要
     */
    public Page<SmartMessageSummaryEntity> getUserSummaries(String userId, Pageable pageable) {
        return summaryRepository.findByUserId(userId, pageable);
    }
    
    /**
     * 查询会话的所有摘要
     */
    public List<SmartMessageSummaryEntity> getSessionSummaries(String sessionId) {
        return summaryRepository.findBySessionId(sessionId);
    }
    
    /**
     * 分页查询会话摘要
     */
    public Page<SmartMessageSummaryEntity> getSessionSummaries(String sessionId, Pageable pageable) {
        return summaryRepository.findBySessionId(sessionId, pageable);
    }
    
    /**
     * 查询指定状态的摘要
     */
    public List<SmartMessageSummaryEntity> getSummariesByStatus(SummaryStatus status) {
        return summaryRepository.findByStatus(status);
    }
    
    /**
     * 分页查询指定状态的摘要
     */
    public Page<SmartMessageSummaryEntity> getSummariesByStatus(SummaryStatus status, Pageable pageable) {
        return summaryRepository.findByStatus(status, pageable);
    }
    
    /**
     * 查询指定质量的摘要
     */
    public List<SmartMessageSummaryEntity> getSummariesByQuality(SummaryQuality quality) {
        return summaryRepository.findByQuality(quality);
    }
    
    /**
     * 查询高质量摘要
     */
    public List<SmartMessageSummaryEntity> getHighQualitySummaries() {
        return summaryRepository.findHighQualitySummaries();
    }
    
    /**
     * 查询低质量需要重新生成的摘要
     */
    public List<SmartMessageSummaryEntity> getLowQualitySummaries() {
        return summaryRepository.findLowQualitySummaries();
    }
    
    /**
     * 查询用户的最近摘要
     */
    public List<SmartMessageSummaryEntity> getUserRecentSummaries(String userId, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return summaryRepository.findRecentByUser(userId, pageable);
    }
    
    /**
     * 查询会话的最近摘要
     */
    public List<SmartMessageSummaryEntity> getSessionRecentSummaries(String sessionId, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return summaryRepository.findRecentBySession(sessionId, pageable);
    }
    
    /**
     * 查询用户最喜欢的摘要
     */
    public List<SmartMessageSummaryEntity> getUserTopSummaries(String userId, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return summaryRepository.findTopByUser(userId, pageable);
    }
    
    /**
     * 查询用户反馈评分高的摘要
     */
    public List<SmartMessageSummaryEntity> getUserHighlyRatedSummaries(String userId, int minRating) {
        return summaryRepository.findHighlyRatedByUser(userId, minRating);
    }
    
    /**
     * 搜索摘要内容
     */
    public List<SmartMessageSummaryEntity> searchSummaries(String keyword) {
        return summaryRepository.searchByKeyword(keyword);
    }
    
    /**
     * 搜索用户的摘要内容
     */
    public List<SmartMessageSummaryEntity> searchUserSummaries(String keyword, String userId) {
        return summaryRepository.searchByKeywordAndUser(keyword, userId);
    }
    
    // ==================== 统计方法 ====================
    
    /**
     * 统计用户摘要数量
     */
    public Long countUserSummaries(String userId) {
        return summaryRepository.countByUserId(userId);
    }
    
    /**
     * 统计会话摘要数量
     */
    public Long countSessionSummaries(String sessionId) {
        return summaryRepository.countBySessionId(sessionId);
    }
    
    /**
     * 统计用户指定状态的摘要数量
     */
    public Long countUserSummariesByStatus(String userId, SummaryStatus status) {
        return summaryRepository.countByUserIdAndStatus(userId, status);
    }
    
    /**
     * 统计用户指定质量的摘要数量
     */
    public Long countUserSummariesByQuality(String userId, SummaryQuality quality) {
        return summaryRepository.countByUserIdAndQuality(userId, quality);
    }
    
    /**
     * 统计用户指定类型的摘要数量
     */
    public Long countUserSummariesByType(String userId, SummaryType summaryType) {
        return summaryRepository.countByUserIdAndSummaryType(userId, summaryType);
    }
    
    /**
     * 统计需要重新生成的摘要数量
     */
    public Long countSummariesNeedingRegeneration() {
        return summaryRepository.countByNeedsRegeneration();
    }
    
    /**
     * 统计已过期的缓存摘要数量
     */
    public Long countExpiredCacheSummaries() {
        return summaryRepository.countByExpiredCache();
    }
    
    /**
     * 获取用户的摘要质量分布
     */
    public Map<SummaryQuality, Long> getUserQualityDistribution(String userId) {
        List<Object[]> results = summaryRepository.findUserQualityDistribution(userId);
        Map<SummaryQuality, Long> distribution = new HashMap<>();
        for (Object[] result : results) {
            SummaryQuality quality = (SummaryQuality) result[0];
            Long count = (Long) result[1];
            distribution.put(quality, count);
        }
        return distribution;
    }
    
    /**
     * 获取用户的摘要风格统计
     */
    public Map<String, Map<String, Object>> getUserStyleStats(String userId) {
        List<Object[]> results = summaryRepository.findUserStyleStats(userId);
        Map<String, Map<String, Object>> stats = new HashMap<>();
        for (Object[] result : results) {
            String style = (String) result[0];
            Long count = (Long) result[1];
            Double avgScore = (Double) result[2];
            
            Map<String, Object> styleStats = new HashMap<>();
            styleStats.put("count", count);
            styleStats.put("averageScore", avgScore);
            stats.put(style, styleStats);
        }
        return stats;
    }
    
    // ==================== 清理和维护方法 ====================
    
    /**
     * 清理过期的缓存摘要
     */
    @Transactional
    public int cleanupExpiredCache() {
        log.info("清理过期的缓存摘要");
        return summaryRepository.cleanupExpiredCache();
    }
    
    /**
     * 归档旧摘要
     */
    @Transactional
    public int archiveOldSummaries(LocalDateTime beforeTime) {
        log.info("归档 {} 之前的旧摘要", beforeTime);
        return summaryRepository.archiveOldSummaries(beforeTime);
    }
    
    /**
     * 标记低质量摘要需要重新生成
     */
    @Transactional
    public int markLowQualityForRegeneration() {
        log.info("标记低质量摘要需要重新生成");
        return summaryRepository.markLowQualityForRegeneration();
    }
    
    /**
     * 物理删除已逻辑删除的记录
     */
    @Transactional
    public int hardDeleteExpired(LocalDateTime beforeTime) {
        log.info("物理删除 {} 之前已逻辑删除的记录", beforeTime);
        return summaryRepository.hardDeleteExpired(beforeTime);
    }
    
    // ==================== 摘要生成和更新方法 ====================
    
    /**
     * 生成消息摘要（模拟AI摘要生成）
     */
    @Transactional
    public SmartMessageSummaryEntity generateSummary(String sessionId, String userId, 
                                                    SummaryType summaryType, 
                                                    String originalContent) {
        log.info("生成智能消息摘要: sessionId={}, userId={}, type={}", 
                sessionId, userId, summaryType);
        
        // 模拟AI摘要生成（实际应调用AI模型）
        String summaryContent = generateAISummary(originalContent, summaryType);
        List<String> keyPoints = extractKeyPoints(originalContent);
        
        // 计算质量评分
        Integer qualityScore = calculateQualityScore(summaryContent, originalContent);
        SummaryQuality quality = SummaryQuality.fromScore(qualityScore);
        
        // 构建摘要实体
        SmartMessageSummaryEntity summary = SmartMessageSummaryEntity.builder()
                .sessionId(sessionId)
                .userId(userId)
                .summaryType(summaryType)
                .originalContent(originalContent)
                .summaryContent(summaryContent)
                .keyPoints(keyPoints)
                .status(SummaryStatus.COMPLETED)
                .quality(quality)
                .qualityScore(qualityScore)
                .summaryLength(summaryContent.length())
                .languageCode("zh-CN") // 默认中文
                .metadata(buildMetadata())
                .build();
        
        return createSummary(summary);
    }
    
    /**
     * 重新生成摘要（提高质量）
     */
    @Transactional
    public SmartMessageSummaryEntity regenerateSummary(Long id) {
        log.info("重新生成摘要: id={}", id);
        
        SmartMessageSummaryEntity existing = summaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("摘要不存在: " + id));
        
        // 重新生成摘要内容
        String newSummaryContent = regenerateAISummary(existing.getOriginalContent(), 
                existing.getSummaryType(), existing.getSummaryStyle());
        List<String> newKeyPoints = extractKeyPoints(existing.getOriginalContent());
        
        // 计算新的质量评分
        Integer newQualityScore = calculateQualityScore(newSummaryContent, existing.getOriginalContent());
        SummaryQuality newQuality = SummaryQuality.fromScore(newQualityScore);
        
        // 更新摘要
        existing.setSummaryContent(newSummaryContent);
        existing.setKeyPoints(newKeyPoints);
        existing.setQuality(newQuality);
        existing.setQualityScore(newQualityScore);
        existing.setSummaryLength(newSummaryContent.length());
        existing.setStatus(SummaryStatus.COMPLETED);
        existing.setVersion(existing.getVersion() + 1);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setGeneratedAt(LocalDateTime.now());
        
        // 更新元数据
        Map<String, Object> metadata = existing.getMetadata();
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put("regeneratedAt", LocalDateTime.now().toString());
        metadata.put("regenerationCount", ((Integer) metadata.getOrDefault("regenerationCount", 0)) + 1);
        existing.setMetadata(metadata);
        
        return summaryRepository.save(existing);
    }
    
    /**
     * 更新用户反馈
     */
    @Transactional
    public SmartMessageSummaryEntity updateUserFeedback(Long id, Integer rating, String feedback) {
        log.info("更新用户反馈: id={}, rating={}", id, rating);
        
        SmartMessageSummaryEntity existing = summaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("摘要不存在: " + id));
        
        existing.setUserRating(rating);
        existing.setUserFeedback(feedback);
        existing.setUpdatedAt(LocalDateTime.now());
        
        return summaryRepository.save(existing);
    }
    
    /**
     * 更新缓存设置
     */
    @Transactional
    public SmartMessageSummaryEntity updateCacheSettings(Long id, boolean offlineCached, 
                                                        LocalDateTime expiryTime) {
        log.info("更新缓存设置: id={}, cached={}, expiry={}", id, offlineCached, expiryTime);
        
        SmartMessageSummaryEntity existing = summaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("摘要不存在: " + id));
        
        existing.setOfflineCached(offlineCached);
        existing.setCacheExpiryTime(expiryTime);
        existing.setUpdatedAt(LocalDateTime.now());
        
        return summaryRepository.save(existing);
    }
    
    // ==================== 验证方法 ====================
    
    private void validateSummary(SmartMessageSummaryEntity summary) {
        if (!StringUtils.hasText(summary.getSessionId())) {
            throw new IllegalArgumentException("会话ID不能为空");
        }
        if (!StringUtils.hasText(summary.getUserId())) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (summary.getSummaryType() == null) {
            throw new IllegalArgumentException("摘要类型不能为空");
        }
        if (!StringUtils.hasText(summary.getSummaryContent())) {
            throw new IllegalArgumentException("摘要内容不能为空");
        }
    }
    
    // ==================== AI 模拟方法 ====================
    
    private String generateAISummary(String originalContent, SummaryType summaryType) {
        // 模拟AI摘要生成
        if (originalContent == null || originalContent.length() < 50) {
            return originalContent; // 短内容直接返回
        }
        
        // 根据摘要类型生成不同格式的摘要
        String content = originalContent.substring(0, Math.min(originalContent.length(), 500));
        switch (summaryType) {
            case SINGLE_MESSAGE:
                return "摘要: " + content.substring(0, Math.min(100, content.length())) + "...";
            case CONVERSATION:
            case GROUP_CONVERSATION:
                return "会话摘要: 主要讨论了" + content.substring(0, Math.min(50, content.length())) + "等话题";
            case PRIVATE_CONVERSATION:
                return "私聊摘要: " + content.substring(0, Math.min(80, content.length())) + "...";
            case KEY_DECISIONS:
                return "关键决策: 1. " + content.substring(0, Math.min(40, content.length())) + 
                       " 2. " + content.substring(Math.min(40, content.length()), Math.min(80, content.length()));
            case ACTION_PLAN:
                return "行动计划: " + content.substring(0, Math.min(60, content.length())) + "...";
            default:
                return "摘要: " + content.substring(0, Math.min(120, content.length())) + "...";
        }
    }
    
    private String regenerateAISummary(String originalContent, SummaryType summaryType, String style) {
        // 模拟重新生成（更高质量的摘要）
        String baseSummary = generateAISummary(originalContent, summaryType);
        if ("详细".equals(style)) {
            return baseSummary + " (详细版)";
        } else if ("简洁".equals(style)) {
            return baseSummary.replace("摘要: ", "要点: ");
        } else {
            return "优化版: " + baseSummary;
        }
    }
    
    private List<String> extractKeyPoints(String originalContent) {
        if (originalContent == null || originalContent.length() < 30) {
            return Collections.emptyList();
        }
        
        List<String> keyPoints = new ArrayList<>();
        // 简单模拟关键点提取
        String[] sentences = originalContent.split("[。.!?！？]");
        for (int i = 0; i < Math.min(3, sentences.length); i++) {
            if (sentences[i].length() > 10) {
                keyPoints.add(sentences[i].substring(0, Math.min(50, sentences[i].length())));
            }
        }
        return keyPoints;
    }
    
    private Integer calculateQualityScore(String summary, String original) {
        if (summary == null || original == null) {
            return 50;
        }
        
        // 简单质量评分算法
        int baseScore = 70;
        
        // 摘要长度适中加分
        int summaryLength = summary.length();
        int originalLength = original.length();
        double compressionRatio = (double) summaryLength / originalLength;
        
        if (compressionRatio > 0.1 && compressionRatio < 0.3) {
            baseScore += 10; // 压缩比例适中
        } else if (compressionRatio < 0.1) {
            baseScore += 5; // 过于简洁
        } else if (compressionRatio > 0.5) {
            baseScore -= 5; // 过于冗长
        }
        
        // 包含关键信息加分
        if (summary.contains("摘要") || summary.contains("总结") || summary.contains("要点")) {
            baseScore += 5;
        }
        
        // 确保评分在合理范围内
        return Math.max(30, Math.min(100, baseScore));
    }
    
    private Map<String, Object> buildMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("modelVersion", "bart-mini-1.0");
        metadata.put("generatedAt", LocalDateTime.now().toString());
        metadata.put("processingTimeMs", 150);
        metadata.put("confidence", 0.85);
        metadata.put("language", "zh-CN");
        return metadata;
    }
}