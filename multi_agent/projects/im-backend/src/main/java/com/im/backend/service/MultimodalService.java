package com.im.backend.service;

import com.im.backend.entity.MultimodalConfigEntity;
import com.im.backend.entity.MultimodalAnalysisResultEntity;
import com.im.backend.repository.MultimodalConfigRepository;
import com.im.backend.repository.MultimodalAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 多模态内容理解引擎服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MultimodalService {
    
    private final MultimodalConfigRepository configRepository;
    private final MultimodalAnalysisResultRepository resultRepository;
    private final ObjectMapper objectMapper;
    
    // ==================== 配置管理 ====================
    
    /**
     * 创建新的多模态配置
     */
    public MultimodalConfigEntity createConfig(MultimodalConfigEntity config) {
        validateConfig(config);
        config.setEnabled(true);
        config.setVersion(1);
        return configRepository.save(config);
    }
    
    /**
     * 更新配置
     */
    public MultimodalConfigEntity updateConfig(Long id, MultimodalConfigEntity configUpdate) {
        MultimodalConfigEntity existing = getConfigById(id);
        
        // 更新字段
        existing.setName(configUpdate.getName());
        existing.setDescription(configUpdate.getDescription());
        existing.setTextEnabled(configUpdate.getTextEnabled());
        existing.setTextModel(configUpdate.getTextModel());
        existing.setTextMaxLength(configUpdate.getTextMaxLength());
        existing.setTextLanguages(configUpdate.getTextLanguages());
        existing.setImageEnabled(configUpdate.getImageEnabled());
        existing.setImageModel(configUpdate.getImageModel());
        existing.setImageMaxSize(configUpdate.getImageMaxSize());
        existing.setImageSupportedFormats(configUpdate.getImageSupportedFormats());
        existing.setAudioEnabled(configUpdate.getAudioEnabled());
        existing.setAudioModel(configUpdate.getAudioModel());
        existing.setAudioMaxDuration(configUpdate.getAudioMaxDuration());
        existing.setAudioSupportedFormats(configUpdate.getAudioSupportedFormats());
        existing.setVideoEnabled(configUpdate.getVideoEnabled());
        existing.setVideoModel(configUpdate.getVideoModel());
        existing.setVideoMaxDuration(configUpdate.getVideoMaxDuration());
        existing.setVideoMaxSize(configUpdate.getVideoMaxSize());
        existing.setMultimodalFusionEnabled(configUpdate.getMultimodalFusionEnabled());
        existing.setFusionMethod(configUpdate.getFusionMethod());
        existing.setCrossModalWeighting(configUpdate.getCrossModalWeighting());
        existing.setCacheEnabled(configUpdate.getCacheEnabled());
        existing.setCacheTtlHours(configUpdate.getCacheTtlHours());
        existing.setCacheMaxSize(configUpdate.getCacheMaxSize());
        existing.setConcurrentWorkers(configUpdate.getConcurrentWorkers());
        existing.setTimeoutMs(configUpdate.getTimeoutMs());
        existing.setBatchSize(configUpdate.getBatchSize());
        existing.setConfidenceThreshold(configUpdate.getConfidenceThreshold());
        existing.setFallbackEnabled(configUpdate.getFallbackEnabled());
        existing.setFallbackModel(configUpdate.getFallbackModel());
        existing.setMetricsEnabled(configUpdate.getMetricsEnabled());
        existing.setMetricsIntervalMinutes(configUpdate.getMetricsIntervalMinutes());
        existing.setAlertThresholdErrorRate(configUpdate.getAlertThresholdErrorRate());
        existing.setAlertThresholdLatencyMs(configUpdate.getAlertThresholdLatencyMs());
        existing.setPrivacyEnabled(configUpdate.getPrivacyEnabled());
        existing.setAnonymizationEnabled(configUpdate.getAnonymizationEnabled());
        existing.setDataRetentionDays(configUpdate.getDataRetentionDays());
        existing.setCustomConfig(configUpdate.getCustomConfig());
        
        // 版本号递增
        existing.setVersion(existing.getVersion() + 1);
        
        validateConfig(existing);
        return configRepository.save(existing);
    }
    
    /**
     * 根据ID获取配置
     */
    @Transactional(readOnly = true)
    public MultimodalConfigEntity getConfigById(Long id) {
        return configRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + id));
    }
    
    /**
     * 根据名称获取配置
     */
    @Transactional(readOnly = true)
    public MultimodalConfigEntity getConfigByName(String name) {
        return configRepository.findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + name));
    }
    
    /**
     * 获取所有启用的配置
     */
    @Transactional(readOnly = true)
    public List<MultimodalConfigEntity> getAllEnabledConfigs() {
        return configRepository.findByEnabledTrue();
    }
    
    /**
     * 获取默认配置（第一个启用的配置）
     */
    @Transactional(readOnly = true)
    public MultimodalConfigEntity getDefaultConfig() {
        List<MultimodalConfigEntity> enabledConfigs = configRepository.findByEnabledTrue();
        if (enabledConfigs.isEmpty()) {
            throw new IllegalStateException("没有启用的多模态配置");
        }
        return enabledConfigs.get(0);
    }
    
    /**
     * 禁用配置
     */
    public void disableConfig(Long id) {
        MultimodalConfigEntity config = getConfigById(id);
        config.setEnabled(false);
        configRepository.save(config);
    }
    
    /**
     * 启用配置
     */
    public void enableConfig(Long id) {
        MultimodalConfigEntity config = getConfigById(id);
        config.setEnabled(true);
        configRepository.save(config);
    }
    
    /**
     * 删除配置
     */
    public void deleteConfig(Long id) {
        MultimodalConfigEntity config = getConfigById(id);
        configRepository.delete(config);
    }
    
    /**
     * 获取配置统计信息
     */
    @Transactional(readOnly = true)
    public ConfigStats getConfigStats() {
        ConfigStats stats = new ConfigStats();
        stats.setTotalConfigs(configRepository.count());
        stats.setEnabledConfigs(configRepository.countEnabledConfigs());
        stats.setTextAnalysisConfigs(configRepository.countTextAnalysisConfigs());
        stats.setImageAnalysisConfigs(configRepository.countImageAnalysisConfigs());
        stats.setAudioAnalysisConfigs(configRepository.countAudioAnalysisConfigs());
        stats.setVideoAnalysisConfigs(configRepository.countVideoAnalysisConfigs());
        stats.setFallbackEnabledConfigs(configRepository.countFallbackEnabledConfigs());
        return stats;
    }
    
    /**
     * 验证配置
     */
    private void validateConfig(MultimodalConfigEntity config) {
        if (config.getName() == null || config.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("配置名称不能为空");
        }
        if (config.getTextEnabled() == null) {
            config.setTextEnabled(false);
        }
        if (config.getImageEnabled() == null) {
            config.setImageEnabled(false);
        }
        if (config.getAudioEnabled() == null) {
            config.setAudioEnabled(false);
        }
        if (config.getVideoEnabled() == null) {
            config.setVideoEnabled(false);
        }
        if (config.getMultimodalFusionEnabled() == null) {
            config.setMultimodalFusionEnabled(false);
        }
        if (config.getCacheEnabled() == null) {
            config.setCacheEnabled(true);
        }
        if (config.getMetricsEnabled() == null) {
            config.setMetricsEnabled(true);
        }
        if (config.getPrivacyEnabled() == null) {
            config.setPrivacyEnabled(true);
        }
    }
    
    // ==================== 分析结果管理 ====================
    
    /**
     * 创建分析请求
     */
    public MultimodalAnalysisResultEntity createAnalysisRequest(AnalysisRequest request) {
        MultimodalAnalysisResultEntity result = new MultimodalAnalysisResultEntity();
        result.setRequestId(generateRequestId());
        result.setSessionId(request.getSessionId());
        result.setUserId(request.getUserId());
        result.setMessageId(request.getMessageId());
        result.setContentType(request.getContentType());
        result.setContentHash(calculateContentHash(request));
        result.setAnalysisStatus("pending");
        result.setBusinessContext(request.getBusinessContext());
        result.setPriority(request.getPriority() != null ? request.getPriority() : 5);
        
        if (request.getContentType() != null) {
            switch (request.getContentType()) {
                case "text":
                    result.setTextAnalysisResult(request.getTextContent());
                    break;
                case "image":
                    result.setImageAnalysisResult(request.getImageUrl());
                    break;
                case "audio":
                    result.setAudioAnalysisResult(request.getAudioUrl());
                    break;
                case "video":
                    result.setVideoAnalysisResult(request.getVideoUrl());
                    break;
                case "mixed":
                    if (request.getTextContent() != null) {
                        result.setTextAnalysisResult(request.getTextContent());
                    }
                    if (request.getImageUrl() != null) {
                        result.setImageAnalysisResult(request.getImageUrl());
                    }
                    if (request.getAudioUrl() != null) {
                        result.setAudioAnalysisResult(request.getAudioUrl());
                    }
                    if (request.getVideoUrl() != null) {
                        result.setVideoAnalysisResult(request.getVideoUrl());
                    }
                    break;
            }
        }
        
        return resultRepository.save(result);
    }
    
    /**
     * 开始分析处理
     */
    public MultimodalAnalysisResultEntity startAnalysis(String requestId, MultimodalConfigEntity config) {
        MultimodalAnalysisResultEntity result = getResultByRequestId(requestId);
        if (!result.isProcessing()) {
            result.markAsProcessing();
            return resultRepository.save(result);
        }
        return result;
    }
    
    /**
     * 完成分析
     */
    public MultimodalAnalysisResultEntity completeAnalysis(String requestId, AnalysisResult resultData) {
        MultimodalAnalysisResultEntity result = getResultByRequestId(requestId);
        
        // 更新文本分析结果
        if (resultData.getTextAnalysis() != null) {
            result.setTextSummary(resultData.getTextAnalysis().getSummary());
            result.setTextKeywords(String.join(",", resultData.getTextAnalysis().getKeywords()));
            result.setTextSentiment(resultData.getTextAnalysis().getSentiment());
            result.setTextSentimentScore(resultData.getTextAnalysis().getSentimentScore());
            result.setTextIntent(resultData.getTextAnalysis().getIntent());
            try {
                result.setTextEntities(objectMapper.writeValueAsString(resultData.getTextAnalysis().getEntities()));
            } catch (Exception e) {
                log.warn("无法序列化文本实体: {}", e.getMessage());
            }
        }
        
        // 更新图像分析结果
        if (resultData.getImageAnalysis() != null) {
            result.setImageDescription(resultData.getImageAnalysis().getDescription());
            result.setImageTags(String.join(",", resultData.getImageAnalysis().getTags()));
            result.setImageScene(resultData.getImageAnalysis().getScene());
            try {
                result.setImageObjects(objectMapper.writeValueAsString(resultData.getImageAnalysis().getObjects()));
                result.setImageFaces(objectMapper.writeValueAsString(resultData.getImageAnalysis().getFaces()));
                result.setImageColors(objectMapper.writeValueAsString(resultData.getImageAnalysis().getColors()));
            } catch (Exception e) {
                log.warn("无法序列化图像分析结果: {}", e.getMessage());
            }
        }
        
        // 更新音频分析结果
        if (resultData.getAudioAnalysis() != null) {
            result.setAudioTranscription(resultData.getAudioAnalysis().getTranscription());
            result.setAudioEmotion(resultData.getAudioAnalysis().getEmotion());
            result.setAudioEmotionScore(resultData.getAudioAnalysis().getEmotionScore());
            result.setAudioSpeakers(resultData.getAudioAnalysis().getSpeakers());
            result.setAudioKeywords(resultData.getAudioAnalysis().getKeywords());
        }
        
        // 更新视频分析结果
        if (resultData.getVideoAnalysis() != null) {
            result.setVideoDescription(resultData.getVideoAnalysis().getDescription());
            try {
                result.setVideoScenes(objectMapper.writeValueAsString(resultData.getVideoAnalysis().getScenes()));
                result.setVideoKeyFrames(objectMapper.writeValueAsString(resultData.getVideoAnalysis().getKeyFrames()));
                result.setVideoMotionAnalysis(objectMapper.writeValueAsString(resultData.getVideoAnalysis().getMotionAnalysis()));
            } catch (Exception e) {
                log.warn("无法序列化视频分析结果: {}", e.getMessage());
            }
        }
        
        // 更新多模态融合结果
        if (resultData.getMultimodalFusion() != null) {
            result.setMultimodalSummary(resultData.getMultimodalFusion().getSummary());
            result.setMultimodalTags(String.join(",", resultData.getMultimodalFusion().getTags()));
            result.setMultimodalSentiment(resultData.getMultimodalFusion().getSentiment());
            result.setMultimodalSentimentScore(resultData.getMultimodalFusion().getSentimentScore());
            result.setMultimodalIntent(resultData.getMultimodalFusion().getIntent());
            try {
                result.setCrossModalCorrelations(objectMapper.writeValueAsString(resultData.getMultimodalFusion().getCorrelations()));
            } catch (Exception e) {
                log.warn("无法序列化跨模态相关性: {}", e.getMessage());
            }
        }
        
        // 更新质量评估
        result.setConfidenceScore(resultData.getConfidenceScore());
        result.setQualityRating(resultData.getQualityRating());
        result.setProcessingTimeMs(resultData.getProcessingTimeMs());
        result.setModelUsed(resultData.getModelUsed());
        result.setCostUnits(resultData.getCostUnits());
        
        // 完成分析
        result.markAsCompleted();
        return resultRepository.save(result);
    }
    
    /**
     * 标记分析失败
     */
    public MultimodalAnalysisResultEntity markAnalysisFailed(String requestId, String errorMessage) {
        MultimodalAnalysisResultEntity result = getResultByRequestId(requestId);
        result.markAsFailed(errorMessage);
        result.setRetryCount(result.getRetryCount() != null ? result.getRetryCount() + 1 : 1);
        return resultRepository.save(result);
    }
    
    /**
     * 根据请求ID获取分析结果
     */
    @Transactional(readOnly = true)
    public MultimodalAnalysisResultEntity getResultByRequestId(String requestId) {
        return resultRepository.findByRequestId(requestId)
            .orElseThrow(() -> new IllegalArgumentException("分析结果不存在: " + requestId));
    }
    
    /**
     * 根据消息ID获取分析结果
     */
    @Transactional(readOnly = true)
    public Optional<MultimodalAnalysisResultEntity> getResultByMessageId(Long messageId) {
        return resultRepository.findByMessageId(messageId);
    }
    
    /**
     * 获取用户的分析历史
     */
    @Transactional(readOnly = true)
    public List<MultimodalAnalysisResultEntity> getUserAnalysisHistory(Long userId, int limit) {
        return resultRepository.findByUserId(userId).stream()
            .limit(limit)
            .toList();
    }
    
    /**
     * 获取会话的分析历史
     */
    @Transactional(readOnly = true)
    public List<MultimodalAnalysisResultEntity> getSessionAnalysisHistory(String sessionId, int limit) {
        return resultRepository.findBySessionId(sessionId).stream()
            .limit(limit)
            .toList();
    }
    
    /**
     * 获取分析结果统计
     */
    @Transactional(readOnly = true)
    public ResultStats getResultStats() {
        ResultStats stats = new ResultStats();
        stats.setTotalResults(resultRepository.count());
        stats.setCompletedResults(resultRepository.countCompleted());
        stats.setFailedResults(resultRepository.countFailed());
        stats.setProcessingResults(resultRepository.countProcessing());
        stats.setPendingResults(resultRepository.countPending());
        stats.setAverageProcessingTime(resultRepository.averageProcessingTime());
        stats.setAverageConfidenceScore(resultRepository.averageConfidenceScore());
        stats.setAverageSentimentScore(resultRepository.averageSentimentScore());
        stats.setTotalCost(resultRepository.totalCost());
        return stats;
    }
    
    /**
     * 清理过期的缓存结果
     */
    public int cleanupExpiredCache() {
        List<MultimodalAnalysisResultEntity> expired = resultRepository.findExpiredCache(LocalDateTime.now());
        expired.forEach(result -> result.setCached(false));
        resultRepository.saveAll(expired);
        return expired.size();
    }
    
    /**
     * 重试失败的请求
     */
    public List<MultimodalAnalysisResultEntity> retryFailedRequests() {
        List<MultimodalAnalysisResultEntity> retryList = resultRepository.findResultsNeedingRetry();
        retryList.forEach(result -> {
            result.setAnalysisStatus("pending");
            result.setRetryCount(result.getRetryCount() != null ? result.getRetryCount() + 1 : 1);
        });
        return resultRepository.saveAll(retryList);
    }
    
    /**
     * 获取分析结果的情感分布
     */
    @Transactional(readOnly = true)
    public List<SentimentDistribution> getSentimentDistribution() {
        List<Object[]> sentimentCounts = resultRepository.countByTextSentiment();
        return sentimentCounts.stream()
            .map(arr -> new SentimentDistribution((String) arr[0], ((Number) arr[1]).longValue()))
            .toList();
    }
    
    /**
     * 获取分析结果的意图分布
     */
    @Transactional(readOnly = true)
    public List<IntentDistribution> getIntentDistribution() {
        List<Object[]> intentCounts = resultRepository.countByTextIntent();
        return intentCounts.stream()
            .map(arr -> new IntentDistribution((String) arr[0], ((Number) arr[1]).longValue()))
            .toList();
    }
    
    /**
     * 获取分析结果的场景分布
     */
    @Transactional(readOnly = true)
    public List<SceneDistribution> getSceneDistribution() {
        List<Object[]> sceneCounts = resultRepository.countByImageScene();
        return sceneCounts.stream()
            .map(arr -> new SceneDistribution((String) arr[0], ((Number) arr[1]).longValue()))
            .toList();
    }
    
    // ==================== 辅助方法 ====================
    
    private String generateRequestId() {
        return "MM_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }
    
    private String calculateContentHash(AnalysisRequest request) {
        StringBuilder content = new StringBuilder();
        if (request.getTextContent() != null) {
            content.append(request.getTextContent());
        }
        if (request.getImageUrl() != null) {
            content.append(request.getImageUrl());
        }
        if (request.getAudioUrl() != null) {
            content.append(request.getAudioUrl());
        }
        if (request.getVideoUrl() != null) {
            content.append(request.getVideoUrl());
        }
        return String.valueOf(content.toString().hashCode());
    }
    
    // ==================== 内部类 ====================
    
    @lombok.Data
    public static class AnalysisRequest {
        private String sessionId;
        private Long userId;
        private Long messageId;
        private String contentType;
        private String businessContext;
        private Integer priority;
        
        // 文本内容
        private String textContent;
        
        // 媒体URL
        private String imageUrl;
        private String audioUrl;
        private String videoUrl;
    }
    
    @lombok.Data
    public static class AnalysisResult {
        // 文本分析结果
        private TextAnalysisResult textAnalysis;
        
        // 图像分析结果
        private ImageAnalysisResult imageAnalysis;
        
        // 音频分析结果
        private AudioAnalysisResult audioAnalysis;
        
        // 视频分析结果
        private VideoAnalysisResult videoAnalysis;
        
        // 多模态融合结果
        private MultimodalFusionResult multimodalFusion;
        
        // 质量评估
        private Double confidenceScore;
        private String qualityRating;
        private Long processingTimeMs;
        private String modelUsed;
        private Double costUnits;
    }
    
    @lombok.Data
    public static class TextAnalysisResult {
        private String summary;
        private List<String> keywords;
        private String sentiment;
        private Double sentimentScore;
        private String intent;
        private List<NamedEntity> entities;
    }
    
    @lombok.Data
    public static class ImageAnalysisResult {
        private String description;
        private List<String> tags;
        private String scene;
        private List<DetectedObject> objects;
        private List<FaceDetection> faces;
        private List<ColorAnalysis> colors;
    }
    
    @lombok.Data
    public static class AudioAnalysisResult {
        private String transcription;
        private String emotion;
        private Double emotionScore;
        private Integer speakers;
        private String keywords;
    }
    
    @lombok.Data
    public static class VideoAnalysisResult {
        private String description;
        private List<VideoScene> scenes;
        private List<KeyFrame> keyFrames;
        private MotionAnalysis motionAnalysis;
    }
    
    @lombok.Data
    public static class MultimodalFusionResult {
        private String summary;
        private List<String> tags;
        private String sentiment;
        private Double sentimentScore;
        private String intent;
        private Map<String, Double> correlations;
    }
    
    @lombok.Data
    public static class ConfigStats {
        private Long totalConfigs;
        private Long enabledConfigs;
        private Long textAnalysisConfigs;
        private Long imageAnalysisConfigs;
        private Long audioAnalysisConfigs;
        private Long videoAnalysisConfigs;
        private Long fallbackEnabledConfigs;
    }
    
    @lombok.Data
    public static class ResultStats {
        private Long totalResults;
        private Long completedResults;
        private Long failedResults;
        private Long processingResults;
        private Long pendingResults;
        private Double averageProcessingTime;
        private Double averageConfidenceScore;
        private Double averageSentimentScore;
        private Double totalCost;
    }
    
    @lombok.Data
    public static class SentimentDistribution {
        private String sentiment;
        private Long count;
        
        public SentimentDistribution(String sentiment, Long count) {
            this.sentiment = sentiment;
            this.count = count;
        }
    }
    
    @lombok.Data
    public static class IntentDistribution {
        private String intent;
        private Long count;
        
        public IntentDistribution(String intent, Long count) {
            this.intent = intent;
            this.count = count;
        }
    }
    
    @lombok.Data
    public static class SceneDistribution {
        private String scene;
        private Long count;
        
        public SceneDistribution(String scene, Long count) {
            this.scene = scene;
            this.count = count;
        }
    }
    
    @lombok.Data
    public static class NamedEntity {
        private String text;
        private String type;
        private Integer start;
        private Integer end;
    }
    
    @lombok.Data
    public static class DetectedObject {
        private String name;
        private Double confidence;
        private BoundingBox bbox;
    }
    
    @lombok.Data
    public static class FaceDetection {
        private BoundingBox bbox;
        private Double confidence;
        private String gender;
        private Integer age;
        private String emotion;
    }
    
    @lombok.Data
    public static class ColorAnalysis {
        private String color;
        private Double percentage;
        private String hex;
    }
    
    @lombok.Data
    public static class VideoScene {
        private Integer startFrame;
        private Integer endFrame;
        private String description;
        private List<String> tags;
    }
    
    @lombok.Data
    public static class KeyFrame {
        private Integer frameNumber;
        private String description;
        private Double significance;
    }
    
    @lombok.Data
    public static class MotionAnalysis {
        private Double motionIntensity;
        private List<MotionVector> vectors;
        private List<MovingObject> movingObjects;
    }
    
    @lombok.Data
    public static class BoundingBox {
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
    }
    
    @lombok.Data
    public static class MotionVector {
        private Integer x;
        private Integer y;
        private Double magnitude;
        private Double direction;
    }
    
    @lombok.Data
    public static class MovingObject {
        private BoundingBox bbox;
        private Double speed;
        private String direction;
    }
}