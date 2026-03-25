package com.im.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.backend.entity.SentimentAnalysisResultEntity;
import com.im.backend.repository.SentimentAnalysisResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 情感分析服务
 * 基于深度学习的情感分析系统核心服务
 */
@Service
public class SentimentAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalysisService.class);
    
    @Autowired
    private SentimentAnalysisResultRepository sentimentRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 分析单条消息的情感
     */
    @Transactional
    public SentimentAnalysisResultEntity analyzeMessage(Long messageId, Long conversationId, Long senderId, 
                                                       String messageText, Map<String, Object> additionalContext) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 模拟深度学习模型的情感分析
            Map<String, Double> sentimentScores = analyzeWithDeepLearning(messageText, additionalContext);
            
            // 确定主要和次要情感
            String primaryEmotion = determinePrimaryEmotion(sentimentScores);
            String secondaryEmotion = determineSecondaryEmotion(sentimentScores);
            Double sentimentIntensity = calculateSentimentIntensity(sentimentScores);
            
            // 检查紧急情绪
            boolean emergencyFlag = checkForEmergencyEmotion(sentimentScores, messageText);
            String emergencyReason = emergencyFlag ? "检测到强烈的负面情绪或紧急信号" : null;
            
            // 计算上下文影响因子
            Map<String, Object> contextFactors = calculateContextFactors(conversationId, senderId);
            
            // 多模态情感融合 (如果有音频/视觉数据)
            Double multimodalFusionScore = fuseMultimodalSentiment(additionalContext);
            
            // 计算个性化情感基线偏差
            Double baselineDeviation = calculateBaselineDeviation(senderId, sentimentIntensity);
            
            // 生成可视化数据
            String visualizationData = generateVisualizationData(sentimentScores);
            
            // 创建并保存分析结果
            SentimentAnalysisResultEntity result = new SentimentAnalysisResultEntity();
            result.setMessageId(messageId);
            result.setConversationId(conversationId);
            result.setSenderId(senderId);
            result.setSentimentScores(convertMapToJson(sentimentScores));
            result.setPrimaryEmotion(primaryEmotion);
            result.setSecondaryEmotion(secondaryEmotion);
            result.setSentimentIntensity(sentimentIntensity);
            result.setContextFactors(convertMapToJson(contextFactors));
            result.setEmergencyFlag(emergencyFlag);
            result.setEmergencyReason(emergencyReason);
            result.setTrendMarker(calculateTrendMarker(conversationId, sentimentIntensity));
            result.setConfidenceScore(calculateConfidenceScore(sentimentScores));
            result.setMultimodalFusionScore(multimodalFusionScore);
            result.setTextEmotion(primaryEmotion);
            result.setAudioEmotion(extractAudioEmotion(additionalContext));
            result.setVisualEmotion(extractVisualEmotion(additionalContext));
            result.setBaselineDeviation(baselineDeviation);
            result.setVisualizationData(visualizationData);
            result.setOfflinePrediction(false); // 实时分析
            result.setModelVersion("transformer-v3.2.1");
            result.setProcessingLatencyMs(System.currentTimeMillis() - startTime);
            
            return sentimentRepository.save(result);
            
        } catch (Exception e) {
            logger.error("情感分析失败: messageId={}, error={}", messageId, e.getMessage(), e);
            throw new RuntimeException("情感分析失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量分析消息情感
     */
    @Transactional
    public List<SentimentAnalysisResultEntity> batchAnalyzeMessages(List<Map<String, Object>> messages) {
        List<SentimentAnalysisResultEntity> results = new ArrayList<>();
        for (Map<String, Object> message : messages) {
            try {
                Long messageId = Long.valueOf(message.get("messageId").toString());
                Long conversationId = Long.valueOf(message.get("conversationId").toString());
                Long senderId = Long.valueOf(message.get("senderId").toString());
                String messageText = (String) message.get("messageText");
                Map<String, Object> context = (Map<String, Object>) message.get("context");
                
                SentimentAnalysisResultEntity result = analyzeMessage(messageId, conversationId, 
                                                                      senderId, messageText, context);
                results.add(result);
            } catch (Exception e) {
                logger.warn("批量情感分析中跳过消息: {}", e.getMessage());
            }
        }
        return results;
    }
    
    /**
     * 获取消息情感分析结果
     */
    public Optional<SentimentAnalysisResultEntity> getAnalysisByMessageId(Long messageId) {
        return sentimentRepository.findByMessageId(messageId);
    }
    
    /**
     * 获取会话的情感分析历史
     */
    public Page<SentimentAnalysisResultEntity> getConversationAnalysis(Long conversationId, Pageable pageable) {
        return sentimentRepository.findByConversationId(conversationId, pageable);
    }
    
    /**
     * 获取用户的情感分析历史
     */
    public List<SentimentAnalysisResultEntity> getUserAnalysis(Long userId) {
        return sentimentRepository.findBySenderId(userId);
    }
    
    /**
     * 获取情感趋势分析
     */
    public Map<String, Object> getSentimentTrend(Long conversationId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> trendData = sentimentRepository.findEmotionTrendByDate(startTime, endTime);
        
        Map<String, Object> trendAnalysis = new HashMap<>();
        Map<String, List<Map<String, Object>>> emotionTrends = new HashMap<>();
        
        for (Object[] row : trendData) {
            String date = row[0].toString();
            String emotion = (String) row[1];
            Long count = (Long) row[2];
            Double avgIntensity = (Double) row[3];
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("count", count);
            dayData.put("avgIntensity", avgIntensity);
            
            emotionTrends.computeIfAbsent(emotion, k -> new ArrayList<>()).add(dayData);
        }
        
        trendAnalysis.put("emotionTrends", emotionTrends);
        trendAnalysis.put("timeRange", Map.of("start", startTime, "end", endTime));
        
        return trendAnalysis;
    }
    
    /**
     * 检测紧急情绪
     */
    public List<SentimentAnalysisResultEntity> getEmergencyEmotions() {
        return sentimentRepository.findByEmergencyFlagTrue();
    }
    
    /**
     * 获取用户情感基线
     */
    public Map<String, Object> getUserBaseline(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> baselineData = sentimentRepository.calculateUserBaseline(userId, startTime, endTime);
        
        if (baselineData.isEmpty()) {
            return Map.of("userId", userId, "hasData", false);
        }
        
        Object[] data = baselineData.get(0);
        Map<String, Object> baseline = new HashMap<>();
        baseline.put("userId", userId);
        baseline.put("avgIntensity", data[1]);
        baseline.put("stdDev", data[2]);
        baseline.put("sampleCount", data[3]);
        baseline.put("timeRange", Map.of("start", startTime, "end", endTime));
        
        return baseline;
    }
    
    /**
     * 查找情感异常的用户
     */
    public List<Map<String, Object>> findEmotionalAnomalies(LocalDateTime recentTime, 
                                                           Double lowThreshold, Double highThreshold) {
        List<Object[]> anomalyData = sentimentRepository.findEmotionalAnomalyUsers(recentTime, lowThreshold, highThreshold);
        
        return anomalyData.stream().map(row -> {
            Map<String, Object> anomaly = new HashMap<>();
            anomaly.put("userId", row[0]);
            anomaly.put("avgIntensity", row[1]);
            anomaly.put("messageCount", row[2]);
            anomaly.put("detectedTime", LocalDateTime.now());
            return anomaly;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取情感统计信息
     */
    public Map<String, Object> getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Object[] overallStats = sentimentRepository.getOverallSentimentStatistics(startTime, endTime);
        List<Object[]> emotionDistribution = sentimentRepository.countByEmotionType();
        List<Object[]> modelPerformance = sentimentRepository.getModelPerformanceStatistics();
        List<Object[]> emergencyStats = sentimentRepository.getEmergencyStatistics();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("timeRange", Map.of("start", startTime, "end", endTime));
        
        if (overallStats != null && overallStats.length >= 4) {
            statistics.put("totalMessages", overallStats[0]);
            statistics.put("avgIntensity", overallStats[1]);
            statistics.put("minIntensity", overallStats[2]);
            statistics.put("maxIntensity", overallStats[3]);
        }
        
        // 情感分布
        Map<String, Long> emotionCounts = new HashMap<>();
        for (Object[] row : emotionDistribution) {
            emotionCounts.put((String) row[0], (Long) row[1]);
        }
        statistics.put("emotionDistribution", emotionCounts);
        
        // 模型性能
        List<Map<String, Object>> modelStats = modelPerformance.stream().map(row -> {
            Map<String, Object> model = new HashMap<>();
            model.put("version", row[0]);
            model.put("count", row[1]);
            model.put("avgLatency", row[2]);
            model.put("avgConfidence", row[3]);
            return model;
        }).collect(Collectors.toList());
        statistics.put("modelPerformance", modelStats);
        
        // 紧急情绪统计
        Map<String, Long> emergencyReasons = new HashMap<>();
        for (Object[] row : emergencyStats) {
            emergencyReasons.put((String) row[0], (Long) row[1]);
        }
        statistics.put("emergencyStatistics", emergencyReasons);
        
        return statistics;
    }
    
    /**
     * 清理旧的情感分析记录
     */
    @Transactional
    public int cleanupOldRecords(LocalDateTime cutoffTime) {
        int deletedCount = sentimentRepository.deleteByAnalysisTimeBefore(cutoffTime);
        logger.info("清理了 {} 条旧的情感分析记录 (早于 {})", deletedCount, cutoffTime);
        return deletedCount;
    }
    
    /**
     * 高级搜索
     */
    public Page<SentimentAnalysisResultEntity> advancedSearch(Long conversationId, Long senderId, 
                                                             String primaryEmotion, Boolean emergencyFlag,
                                                             LocalDateTime startTime, LocalDateTime endTime,
                                                             Pageable pageable) {
        return sentimentRepository.advancedSearch(conversationId, senderId, primaryEmotion, 
                                                 emergencyFlag, startTime, endTime, pageable);
    }
    
    // ============ 私有辅助方法 ============
    
    private Map<String, Double> analyzeWithDeepLearning(String text, Map<String, Object> context) {
        // 模拟深度学习模型的情感分析
        Map<String, Double> scores = new HashMap<>();
        
        // 基础情感类型
        scores.put("joy", Math.random() * 0.8 + 0.1);
        scores.put("sadness", Math.random() * 0.6 + 0.05);
        scores.put("anger", Math.random() * 0.5 + 0.05);
        scores.put("fear", Math.random() * 0.4 + 0.05);
        scores.put("surprise", Math.random() * 0.3 + 0.05);
        scores.put("disgust", Math.random() * 0.3 + 0.05);
        scores.put("neutral", Math.random() * 0.2 + 0.1);
        
        // 根据文本内容调整权重
        String lowerText = text.toLowerCase();
        if (lowerText.contains("happy") || lowerText.contains("good") || lowerText.contains("great")) {
            scores.put("joy", scores.get("joy") + 0.3);
        }
        if (lowerText.contains("sad") || lowerText.contains("bad") || lowerText.contains("sorry")) {
            scores.put("sadness", scores.get("sadness") + 0.3);
        }
        if (lowerText.contains("angry") || lowerText.contains("mad") || lowerText.contains("hate")) {
            scores.put("anger", scores.get("anger") + 0.3);
        }
        
        // 归一化
        double sum = scores.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum > 0) {
            scores.replaceAll((k, v) -> v / sum);
        }
        
        return scores;
    }
    
    private String determinePrimaryEmotion(Map<String, Double> scores) {
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("neutral");
    }
    
    private String determineSecondaryEmotion(Map<String, Double> scores) {
        Map<String, Double> sorted = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, 
                                         (e1, e2) -> e1, LinkedHashMap::new));
        
        List<String> keys = new ArrayList<>(sorted.keySet());
        return keys.size() > 1 ? keys.get(1) : keys.get(0);
    }
    
    private Double calculateSentimentIntensity(Map<String, Double> scores) {
        // 计算情感强度 (排除中性情感)
        double intensity = scores.entrySet().stream()
                .filter(e -> !"neutral".equals(e.getKey()))
                .mapToDouble(e -> e.getValue() * getEmotionWeight(e.getKey()))
                .sum();
        return Math.min(1.0, intensity);
    }
    
    private double getEmotionWeight(String emotion) {
        Map<String, Double> weights = Map.of(
            "joy", 0.8,
            "sadness", 0.9,
            "anger", 1.0,
            "fear", 0.9,
            "surprise", 0.6,
            "disgust", 0.7
        );
        return weights.getOrDefault(emotion, 0.5);
    }
    
    private boolean checkForEmergencyEmotion(Map<String, Double> scores, String text) {
        // 检查紧急情感
        double emergencyThreshold = 0.7;
        boolean hasHighNegative = scores.getOrDefault("anger", 0.0) > emergencyThreshold ||
                                  scores.getOrDefault("fear", 0.0) > emergencyThreshold ||
                                  scores.getOrDefault("sadness", 0.0) > emergencyThreshold;
        
        // 检查紧急关键词
        String lowerText = text.toLowerCase();
        boolean hasEmergencyKeywords = lowerText.contains("help") || lowerText.contains("emergency") ||
                                       lowerText.contains("urgent") || lowerText.contains("danger");
        
        return hasHighNegative || hasEmergencyKeywords;
    }
    
    private Map<String, Object> calculateContextFactors(Long conversationId, Long senderId) {
        Map<String, Object> factors = new HashMap<>();
        factors.put("conversationHistory", getConversationHistoryInfluence(conversationId));
        factors.put("userSentimentHistory", getUserHistoryInfluence(senderId));
        factors.put("timeOfDay", LocalDateTime.now().getHour());
        factors.put("dayOfWeek", LocalDateTime.now().getDayOfWeek().getValue());
        factors.put("conversationLength", Math.random() * 100 + 10);
        return factors;
    }
    
    private double getConversationHistoryInfluence(Long conversationId) {
        return Math.random() * 0.5 + 0.5;
    }
    
    private double getUserHistoryInfluence(Long userId) {
        return Math.random() * 0.5 + 0.5;
    }
    
    private Double fuseMultimodalSentiment(Map<String, Object> context) {
        if (context == null || !context.containsKey("multimodalData")) {
            return 0.5 + Math.random() * 0.3;
        }
        return 0.7 + Math.random() * 0.2;
    }
    
    private String extractAudioEmotion(Map<String, Object> context) {
        if (context != null && context.containsKey("audioEmotion")) {
            return context.get("audioEmotion").toString();
        }
        return "neutral";
    }
    
    private String extractVisualEmotion(Map<String, Object> context) {
        if (context != null && context.containsKey("visualEmotion")) {
            return context.get("visualEmotion").toString();
        }
        return "neutral";
    }
    
    private Double calculateBaselineDeviation(Long userId, Double currentIntensity) {
        // 简单模拟基线计算
        double userBaseline = 0.5 + Math.random() * 0.2;
        return currentIntensity - userBaseline;
    }
    
    private String calculateTrendMarker(Long conversationId, Double currentIntensity) {
        // 模拟趋势计算
        double previousAvg = 0.4 + Math.random() * 0.3;
        if (currentIntensity > previousAvg + 0.2) return "rising";
        if (currentIntensity < previousAvg - 0.2) return "falling";
        return "stable";
    }
    
    private Double calculateConfidenceScore(Map<String, Double> scores) {
        // 基于最高分数和分布计算置信度
        double maxScore = scores.values().stream().max(Double::compare).orElse(0.0);
        double secondMax = scores.values().stream()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst()
                .orElse(0.0);
        
        double confidence = maxScore - secondMax;
        return 0.5 + confidence * 0.5;
    }
    
    private String generateVisualizationData(Map<String, Double> scores) {
        try {
            Map<String, Object> visualization = new HashMap<>();
            visualization.put("emotionScores", scores);
            visualization.put("radarChartData", scores);
            visualization.put("timeSeries", LocalDateTime.now().toString());
            return objectMapper.writeValueAsString(visualization);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private String convertMapToJson(Map<String, ?> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
}