package com.im.backend.controller;

import com.im.backend.entity.MultimodalConfigEntity;
import com.im.backend.entity.MultimodalAnalysisResultEntity;
import com.im.backend.service.MultimodalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 多模态内容理解引擎 REST API 控制器
 */
@RestController
@RequestMapping("/api/multimodal")
@Slf4j
@RequiredArgsConstructor
public class MultimodalController {
    
    private final MultimodalService multimodalService;
    
    // ==================== 配置管理 API ====================
    
    /**
     * 创建新的多模态配置
     */
    @PostMapping("/configs")
    public ResponseEntity<?> createConfig(@RequestBody MultimodalConfigEntity config) {
        try {
            MultimodalConfigEntity created = multimodalService.createConfig(config);
            return ResponseEntity.ok(ApiResponse.success("配置创建成功", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("创建配置失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("配置创建失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新配置
     */
    @PutMapping("/configs/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody MultimodalConfigEntity configUpdate) {
        try {
            MultimodalConfigEntity updated = multimodalService.updateConfig(id, configUpdate);
            return ResponseEntity.ok(ApiResponse.success("配置更新成功", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("更新配置失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("配置更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取配置
     */
    @GetMapping("/configs/{id}")
    public ResponseEntity<?> getConfigById(@PathVariable Long id) {
        try {
            MultimodalConfigEntity config = multimodalService.getConfigById(id);
            return ResponseEntity.ok(ApiResponse.success("获取配置成功", config));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("获取配置失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取配置失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据名称获取配置
     */
    @GetMapping("/configs/name/{name}")
    public ResponseEntity<?> getConfigByName(@PathVariable String name) {
        try {
            MultimodalConfigEntity config = multimodalService.getConfigByName(name);
            return ResponseEntity.ok(ApiResponse.success("获取配置成功", config));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("获取配置失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取配置失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有启用的配置
     */
    @GetMapping("/configs/enabled")
    public ResponseEntity<?> getAllEnabledConfigs() {
        try {
            List<MultimodalConfigEntity> configs = multimodalService.getAllEnabledConfigs();
            return ResponseEntity.ok(ApiResponse.success("获取启用的配置成功", configs));
        } catch (Exception e) {
            log.error("获取配置列表失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取配置列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取默认配置
     */
    @GetMapping("/configs/default")
    public ResponseEntity<?> getDefaultConfig() {
        try {
            MultimodalConfigEntity config = multimodalService.getDefaultConfig();
            return ResponseEntity.ok(ApiResponse.success("获取默认配置成功", config));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("获取默认配置失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取默认配置失败: " + e.getMessage()));
        }
    }
    
    /**
     * 禁用配置
     */
    @PostMapping("/configs/{id}/disable")
    public ResponseEntity<?> disableConfig(@PathVariable Long id) {
        try {
            multimodalService.disableConfig(id);
            return ResponseEntity.ok(ApiResponse.success("配置已禁用"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("禁用配置失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("禁用配置失败: " + e.getMessage()));
        }
    }
    
    /**
     * 启用配置
     */
    @PostMapping("/configs/{id}/enable")
    public ResponseEntity<?> enableConfig(@PathVariable Long id) {
        try {
            multimodalService.enableConfig(id);
            return ResponseEntity.ok(ApiResponse.success("配置已启用"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("启用配置失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("启用配置失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除配置
     */
    @DeleteMapping("/configs/{id}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long id) {
        try {
            multimodalService.deleteConfig(id);
            return ResponseEntity.ok(ApiResponse.success("配置已删除"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("删除配置失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("删除配置失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取配置统计信息
     */
    @GetMapping("/configs/stats")
    public ResponseEntity<?> getConfigStats() {
        try {
            MultimodalService.ConfigStats stats = multimodalService.getConfigStats();
            return ResponseEntity.ok(ApiResponse.success("获取配置统计成功", stats));
        } catch (Exception e) {
            log.error("获取配置统计失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取配置统计失败: " + e.getMessage()));
        }
    }
    
    // ==================== 分析结果 API ====================
    
    /**
     * 提交分析请求
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody MultimodalService.AnalysisRequest request) {
        try {
            MultimodalAnalysisResultEntity result = multimodalService.createAnalysisRequest(request);
            return ResponseEntity.ok(ApiResponse.success("分析请求已提交", Map.of(
                "requestId", result.getRequestId(),
                "status", result.getAnalysisStatus(),
                "estimatedTime", result.getPriority() < 3 ? "高优先级，立即处理" : "标准优先级，排队处理"
            )));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("提交分析请求失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("提交分析请求失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取分析结果状态
     */
    @GetMapping("/results/{requestId}/status")
    public ResponseEntity<?> getAnalysisStatus(@PathVariable String requestId) {
        try {
            MultimodalAnalysisResultEntity result = multimodalService.getResultByRequestId(requestId);
            Map<String, Object> status = new HashMap<>();
            status.put("requestId", result.getRequestId());
            status.put("status", result.getAnalysisStatus());
            status.put("createdAt", result.getCreatedAt());
            status.put("updatedAt", result.getUpdatedAt());
            
            if (result.isCompleted()) {
                status.put("completedAt", result.getCompletedAt());
                status.put("confidenceScore", result.getConfidenceScore());
                status.put("processingTimeMs", result.getProcessingTimeMs());
                status.put("hasSummary", result.getMultimodalSummary() != null);
            }
            
            if (result.isFailed()) {
                status.put("errorMessage", result.getErrorMessage());
                status.put("retryCount", result.getRetryCount());
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取分析状态成功", status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("获取分析状态失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取分析状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取分析结果详情
     */
    @GetMapping("/results/{requestId}")
    public ResponseEntity<?> getAnalysisResult(@PathVariable String requestId) {
        try {
            MultimodalAnalysisResultEntity result = multimodalService.getResultByRequestId(requestId);
            
            if (!result.isCompleted()) {
                return ResponseEntity.accepted().body(ApiResponse.success("分析仍在处理中", Map.of(
                    "status", result.getAnalysisStatus(),
                    "message", result.isProcessing() ? "正在处理中" : "排队等待中"
                )));
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取分析结果成功", buildResultResponse(result)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("获取分析结果失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取分析结果失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据消息ID获取分析结果
     */
    @GetMapping("/results/by-message/{messageId}")
    public ResponseEntity<?> getAnalysisResultByMessageId(@PathVariable Long messageId) {
        try {
            java.util.Optional<MultimodalAnalysisResultEntity> resultOpt = multimodalService.getResultByMessageId(messageId);
            
            if (resultOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("未找到该消息的分析结果", null));
            }
            
            MultimodalAnalysisResultEntity result = resultOpt.get();
            if (!result.isCompleted()) {
                return ResponseEntity.accepted().body(ApiResponse.success("分析仍在处理中", Map.of(
                    "status", result.getAnalysisStatus(),
                    "requestId", result.getRequestId()
                )));
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取分析结果成功", buildResultResponse(result)));
        } catch (Exception e) {
            log.error("获取消息分析结果失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取消息分析结果失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户分析历史
     */
    @GetMapping("/users/{userId}/history")
    public ResponseEntity<?> getUserAnalysisHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<MultimodalAnalysisResultEntity> history = multimodalService.getUserAnalysisHistory(userId, limit);
            List<Map<String, Object>> response = history.stream()
                .map(this::buildHistoryItem)
                .toList();
            return ResponseEntity.ok(ApiResponse.success("获取用户分析历史成功", response));
        } catch (Exception e) {
            log.error("获取用户分析历史失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取用户分析历史失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取会话分析历史
     */
    @GetMapping("/sessions/{sessionId}/history")
    public ResponseEntity<?> getSessionAnalysisHistory(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<MultimodalAnalysisResultEntity> history = multimodalService.getSessionAnalysisHistory(sessionId, limit);
            List<Map<String, Object>> response = history.stream()
                .map(this::buildHistoryItem)
                .toList();
            return ResponseEntity.ok(ApiResponse.success("获取会话分析历史成功", response));
        } catch (Exception e) {
            log.error("获取会话分析历史失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取会话分析历史失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取分析结果统计
     */
    @GetMapping("/results/stats")
    public ResponseEntity<?> getResultStats() {
        try {
            MultimodalService.ResultStats stats = multimodalService.getResultStats();
            return ResponseEntity.ok(ApiResponse.success("获取结果统计成功", stats));
        } catch (Exception e) {
            log.error("获取结果统计失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取结果统计失败: " + e.getMessage()));
        }
    }
    
    /**
     * 批量查询分析结果
     */
    @GetMapping("/results")
    public ResponseEntity<?> searchResults(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 这里需要实现分页查询逻辑
            // 暂时返回空结果
            return ResponseEntity.ok(ApiResponse.success("搜索分析结果", Map.of(
                "page", page,
                "size", size,
                "total", 0,
                "results", List.of()
            )));
        } catch (Exception e) {
            log.error("搜索分析结果失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("搜索分析结果失败: " + e.getMessage()));
        }
    }
    
    // ==================== 系统管理 API ====================
    
    /**
     * 清理过期缓存
     */
    @PostMapping("/admin/cleanup-cache")
    public ResponseEntity<?> cleanupExpiredCache() {
        try {
            int cleaned = multimodalService.cleanupExpiredCache();
            return ResponseEntity.ok(ApiResponse.success("缓存清理完成", Map.of(
                "cleanedCount", cleaned,
                "timestamp", LocalDateTime.now()
            )));
        } catch (Exception e) {
            log.error("清理缓存失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("清理缓存失败: " + e.getMessage()));
        }
    }
    
    /**
     * 重试失败请求
     */
    @PostMapping("/admin/retry-failed")
    public ResponseEntity<?> retryFailedRequests() {
        try {
            List<MultimodalAnalysisResultEntity> retried = multimodalService.retryFailedRequests();
            return ResponseEntity.ok(ApiResponse.success("重试失败请求完成", Map.of(
                "retriedCount", retried.size(),
                "requests", retried.stream().map(r -> Map.of(
                    "requestId", r.getRequestId(),
                    "retryCount", r.getRetryCount()
                )).toList()
            )));
        } catch (Exception e) {
            log.error("重试失败请求失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("重试失败请求失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取情感分布
     */
    @GetMapping("/stats/sentiment-distribution")
    public ResponseEntity<?> getSentimentDistribution() {
        try {
            List<MultimodalService.SentimentDistribution> distribution = multimodalService.getSentimentDistribution();
            return ResponseEntity.ok(ApiResponse.success("获取情感分布成功", distribution));
        } catch (Exception e) {
            log.error("获取情感分布失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取情感分布失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取意图分布
     */
    @GetMapping("/stats/intent-distribution")
    public ResponseEntity<?> getIntentDistribution() {
        try {
            List<MultimodalService.IntentDistribution> distribution = multimodalService.getIntentDistribution();
            return ResponseEntity.ok(ApiResponse.success("获取意图分布成功", distribution));
        } catch (Exception e) {
            log.error("获取意图分布失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取意图分布失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取场景分布
     */
    @GetMapping("/stats/scene-distribution")
    public ResponseEntity<?> getSceneDistribution() {
        try {
            List<MultimodalService.SceneDistribution> distribution = multimodalService.getSceneDistribution();
            return ResponseEntity.ok(ApiResponse.success("获取场景分布成功", distribution));
        } catch (Exception e) {
            log.error("获取场景分布失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取场景分布失败: " + e.getMessage()));
        }
    }
    
    /**
     * 系统健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            MultimodalService.ConfigStats configStats = multimodalService.getConfigStats();
            MultimodalService.ResultStats resultStats = multimodalService.getResultStats();
            
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now());
            health.put("configStats", configStats);
            health.put("resultStats", resultStats);
            health.put("message", "多模态内容理解引擎运行正常");
            
            return ResponseEntity.ok(ApiResponse.success("系统健康检查通过", health));
        } catch (Exception e) {
            log.error("系统健康检查失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("系统健康检查失败: " + e.getMessage()));
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private Map<String, Object> buildResultResponse(MultimodalAnalysisResultEntity result) {
        Map<String, Object> response = new HashMap<>();
        
        // 基本信息
        response.put("requestId", result.getRequestId());
        response.put("sessionId", result.getSessionId());
        response.put("userId", result.getUserId());
        response.put("messageId", result.getMessageId());
        response.put("contentType", result.getContentType());
        response.put("analysisStatus", result.getAnalysisStatus());
        response.put("createdAt", result.getCreatedAt());
        response.put("completedAt", result.getCompletedAt());
        response.put("processingTimeMs", result.getProcessingTimeMs());
        response.put("confidenceScore", result.getConfidenceScore());
        response.put("qualityRating", result.getQualityRating());
        response.put("modelUsed", result.getModelUsed());
        response.put("costUnits", result.getCostUnits());
        
        // 文本分析结果
        if (result.getTextSummary() != null) {
            Map<String, Object> textResult = new HashMap<>();
            textResult.put("summary", result.getTextSummary());
            textResult.put("keywords", result.getTextKeywords() != null ? List.of(result.getTextKeywords().split(",")) : List.of());
            textResult.put("sentiment", result.getTextSentiment());
            textResult.put("sentimentScore", result.getTextSentimentScore());
            textResult.put("intent", result.getTextIntent());
            response.put("textAnalysis", textResult);
        }
        
        // 图像分析结果
        if (result.getImageDescription() != null) {
            Map<String, Object> imageResult = new HashMap<>();
            imageResult.put("description", result.getImageDescription());
            imageResult.put("tags", result.getImageTags() != null ? List.of(result.getImageTags().split(",")) : List.of());
            imageResult.put("scene", result.getImageScene());
            response.put("imageAnalysis", imageResult);
        }
        
        // 音频分析结果
        if (result.getAudioTranscription() != null) {
            Map<String, Object> audioResult = new HashMap<>();
            audioResult.put("transcription", result.getAudioTranscription());
            audioResult.put("emotion", result.getAudioEmotion());
            audioResult.put("emotionScore", result.getAudioEmotionScore());
            audioResult.put("speakers", result.getAudioSpeakers());
            audioResult.put("keywords", result.getAudioKeywords());
            response.put("audioAnalysis", audioResult);
        }
        
        // 视频分析结果
        if (result.getVideoDescription() != null) {
            Map<String, Object> videoResult = new HashMap<>();
            videoResult.put("description", result.getVideoDescription());
            response.put("videoAnalysis", videoResult);
        }
        
        // 多模态融合结果
        if (result.getMultimodalSummary() != null) {
            Map<String, Object> fusionResult = new HashMap<>();
            fusionResult.put("summary", result.getMultimodalSummary());
            fusionResult.put("tags", result.getMultimodalTags() != null ? List.of(result.getMultimodalTags().split(",")) : List.of());
            fusionResult.put("sentiment", result.getMultimodalSentiment());
            fusionResult.put("sentimentScore", result.getMultimodalSentimentScore());
            fusionResult.put("intent", result.getMultimodalIntent());
            response.put("multimodalFusion", fusionResult);
        }
        
        return response;
    }
    
    private Map<String, Object> buildHistoryItem(MultimodalAnalysisResultEntity result) {
        Map<String, Object> item = new HashMap<>();
        item.put("requestId", result.getRequestId());
        item.put("contentType", result.getContentType());
        item.put("analysisStatus", result.getAnalysisStatus());
        item.put("createdAt", result.getCreatedAt());
        item.put("completedAt", result.getCompletedAt());
        item.put("confidenceScore", result.getConfidenceScore());
        item.put("processingTimeMs", result.getProcessingTimeMs());
        
        if (result.getMultimodalSummary() != null && result.getMultimodalSummary().length() > 100) {
            item.put("summary", result.getMultimodalSummary().substring(0, 100) + "...");
        } else if (result.getMultimodalSummary() != null) {
            item.put("summary", result.getMultimodalSummary());
        } else if (result.getTextSummary() != null && result.getTextSummary().length() > 100) {
            item.put("summary", result.getTextSummary().substring(0, 100) + "...");
        } else if (result.getTextSummary() != null) {
            item.put("summary", result.getTextSummary());
        }
        
        return item;
    }
    
    // ==================== 响应类 ====================
    
    @lombok.Data
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private long timestamp;
        
        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
        
        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }
        
        public static <T> ApiResponse<T> success(String message) {
            return success(message, null);
        }
        
        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }
    }
}