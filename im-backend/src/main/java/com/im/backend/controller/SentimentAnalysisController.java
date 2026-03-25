package com.im.backend.controller;

import com.im.backend.entity.SentimentAnalysisResultEntity;
import com.im.backend.service.SentimentAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 情感分析 REST API 控制器
 * 基于深度学习的情感分析系统 API
 */
@RestController
@RequestMapping("/api/v1/sentiment")
@Tag(name = "Sentiment Analysis", description = "基于深度学习的情感分析系统 API")
public class SentimentAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalysisController.class);
    
    @Autowired
    private SentimentAnalysisService sentimentService;
    
    @PostMapping("/analyze")
    @Operation(summary = "分析单条消息的情感", description = "使用深度学习模型分析消息的情感")
    public ResponseEntity<Map<String, Object>> analyzeMessage(
            @RequestParam Long messageId,
            @RequestParam Long conversationId,
            @RequestParam Long senderId,
            @RequestParam String messageText,
            @RequestBody(required = false) Map<String, Object> context) {
        
        try {
            logger.info("分析消息情感: messageId={}, conversationId={}, senderId={}", 
                       messageId, conversationId, senderId);
            
            SentimentAnalysisResultEntity result = sentimentService.analyzeMessage(
                messageId, conversationId, senderId, messageText, context);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("analysisId", result.getId());
            response.put("primaryEmotion", result.getPrimaryEmotion());
            response.put("sentimentIntensity", result.getSentimentIntensity());
            response.put("emergencyFlag", result.getEmergencyFlag());
            response.put("processingLatency", result.getProcessingLatencyMs());
            response.put("analysisTime", result.getAnalysisTime());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("情感分析失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @PostMapping("/batch-analyze")
    @Operation(summary = "批量分析消息情感", description = "批量分析多条消息的情感")
    public ResponseEntity<Map<String, Object>> batchAnalyzeMessages(@RequestBody List<Map<String, Object>> messages) {
        try {
            logger.info("批量分析 {} 条消息的情感", messages.size());
            
            List<SentimentAnalysisResultEntity> results = sentimentService.batchAnalyzeMessages(messages);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("analyzedCount", results.size());
            response.put("analysisIds", results.stream().map(SentimentAnalysisResultEntity::getId).toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量情感分析失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/message/{messageId}")
    @Operation(summary = "获取消息情感分析结果", description = "根据消息ID获取情感分析结果")
    public ResponseEntity<Map<String, Object>> getAnalysisByMessageId(
            @PathVariable Long messageId) {
        
        try {
            Optional<SentimentAnalysisResultEntity> result = sentimentService.getAnalysisByMessageId(messageId);
            
            if (result.isPresent()) {
                SentimentAnalysisResultEntity analysis = result.get();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("analysis", convertToMap(analysis));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "未找到情感分析结果"));
            }
            
        } catch (Exception e) {
            logger.error("获取情感分析结果失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/conversation/{conversationId}")
    @Operation(summary = "获取会话情感分析历史", description = "获取指定会话的情感分析历史记录")
    public ResponseEntity<Map<String, Object>> getConversationAnalysis(
            @PathVariable Long conversationId,
            @Parameter(description = "分页参数") Pageable pageable) {
        
        try {
            Page<SentimentAnalysisResultEntity> page = sentimentService.getConversationAnalysis(conversationId, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("pageNumber", page.getNumber());
            response.put("pageSize", page.getSize());
            response.put("totalPages", page.getTotalPages());
            response.put("totalElements", page.getTotalElements());
            response.put("analyses", page.getContent().stream()
                    .map(this::convertToMap)
                    .toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取会话情感分析历史失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户情感分析历史", description = "获取指定用户的情感分析历史记录")
    public ResponseEntity<Map<String, Object>> getUserAnalysis(@PathVariable Long userId) {
        try {
            List<SentimentAnalysisResultEntity> analyses = sentimentService.getUserAnalysis(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("analysisCount", analyses.size());
            response.put("analyses", analyses.stream()
                    .map(this::convertToMap)
                    .toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取用户情感分析历史失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/trend/{conversationId}")
    @Operation(summary = "获取情感趋势分析", description = "获取指定会话的情感趋势分析")
    public ResponseEntity<Map<String, Object>> getSentimentTrend(
            @PathVariable Long conversationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            Map<String, Object> trendAnalysis = sentimentService.getSentimentTrend(conversationId, startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("conversationId", conversationId);
            response.put("timeRange", Map.of("start", startTime, "end", endTime));
            response.put("trendAnalysis", trendAnalysis);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取情感趋势分析失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/emergency")
    @Operation(summary = "获取紧急情绪检测", description = "获取检测到的紧急情绪记录")
    public ResponseEntity<Map<String, Object>> getEmergencyEmotions() {
        try {
            List<SentimentAnalysisResultEntity> emergencies = sentimentService.getEmergencyEmotions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("emergencyCount", emergencies.size());
            response.put("emergencies", emergencies.stream()
                    .map(this::convertToMap)
                    .toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取紧急情绪检测失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/baseline/{userId}")
    @Operation(summary = "获取用户情感基线", description = "获取指定用户的情感基线数据")
    public ResponseEntity<Map<String, Object>> getUserBaseline(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            if (startTime == null) {
                startTime = LocalDateTime.now().minusDays(30);
            }
            if (endTime == null) {
                endTime = LocalDateTime.now();
            }
            
            Map<String, Object> baseline = sentimentService.getUserBaseline(userId, startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("timeRange", Map.of("start", startTime, "end", endTime));
            response.put("baseline", baseline);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取用户情感基线失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/anomalies")
    @Operation(summary = "查找情感异常用户", description = "查找情感强度异常的用户")
    public ResponseEntity<Map<String, Object>> findEmotionalAnomalies(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime recentTime,
            @RequestParam(required = false) Double lowThreshold,
            @RequestParam(required = false) Double highThreshold) {
        
        try {
            if (recentTime == null) {
                recentTime = LocalDateTime.now().minusHours(24);
            }
            if (lowThreshold == null) {
                lowThreshold = 0.2;
            }
            if (highThreshold == null) {
                highThreshold = 0.8;
            }
            
            List<Map<String, Object>> anomalies = sentimentService.findEmotionalAnomalies(
                recentTime, lowThreshold, highThreshold);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("anomalyCount", anomalies.size());
            response.put("anomalies", anomalies);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查找情感异常用户失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取情感统计信息", description = "获取情感分析系统的统计信息")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            if (startTime == null) {
                startTime = LocalDateTime.now().minusDays(7);
            }
            if (endTime == null) {
                endTime = LocalDateTime.now();
            }
            
            Map<String, Object> statistics = sentimentService.getStatistics(startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取情感统计信息失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/cleanup")
    @Operation(summary = "清理旧记录", description = "清理指定时间前的旧情感分析记录")
    public ResponseEntity<Map<String, Object>> cleanupOldRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cutoffTime) {
        
        try {
            int deletedCount = sentimentService.cleanupOldRecords(cutoffTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deletedCount", deletedCount);
            response.put("cutoffTime", cutoffTime);
            response.put("message", String.format("成功清理了 %d 条旧记录", deletedCount));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("清理旧记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "高级搜索", description = "高级搜索情感分析记录")
    public ResponseEntity<Map<String, Object>> advancedSearch(
            @RequestParam(required = false) Long conversationId,
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) String primaryEmotion,
            @RequestParam(required = false) Boolean emergencyFlag,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        
        try {
            Page<SentimentAnalysisResultEntity> page = sentimentService.advancedSearch(
                conversationId, senderId, primaryEmotion, emergencyFlag, startTime, endTime, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("searchCriteria", Map.of(
                "conversationId", conversationId,
                "senderId", senderId,
                "primaryEmotion", primaryEmotion,
                "emergencyFlag", emergencyFlag,
                "startTime", startTime,
                "endTime", endTime
            ));
            response.put("pageNumber", page.getNumber());
            response.put("pageSize", page.getSize());
            response.put("totalPages", page.getTotalPages());
            response.put("totalElements", page.getTotalElements());
            response.put("results", page.getContent().stream()
                    .map(this::convertToMap)
                    .toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("高级搜索失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "情感分析系统健康检查")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            // 执行简单查询检查系统健康
            long totalCount = sentimentService.getStatistics(
                LocalDateTime.now().minusHours(1), LocalDateTime.now())
                .getOrDefault("totalMessages", 0L);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now());
            response.put("system", "Sentiment Analysis System");
            response.put("version", "1.0.0");
            response.put("recentAnalyses", totalCount);
            response.put("message", "情感分析系统运行正常");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("健康检查失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "DOWN", "error", e.getMessage()));
        }
    }
    
    // ============ 私有辅助方法 ============
    
    private Map<String, Object> convertToMap(SentimentAnalysisResultEntity analysis) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", analysis.getId());
        map.put("messageId", analysis.getMessageId());
        map.put("conversationId", analysis.getConversationId());
        map.put("senderId", analysis.getSenderId());
        map.put("analysisTime", analysis.getAnalysisTime());
        map.put("primaryEmotion", analysis.getPrimaryEmotion());
        map.put("secondaryEmotion", analysis.getSecondaryEmotion());
        map.put("sentimentIntensity", analysis.getSentimentIntensity());
        map.put("emergencyFlag", analysis.getEmergencyFlag());
        map.put("emergencyReason", analysis.getEmergencyReason());
        map.put("confidenceScore", analysis.getConfidenceScore());
        map.put("multimodalFusionScore", analysis.getMultimodalFusionScore());
        map.put("textEmotion", analysis.getTextEmotion());
        map.put("audioEmotion", analysis.getAudioEmotion());
        map.put("visualEmotion", analysis.getVisualEmotion());
        map.put("baselineDeviation", analysis.getBaselineDeviation());
        map.put("modelVersion", analysis.getModelVersion());
        map.put("processingLatencyMs", analysis.getProcessingLatencyMs());
        map.put("createdAt", analysis.getCreatedAt());
        map.put("updatedAt", analysis.getUpdatedAt());
        return map;
    }
}