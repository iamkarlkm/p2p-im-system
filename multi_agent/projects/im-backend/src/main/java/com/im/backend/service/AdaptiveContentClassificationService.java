package com.im.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.backend.entity.AdaptiveContentClassificationConfigEntity;
import com.im.backend.entity.ContentClassificationResultEntity;
import com.im.backend.repository.AdaptiveContentClassificationConfigRepository;
import com.im.backend.repository.ContentClassificationResultRepository;
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
 * 自适应内容分类服务
 * 基于自监督学习的自适应内容分类引擎，支持自定义分类体系、增量学习和多模态内容分类
 */
@Slf4j
@Service
public class AdaptiveContentClassificationService {
    
    private final AdaptiveContentClassificationConfigRepository configRepository;
    private final ContentClassificationResultRepository resultRepository;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public AdaptiveContentClassificationService(
            AdaptiveContentClassificationConfigRepository configRepository,
            ContentClassificationResultRepository resultRepository,
            ObjectMapper objectMapper) {
        this.configRepository = configRepository;
        this.resultRepository = resultRepository;
        this.objectMapper = objectMapper;
    }
    
    // ========== 配置管理 ==========
    
    /**
     * 创建分类配置
     */
    @Transactional
    public AdaptiveContentClassificationConfigEntity createConfig(AdaptiveContentClassificationConfigEntity config) {
        try {
            // 验证名称唯一性
            if (configRepository.existsByUserIdAndName(config.getUserId(), config.getName())) {
                throw new IllegalArgumentException("配置名称已存在，请使用其他名称");
            }
            
            // 设置默认值
            config.setStatus(AdaptiveContentClassificationConfigEntity.ClassificationStatus.ACTIVE);
            config.setAccuracyScore(0.0);
            config.setTotalClassifications(0);
            config.setCorrectClassifications(0);
            
            if (config.getClassificationType() == null) {
                config.setClassificationType(AdaptiveContentClassificationConfigEntity.ClassificationType.HIERARCHICAL);
            }
            
            if (config.getContentModality() == null) {
                config.setContentModality(AdaptiveContentClassificationConfigEntity.ContentModality.TEXT_ONLY);
            }
            
            if (config.getPrivacyLevel() == null) {
                config.setPrivacyLevel(AdaptiveContentClassificationConfigEntity.ClassificationPrivacyLevel.PRIVATE);
            }
            
            // 保存配置
            AdaptiveContentClassificationConfigEntity savedConfig = configRepository.save(config);
            log.info("创建分类配置成功，ID: {}, 用户ID: {}, 名称: {}", 
                    savedConfig.getId(), savedConfig.getUserId(), savedConfig.getName());
            return savedConfig;
        } catch (Exception e) {
            log.error("创建分类配置失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建分类配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取配置详情
     */
    public AdaptiveContentClassificationConfigEntity getConfig(Long configId) {
        return configRepository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("分类配置不存在，ID: " + configId));
    }
    
    /**
     * 更新分类配置
     */
    @Transactional
    public AdaptiveContentClassificationConfigEntity updateConfig(Long configId, AdaptiveContentClassificationConfigEntity updatedConfig) {
        try {
            AdaptiveContentClassificationConfigEntity existingConfig = getConfig(configId);
            
            // 更新允许修改的字段
            existingConfig.setName(updatedConfig.getName());
            existingConfig.setDescription(updatedConfig.getDescription());
            existingConfig.setCategoryHierarchy(updatedConfig.getCategoryHierarchy());
            existingConfig.setClassificationType(updatedConfig.getClassificationType());
            existingConfig.setContentModality(updatedConfig.getContentModality());
            existingConfig.setMinConfidenceScore(updatedConfig.getMinConfidenceScore());
            existingConfig.setEnableIncrementalLearning(updatedConfig.getEnableIncrementalLearning());
            existingConfig.setIncrementalLearningBatchSize(updatedConfig.getIncrementalLearningBatchSize());
            existingConfig.setEnableContextAwareness(updatedConfig.getEnableContextAwareness());
            existingConfig.setContextWindowSize(updatedConfig.getContextWindowSize());
            existingConfig.setEnableMultiLanguage(updatedConfig.getEnableMultiLanguage());
            existingConfig.setSupportedLanguages(updatedConfig.getSupportedLanguages());
            existingConfig.setEnableAutoLabelRecommendation(updatedConfig.getEnableAutoLabelRecommendation());
            existingConfig.setMaxLabelRecommendations(updatedConfig.getMaxLabelRecommendations());
            existingConfig.setEnablePrivacyProtection(updatedConfig.getEnablePrivacyProtection());
            existingConfig.setPrivacyLevel(updatedConfig.getPrivacyLevel());
            existingConfig.setEnableEvolutionTracking(updatedConfig.getEnableEvolutionTracking());
            existingConfig.setEvolutionTrackingDepth(updatedConfig.getEvolutionTrackingDepth());
            existingConfig.setStatus(updatedConfig.getStatus());
            existingConfig.setVersionNotes(updatedConfig.getVersionNotes());
            existingConfig.setPerformanceMetrics(updatedConfig.getPerformanceMetrics());
            
            // 更新版本号
            existingConfig.setVersion(existingConfig.getVersion() + 1);
            
            AdaptiveContentClassificationConfigEntity savedConfig = configRepository.save(existingConfig);
            log.info("更新分类配置成功，ID: {}, 版本: {}", savedConfig.getId(), savedConfig.getVersion());
            return savedConfig;
        } catch (Exception e) {
            log.error("更新分类配置失败，ID: {}: {}", configId, e.getMessage(), e);
            throw new RuntimeException("更新分类配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除分类配置
     */
    @Transactional
    public void deleteConfig(Long configId) {
        try {
            AdaptiveContentClassificationConfigEntity config = getConfig(configId);
            config.setStatus(AdaptiveContentClassificationConfigEntity.ClassificationStatus.DELETED);
            configRepository.save(config);
            
            // 可选：同时删除关联的分类结果
            // resultRepository.deleteByClassificationConfigId(configId);
            
            log.info("删除分类配置成功，ID: {}", configId);
        } catch (Exception e) {
            log.error("删除分类配置失败，ID: {}: {}", configId, e.getMessage(), e);
            throw new RuntimeException("删除分类配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询用户配置列表
     */
    public List<AdaptiveContentClassificationConfigEntity> getUserConfigs(Long userId) {
        return configRepository.findByUserId(userId);
    }
    
    /**
     * 分页查询用户配置
     */
    public Page<AdaptiveContentClassificationConfigEntity> getUserConfigsPage(Long userId, Pageable pageable) {
        return configRepository.findByUserId(userId, pageable);
    }
    
    /**
     * 搜索配置
     */
    public List<AdaptiveContentClassificationConfigEntity> searchConfigs(String keyword) {
        List<AdaptiveContentClassificationConfigEntity> results = new ArrayList<>();
        results.addAll(configRepository.searchByName(keyword));
        results.addAll(configRepository.searchByDescription(keyword));
        
        // 去重
        return results.stream()
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 获取配置统计信息
     */
    public Map<String, Object> getConfigStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<AdaptiveContentClassificationConfigEntity> configs = configRepository.findByUserId(userId);
        
        stats.put("totalConfigs", configs.size());
        stats.put("activeConfigs", configs.stream()
                .filter(c -> c.getStatus() == AdaptiveContentClassificationConfigEntity.ClassificationStatus.ACTIVE)
                .count());
        
        // 按分类类型统计
        Map<String, Long> typeStats = configs.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getClassificationType().name(),
                        Collectors.counting()
                ));
        stats.put("typeDistribution", typeStats);
        
        // 按内容模态统计
        Map<String, Long> modalityStats = configs.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getContentModality().name(),
                        Collectors.counting()
                ));
        stats.put("modalityDistribution", modalityStats);
        
        // 平均准确率
        Double avgAccuracy = configs.stream()
                .mapToDouble(AdaptiveContentClassificationConfigEntity::getAccuracyScore)
                .average()
                .orElse(0.0);
        stats.put("averageAccuracy", avgAccuracy);
        
        // 总分类数量
        Integer totalClassifications = configs.stream()
                .mapToInt(AdaptiveContentClassificationConfigEntity::getTotalClassifications)
                .sum();
        stats.put("totalClassifications", totalClassifications);
        
        return stats;
    }
    
    // ========== 内容分类 ==========
    
    /**
     * 分类单个内容
     */
    @Transactional
    public ContentClassificationResultEntity classifyContent(Long configId, Map<String, Object> contentData) {
        try {
            AdaptiveContentClassificationConfigEntity config = getConfig(configId);
            
            // 模拟分类过程（实际应调用AI模型）
            ContentClassificationResultEntity result = new ContentClassificationResultEntity();
            result.setClassificationConfigId(configId);
            result.setContentId(getContentIdFromData(contentData));
            result.setContentType(getContentTypeFromData(contentData));
            result.setUserId(config.getUserId());
            result.setSessionId(getSessionIdFromData(contentData));
            result.setPrimaryCategory(classifyContentInternal(contentData, config));
            result.setSecondaryCategories(generateSecondaryCategories(contentData, config));
            result.setConfidenceScore(calculateConfidenceScore(contentData, config));
            result.setClassificationEvidence(generateClassificationEvidence(contentData));
            result.setIsContextAware(config.getEnableContextAwareness());
            result.setContextInformation(generateContextInformation(contentData, config));
            result.setIsMultiModal(config.getContentModality() == AdaptiveContentClassificationConfigEntity.ContentModality.MULTIMODAL);
            result.setMultiModalAnalysis(generateMultiModalAnalysis(contentData, config));
            result.setIsAutoLabelRecommended(config.getEnableAutoLabelRecommendation());
            result.setRecommendedLabels(generateRecommendedLabels(contentData, config));
            result.setIsPrivacyProtected(config.getEnablePrivacyProtection());
            result.setPrivacyLevel(config.getPrivacyLevel());
            result.setIsEvolutionTracked(config.getEnableEvolutionTracking());
            result.setEvolutionHistory(generateEvolutionHistory(contentData, config));
            result.setClassificationVersion(config.getVersion());
            result.setVersionChanges(generateVersionChanges(config));
            result.setAccuracyContribution(calculateAccuracyContribution(contentData, config));
            result.setIsTrainingExample(determineIfTrainingExample(contentData, config));
            result.setModelFeatures(extractModelFeatures(contentData));
            result.setIsAnomalyDetected(detectAnomalies(contentData, config));
            result.setAnomalyDetails(generateAnomalyDetails(contentData, config));
            result.setLanguageCode(detectLanguage(contentData));
            result.setContentLanguage(detectContentLanguage(contentData));
            result.setCrossLanguageMapping(generateCrossLanguageMapping(contentData, config));
            result.setContentCreatedAt(getContentCreatedAtFromData(contentData));
            
            // 保存分类结果
            ContentClassificationResultEntity savedResult = resultRepository.save(result);
            
            // 更新配置统计
            updateConfigStats(config, savedResult);
            
            log.info("内容分类成功，配置ID: {}, 内容ID: {}, 主要类别: {}, 置信度: {}%", 
                    configId, result.getContentId(), result.getPrimaryCategory(), result.getConfidenceScore());
            return savedResult;
        } catch (Exception e) {
            log.error("内容分类失败，配置ID: {}: {}", configId, e.getMessage(), e);
            throw new RuntimeException("内容分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量分类内容
     */
    @Transactional
    public List<ContentClassificationResultEntity> batchClassifyContent(Long configId, List<Map<String, Object>> contentDataList) {
        List<ContentClassificationResultEntity> results = new ArrayList<>();
        
        for (Map<String, Object> contentData : contentDataList) {
            try {
                ContentClassificationResultEntity result = classifyContent(configId, contentData);
                results.add(result);
            } catch (Exception e) {
                log.error("批量分类失败，跳过内容: {}", e.getMessage());
                // 继续处理其他内容
            }
        }
        
        log.info("批量分类完成，配置ID: {}, 成功: {}, 失败: {}", 
                configId, results.size(), contentDataList.size() - results.size());
        return results;
    }
    
    /**
     * 获取分类结果
     */
    public ContentClassificationResultEntity getClassificationResult(Long resultId) {
        return resultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("分类结果不存在，ID: " + resultId));
    }
    
    /**
     * 查询配置的分类结果
     */
    public List<ContentClassificationResultEntity> getConfigResults(Long configId) {
        return resultRepository.findByClassificationConfigId(configId);
    }
    
    /**
     * 分页查询配置的分类结果
     */
    public Page<ContentClassificationResultEntity> getConfigResultsPage(Long configId, Pageable pageable) {
        return resultRepository.findByClassificationConfigId(configId, pageable);
    }
    
    /**
     * 查询高置信度结果
     */
    public List<ContentClassificationResultEntity> getHighConfidenceResults(Long configId, Integer minConfidence) {
        if (minConfidence == null) {
            minConfidence = 80;
        }
        return resultRepository.findByClassificationConfigId(configId).stream()
                .filter(r -> r.getConfidenceScore() >= minConfidence)
                .collect(Collectors.toList());
    }
    
    /**
     * 查询低置信度结果
     */
    public List<ContentClassificationResultEntity> getLowConfidenceResults(Long configId, Integer maxConfidence) {
        if (maxConfidence == null) {
            maxConfidence = 60;
        }
        return resultRepository.findByClassificationConfigId(configId).stream()
                .filter(r -> r.getConfidenceScore() < maxConfidence)
                .collect(Collectors.toList());
    }
    
    /**
     * 搜索分类结果
     */
    public List<ContentClassificationResultEntity> searchResults(Long configId, String keyword) {
        return resultRepository.searchByPrimaryCategory(keyword).stream()
                .filter(r -> r.getClassificationConfigId().equals(configId))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取分类统计信息
     */
    public Map<String, Object> getClassificationStats(Long configId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<ContentClassificationResultEntity> results = resultRepository.findByClassificationConfigId(configId);
        
        stats.put("totalResults", results.size());
        
        // 置信度分布
        Map<String, Long> confidenceDistribution = results.stream()
                .collect(Collectors.groupingBy(
                        r -> getConfidenceLevel(r.getConfidenceScore()),
                        Collectors.counting()
                ));
        stats.put("confidenceDistribution", confidenceDistribution);
        
        // 类别分布
        Map<String, Long> categoryDistribution = results.stream()
                .collect(Collectors.groupingBy(
                        ContentClassificationResultEntity::getPrimaryCategory,
                        Collectors.counting()
                ));
        stats.put("categoryDistribution", categoryDistribution);
        
        // 内容类型分布
        Map<String, Long> contentTypeDistribution = results.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getContentType().name(),
                        Collectors.counting()
                ));
        stats.put("contentTypeDistribution", contentTypeDistribution);
        
        // 平均置信度
        Double avgConfidence = results.stream()
                .mapToInt(ContentClassificationResultEntity::getConfidenceScore)
                .average()
                .orElse(0.0);
        stats.put("averageConfidence", avgConfidence);
        
        // 高置信度比例
        long highConfidenceCount = results.stream()
                .filter(r -> r.getConfidenceScore() >= 80)
                .count();
        double highConfidencePercentage = results.size() > 0 ? (highConfidenceCount * 100.0 / results.size()) : 0.0;
        stats.put("highConfidencePercentage", highConfidencePercentage);
        
        return stats;
    }
    
    /**
     * 获取分类趋势分析
     */
    public Map<String, Object> getClassificationTrend(Long configId, Integer days) {
        Map<String, Object> trend = new HashMap<>();
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(days != null ? days : 30);
        List<Object[]> dailyStats = resultRepository.countDailyClassifications(startDate);
        
        Map<String, Long> dailyCounts = new LinkedHashMap<>();
        Map<String, Double> dailyAvgConfidence = new LinkedHashMap<>();
        
        for (Object[] stat : dailyStats) {
            String date = stat[0].toString();
            Long count = (Long) stat[1];
            dailyCounts.put(date, count);
            
            // 计算每日平均置信度（简化实现）
            List<ContentClassificationResultEntity> dailyResults = resultRepository.findByClassificationConfigId(configId).stream()
                    .filter(r -> r.getCreatedAt().toLocalDate().toString().equals(date))
                    .collect(Collectors.toList());
            
            double avgConfidence = dailyResults.stream()
                    .mapToInt(ContentClassificationResultEntity::getConfidenceScore)
                    .average()
                    .orElse(0.0);
            dailyAvgConfidence.put(date, avgConfidence);
        }
        
        trend.put("dailyCounts", dailyCounts);
        trend.put("dailyAvgConfidence", dailyAvgConfidence);
        trend.put("totalDays", dailyCounts.size());
        trend.put("totalClassifications", dailyCounts.values().stream().mapToLong(Long::longValue).sum());
        
        return trend;
    }
    
    // ========== 增量学习 ==========
    
    /**
     * 执行增量学习
     */
    @Transactional
    public void performIncrementalLearning(Long configId) {
        try {
            AdaptiveContentClassificationConfigEntity config = getConfig(configId);
            
            if (!config.getEnableIncrementalLearning()) {
                throw new IllegalArgumentException("增量学习未启用");
            }
            
            // 获取低置信度结果作为训练数据
            List<ContentClassificationResultEntity> lowConfidenceResults = getLowConfidenceResults(configId, 60);
            
            if (lowConfidenceResults.isEmpty()) {
                log.info("增量学习：无低置信度结果可用，配置ID: {}", configId);
                return;
            }
            
            // 模拟增量学习过程
            int batchSize = Math.min(config.getIncrementalLearningBatchSize(), lowConfidenceResults.size());
            List<ContentClassificationResultEntity> trainingBatch = lowConfidenceResults.subList(0, batchSize);
            
            // 更新训练示例标记
            List<Long> trainingIds = trainingBatch.stream()
                    .map(ContentClassificationResultEntity::getId)
                    .collect(Collectors.toList());
            resultRepository.markAsTrainingExamplesByIds(trainingIds);
            
            // 模拟模型更新
            double accuracyImprovement = simulateModelUpdate(trainingBatch, config);
            
            // 更新配置准确率
            double newAccuracy = config.getAccuracyScore() + accuracyImprovement;
            config.setAccuracyScore(Math.min(newAccuracy, 100.0));
            configRepository.save(config);
            
            log.info("增量学习完成，配置ID: {}, 训练样本: {}, 准确率提升: {}%", 
                    configId, trainingBatch.size(), accuracyImprovement);
        } catch (Exception e) {
            log.error("增量学习失败，配置ID: {}: {}", configId, e.getMessage(), e);
            throw new RuntimeException("增量学习失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量执行增量学习
     */
    @Transactional
    public void batchIncrementalLearning(List<Long> configIds) {
        for (Long configId : configIds) {
            try {
                performIncrementalLearning(configId);
            } catch (Exception e) {
                log.error("批量增量学习失败，跳过配置ID: {}: {}", configId, e.getMessage());
                // 继续处理其他配置
            }
        }
    }
    
    /**
     * 自动执行增量学习
     */
    @Transactional
    public void autoIncrementalLearning() {
        try {
            // 查找需要重新训练的配置
            List<AdaptiveContentClassificationConfigEntity> configsNeedingRetraining = 
                    configRepository.findConfigsNeedingRetraining(80.0, 100);
            
            log.info("自动增量学习：找到 {} 个需要训练的配置", configsNeedingRetraining.size());
            
            for (AdaptiveContentClassificationConfigEntity config : configsNeedingRetraining) {
                try {
                    performIncrementalLearning(config.getId());
                } catch (Exception e) {
                    log.error("自动增量学习失败，跳过配置ID: {}: {}", config.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("自动增量学习失败: {}", e.getMessage(), e);
        }
    }
    
    // ========== 工具方法 ==========
    
    private Long getContentIdFromData(Map<String, Object> contentData) {
        return ((Number) contentData.getOrDefault("contentId", System.currentTimeMillis())).longValue();
    }
    
    private ContentClassificationResultEntity.ContentType getContentTypeFromData(Map<String, Object> contentData) {
        String type = (String) contentData.getOrDefault("contentType", "TEXT_MESSAGE");
        try {
            return ContentClassificationResultEntity.ContentType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return ContentClassificationResultEntity.ContentType.TEXT_MESSAGE;
        }
    }
    
    private Long getSessionIdFromData(Map<String, Object> contentData) {
        Object sessionId = contentData.get("sessionId");
        return sessionId != null ? ((Number) sessionId).longValue() : null;
    }
    
    private String classifyContentInternal(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        // 模拟分类算法（实际应调用AI模型）
        String content = (String) contentData.getOrDefault("content", "");
        
        // 简单关键词匹配
        if (content.contains("会议") || content.contains("meeting")) {
            return "工作";
        } else if (content.contains("购物") || content.contains("shopping")) {
            return "生活";
        } else if (content.contains("学习") || content.contains("study")) {
            return "教育";
        } else if (content.contains("娱乐") || content.contains("entertainment")) {
            return "娱乐";
        } else if (content.contains("新闻") || content.contains("news")) {
            return "资讯";
        } else {
            return "其他";
        }
    }
    
    private String generateSecondaryCategories(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        try {
            List<String> categories = Arrays.asList("通用", "默认");
            return objectMapper.writeValueAsString(categories);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    private Integer calculateConfidenceScore(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        // 模拟置信度计算
        String content = (String) contentData.getOrDefault("content", "");
        int baseScore = 70; // 基础分数
        
        // 根据内容长度调整
        if (content.length() > 50) baseScore += 10;
        if (content.length() > 100) baseScore += 5;
        
        // 根据关键词数量调整
        int keywordCount = countKeywords(content);
        baseScore += Math.min(keywordCount * 3, 15);
        
        return Math.min(baseScore, 95); // 不超过95
    }
    
    private int countKeywords(String content) {
        String[] keywords = {"会议", "购物", "学习", "娱乐", "新闻", "工作", "生活", "教育"};
        int count = 0;
        for (String keyword : keywords) {
            if (content.contains(keyword)) count++;
        }
        return count;
    }
    
    private String generateClassificationEvidence(Map<String, Object> contentData) {
        try {
            Map<String, Object> evidence = new HashMap<>();
            evidence.put("keywords", Arrays.asList("模拟关键词1", "模拟关键词2"));
            evidence.put("confidenceFactors", Arrays.asList("内容长度", "关键词匹配"));
            evidence.put("aiModelVersion", "1.0.0");
            return objectMapper.writeValueAsString(evidence);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private String generateContextInformation(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("windowSize", config.getContextWindowSize());
            context.put("previousClassifications", Arrays.asList("工作", "生活"));
            context.put("temporalContext", "工作日");
            return objectMapper.writeValueAsString(context);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private String generateMultiModalAnalysis(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        try {
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("textAnalysis", "文本分析完成");
            analysis.put("imageAnalysis", "图像分析完成");
            analysis.put("audioAnalysis", "音频分析完成");
            analysis.put("fusionScore", 85);
            return objectMapper.writeValueAsString(analysis);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private String generateRecommendedLabels(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        try {
            List<String> labels = Arrays.asList("标签1", "标签2", "标签3");
            return objectMapper.writeValueAsString(labels);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    private String generateEvolutionHistory(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        try {
            List<Map<String, Object>> history = new ArrayList<>();
            
            Map<String, Object> entry = new HashMap<>();
            entry.put("timestamp", LocalDateTime.now().toString());
            entry.put("action", "初始分类");
            entry.put("confidence", 85);
            entry.put("category", classifyContentInternal(contentData, config));
            history.add(entry);
            
            return objectMapper.writeValueAsString(history);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    private String generateVersionChanges(AdaptiveContentClassificationConfigEntity config) {
        try {
            Map<String, Object> changes = new HashMap<>();
            changes.put("fromVersion", config.getVersion() - 1);
            changes.put("toVersion", config.getVersion());
            changes.put("changes", Arrays.asList("优化分类算法", "增加上下文支持"));
            return objectMapper.writeValueAsString(changes);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private Double calculateAccuracyContribution(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        // 模拟准确率贡献计算
        double baseContribution = 0.5;
        
        // 根据置信度调整
        Integer confidenceScore = calculateConfidenceScore(contentData, config);
        baseContribution *= (confidenceScore / 100.0);
        
        // 根据内容长度调整
        String content = (String) contentData.getOrDefault("content", "");
        if (content.length() > 100) baseContribution *= 1.2;
        
        return Math.min(baseContribution, 1.0);
    }
    
    private Boolean determineIfTrainingExample(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        // 低置信度或高价值内容作为训练示例
        Integer confidenceScore = calculateConfidenceScore(contentData, config);
        return confidenceScore < 60 || confidenceScore > 90;
    }
    
    private String extractModelFeatures(Map<String, Object> contentData) {
        try {
            Map<String, Object> features = new HashMap<>();
            String content = (String) contentData.getOrDefault("content", "");
            
            features.put("length", content.length());
            features.put("wordCount", content.split("\\s+").length);
            features.put("hasLinks", content.contains("http"));
            features.put("hasImages", contentData.containsKey("imageUrl"));
            features.put("hasAudio", contentData.containsKey("audioUrl"));
            
            return objectMapper.writeValueAsString(features);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private Boolean detectAnomalies(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        // 模拟异常检测
        String content = (String) contentData.getOrDefault("content", "");
        
        // 检测异常模式
        boolean isTooShort = content.length() < 5;
        boolean isTooLong = content.length() > 1000;
        boolean hasSuspiciousPatterns = content.contains("!!!") || content.contains("???");
        
        return isTooShort || isTooLong || hasSuspiciousPatterns;
    }
    
    private String generateAnomalyDetails(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        try {
            Map<String, Object> details = new HashMap<>();
            String content = (String) contentData.getOrDefault("content", "");
            
            details.put("contentLength", content.length());
            details.put("isTooShort", content.length() < 5);
            details.put("isTooLong", content.length() > 1000);
            details.put("suspiciousPatterns", detectSuspiciousPatterns(content));
            details.put("recommendedAction", "人工审核");
            
            return objectMapper.writeValueAsString(details);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private List<String> detectSuspiciousPatterns(String content) {
        List<String> patterns = new ArrayList<>();
        
        if (content.contains("!!!")) patterns.add("过多感叹号");
        if (content.contains("???")) patterns.add("过多问号");
        if (content.matches(".*\\d{10,}.*")) patterns.add("长数字序列");
        if (content.contains("http") && content.split("http").length > 3) patterns.add("多个链接");
        
        return patterns;
    }
    
    private String detectLanguage(Map<String, Object> contentData) {
        String content = (String) contentData.getOrDefault("content", "");
        
        // 简单语言检测
        if (containsChinese(content)) return "zh";
        if (containsJapanese(content)) return "ja";
        if (containsKorean(content)) return "ko";
        
        return "en"; // 默认英语
    }
    
    private boolean containsChinese(String text) {
        return text.matches(".*[\\u4e00-\\u9fff].*");
    }
    
    private boolean containsJapanese(String text) {
        return text.matches(".*[\\u3040-\\u309f\\u30a0-\\u30ff\\u4e00-\\u9faf].*");
    }
    
    private boolean containsKorean(String text) {
        return text.matches(".*[\\uac00-\\ud7af].*");
    }
    
    private String detectContentLanguage(Map<String, Object> contentData) {
        return detectLanguage(contentData); // 简化实现，与检测语言相同
    }
    
    private String generateCrossLanguageMapping(Map<String, Object> contentData, AdaptiveContentClassificationConfigEntity config) {
        try {
            Map<String, Map<String, String>> mapping = new HashMap<>();
            
            Map<String, String> englishMapping = new HashMap<>();
            englishMapping.put("工作", "work");
            englishMapping.put("生活", "life");
            englishMapping.put("教育", "education");
            englishMapping.put("娱乐", "entertainment");
            englishMapping.put("资讯", "news");
            englishMapping.put("其他", "other");
            
            mapping.put("en", englishMapping);
            
            return objectMapper.writeValueAsString(mapping);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private LocalDateTime getContentCreatedAtFromData(Map<String, Object> contentData) {
        Object createdAt = contentData.get("createdAt");
        if (createdAt instanceof LocalDateTime) {
            return (LocalDateTime) createdAt;
        } else if (createdAt instanceof String) {
            return LocalDateTime.parse((String) createdAt);
        } else {
            return LocalDateTime.now();
        }
    }
    
    private void updateConfigStats(AdaptiveContentClassificationConfigEntity config, ContentClassificationResultEntity result) {
        config.setTotalClassifications(config.getTotalClassifications() + 1);
        
        // 模拟正确分类判断（实际应根据用户反馈或验证）
        if (result.getConfidenceScore() >= config.getMinConfidenceScore()) {
            config.setCorrectClassifications(config.getCorrectClassifications() + 1);
        }
        
        // 更新准确率
        if (config.getTotalClassifications() > 0) {
            double accuracy = (config.getCorrectClassifications() * 100.0) / config.getTotalClassifications();
            config.setAccuracyScore(Math.round(accuracy * 100.0) / 100.0);
        }
        
        configRepository.save(config);
    }
    
    private double simulateModelUpdate(List<ContentClassificationResultEntity> trainingBatch, AdaptiveContentClassificationConfigEntity config) {
        // 模拟模型更新效果
        double totalConfidence = trainingBatch.stream()
                .mapToInt(ContentClassificationResultEntity::getConfidenceScore)
                .average()
                .orElse(0.0);
        
        // 低置信度训练样本越多，提升越大
        double improvement = (100 - totalConfidence) * 0.1;
        return Math.min(improvement, 5.0); // 最大提升5%
    }
    
    private String getConfidenceLevel(Integer score) {
        if (score >= 90) return "很高";
        if (score >= 80) return "高";
        if (score >= 70) return "中";
        if (score >= 60) return "低";
        return "很低";
    }
}