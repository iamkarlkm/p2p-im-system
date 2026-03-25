package com.im.system.controller;

import com.im.system.entity.FederatedLearningModelEntity;
import com.im.system.entity.PrivacyPreservingRecommendationEntity;
import com.im.system.service.FederatedLearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联邦学习控制器
 * 提供 REST API 用于联邦学习模型管理和隐私保护推荐
 */
@RestController
@RequestMapping("/api/v1/federated-learning")
public class FederatedLearningController {

    private final FederatedLearningService federatedLearningService;

    @Autowired
    public FederatedLearningController(FederatedLearningService federatedLearningService) {
        this.federatedLearningService = federatedLearningService;
    }

    /**
     * 创建联邦学习模型
     */
    @PostMapping("/models")
    public ResponseEntity<Map<String, Object>> createModel(@RequestBody CreateModelRequest request) {
        try {
            FederatedLearningModelEntity model = federatedLearningService.createFederatedModel(
                request.getModelName(),
                request.getModelType(),
                request.getModelScope(),
                request.getConfig()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("modelId", model.getModelId());
            response.put("modelName", model.getModelName());
            response.put("modelType", model.getModelType());
            response.put("modelVersion", model.getModelVersion());
            response.put("createdAt", model.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * 获取模型状态
     */
    @GetMapping("/models/{modelId}/status")
    public ResponseEntity<Map<String, Object>> getModelStatus(@PathVariable String modelId) {
        FederatedLearningModelEntity model = federatedLearningService.getModelStatus(modelId);
        
        if (model == null) {
            return createErrorResponse("Model not found", HttpStatus.NOT_FOUND);
        }
        
        Map<String, Object> response = buildModelResponse(model);
        return ResponseEntity.ok(response);
    }

    /**
     * 生成个性化推荐
     */
    @PostMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> generateRecommendation(
            @RequestBody GenerateRecommendationRequest request) {
        
        try {
            PrivacyPreservingRecommendationEntity recommendation = 
                federatedLearningService.generateRecommendation(
                    request.getUserId(),
                    request.getSessionId(),
                    request.getRecommendationType(),
                    request.getContext(),
                    request.getOptions()
                );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("recommendationId", recommendation.getRecommendationId());
            response.put("status", recommendation.getStatus());
            response.put("createdAt", recommendation.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * 获取推荐状态
     */
    @GetMapping("/recommendations/{recommendationId}/status")
    public ResponseEntity<Map<String, Object>> getRecommendationStatus(
            @PathVariable String recommendationId) {
        
        PrivacyPreservingRecommendationEntity rec = 
            federatedLearningService.getRecommendationStatus(recommendationId);
        
        if (rec == null) {
            return createErrorResponse("Recommendation not found", HttpStatus.NOT_FOUND);
        }
        
        Map<String, Object> response = buildRecommendationResponse(rec);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户推荐历史
     */
    @GetMapping("/users/{userId}/recommendations")
    public ResponseEntity<Map<String, Object>> getUserRecommendations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<PrivacyPreservingRecommendationEntity> recommendations = 
            federatedLearningService.getUserRecommendations(userId, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("totalRecommendations", recommendations.size());
        response.put("recommendations", recommendations.stream()
            .map(this::buildRecommendationResponse)
            .toArray()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * 提交客户端梯度更新
     */
    @PostMapping("/models/{modelId}/gradients")
    public ResponseEntity<Map<String, Object>> submitGradient(
            @PathVariable String modelId,
            @RequestBody GradientUpdateRequest request) {
        
        try {
            federatedLearningService.submitClientGradient(
                modelId,
                request.getClientId(),
                request.getGradientUpdate(),
                request.getTrainingSamples()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gradient submitted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * 执行联邦聚合
     */
    @PostMapping("/models/{modelId}/aggregate")
    public ResponseEntity<Map<String, Object>> performAggregation(@PathVariable String modelId) {
        try {
            Map<String, Object> result = federatedLearningService.performFederatedAggregation(modelId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("aggregationResult", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * 记录用户反馈
     */
    @PostMapping("/recommendations/{recommendationId}/feedback")
    public ResponseEntity<Map<String, Object>> recordFeedback(
            @PathVariable String recommendationId,
            @RequestBody FeedbackRequest request) {
        
        try {
            federatedLearningService.recordUserFeedback(
                recommendationId,
                request.getInteractionType(),
                request.getFeedbackScore(),
                request.getDwellTimeSeconds()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Feedback recorded successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * 获取系统统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        Map<String, Object> stats = federatedLearningService.getSystemStatistics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("statistics", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 暂停模型训练
     */
    @PostMapping("/models/{modelId}/pause")
    public ResponseEntity<Map<String, Object>> pauseModel(@PathVariable String modelId) {
        boolean paused = federatedLearningService.pauseModelTraining(modelId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", paused);
        
        if (paused) {
            response.put("message", "Model training paused");
        } else {
            response.put("error", "Model not found or already paused");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 恢复模型训练
     */
    @PostMapping("/models/{modelId}/resume")
    public ResponseEntity<Map<String, Object>> resumeModel(@PathVariable String modelId) {
        boolean resumed = federatedLearningService.resumeModelTraining(modelId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", resumed);
        
        if (resumed) {
            response.put("message", "Model training resumed");
        } else {
            response.put("error", "Model not found or not paused");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 撤销用户同意
     */
    @PostMapping("/users/{userId}/withdraw-consent")
    public ResponseEntity<Map<String, Object>> withdrawConsent(@PathVariable String userId) {
        try {
            federatedLearningService.withdrawUserConsent(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User consent withdrawn successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * 清理过期数据
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpiredData(
            @RequestParam(defaultValue = "30") int daysToKeep) {
        
        federatedLearningService.cleanupExpiredData(daysToKeep);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", String.format("Cleaned up data older than %d days", daysToKeep));
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取隐私保护选项
     */
    @GetMapping("/privacy-options")
    public ResponseEntity<Map<String, Object>> getPrivacyOptions() {
        Map<String, Object> options = new HashMap<>();
        
        options.put("privacyLevels", new String[]{
            "MINIMAL", "BASIC", "STANDARD", "ENHANCED", "MAXIMUM"
        });
        
        options.put("anonymizationLevels", new String[]{
            "RAW", "PSEUDONYMIZED", "ANONYMIZED", "AGGREGATED", "DIFFERENTIALLY_PRIVATE"
        });
        
        options.put("differentialPrivacy", Map.of(
            "enabled", true,
            "defaultEpsilon", 1.0,
            "defaultDelta", 1e-5,
            "noiseMechanism", "GAUSSIAN"
        ));
        
        options.put("secureAggregation", Map.of(
            "enabled", true,
            "protocol", "SECOA",
            "minClients", 3
        ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("privacyOptions", options);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取聚合算法选项
     */
    @GetMapping("/aggregation-algorithms")
    public ResponseEntity<Map<String, Object>> getAggregationAlgorithms() {
        Map<String, Object> algorithms = new HashMap<>();
        
        algorithms.put("available", new String[]{
            "FedAvg", "FedProx", "SCAFFOLD", "FedAdam", "FedYogi", "FedNova"
        });
        
        algorithms.put("default", "FedAvg");
        
        algorithms.put("descriptions", Map.of(
            "FedAvg", "Federated Averaging - Standard aggregation",
            "FedProx", "FedProx - Handles system heterogeneity",
            "SCAFFOLD", "Variance reduction for faster convergence",
            "FedAdam", "Adam optimizer for server updates",
            "FedYogi", "Yogi optimizer for sparse gradients",
            "FedNova", "Normalized averaging for unbalanced data"
        ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("algorithms", algorithms);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> stats = federatedLearningService.getSystemStatistics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "federated-learning");
        response.put("timestamp", System.currentTimeMillis());
        response.put("activeModels", stats.get("activeModels"));
        response.put("totalRecommendations", stats.get("totalRecommendations"));
        
        return ResponseEntity.ok(response);
    }

    // ========== 辅助方法 ==========

    private Map<String, Object> buildModelResponse(FederatedLearningModelEntity model) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("modelId", model.getModelId());
        response.put("modelName", model.getModelName());
        response.put("modelType", model.getModelType());
        response.put("modelVersion", model.getModelVersion());
        response.put("modelScope", model.getModelScope());
        response.put("aggregationAlgorithm", model.getAggregationAlgorithm());
        response.put("privacyBudget", model.getPrivacyBudget());
        response.put("noiseScale", model.getNoiseScale());
        response.put("learningRate", model.getLearningRate());
        response.put("batchSize", model.getBatchSize());
        response.put("epochs", model.getEpochs());
        response.put("participatingClients", model.getParticipatingClients());
        response.put("minimumClients", model.getMinimumClients());
        response.put("aggregationRound", model.getAggregationRound());
        response.put("modelAccuracy", model.getModelAccuracy());
        response.put("modelLoss", model.getModelLoss());
        response.put("convergenceStatus", model.getConvergenceStatus());
        response.put("isActive", model.getIsActive());
        response.put("isEncrypted", model.getIsEncrypted());
        response.put("createdAt", model.getCreatedAt());
        response.put("updatedAt", model.getUpdatedAt());
        response.put("lastAggregationTime", model.getLastAggregationTime());
        response.put("nextAggregationTime", model.getNextAggregationTime());
        
        return response;
    }

    private Map<String, Object> buildRecommendationResponse(PrivacyPreservingRecommendationEntity rec) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("recommendationId", rec.getRecommendationId());
        response.put("userId", rec.getUserId());
        response.put("sessionId", rec.getSessionId());
        response.put("modelId", rec.getModelId());
        response.put("modelVersion", rec.getModelVersion());
        response.put("recommendationType", rec.getRecommendationType());
        response.put("recommendationContext", rec.getRecommendationContext());
        response.put("recommendedContentIds", rec.getRecommendedContentIds());
        response.put("recommendationScores", rec.getRecommendationScores());
        response.put("totalRecommendations", rec.getTotalRecommendations());
        response.put("userFeedbackScore", rec.getUserFeedbackScore());
        response.put("userInteractionType", rec.getUserInteractionType());
        response.put("clickThrough", rec.getClickThrough());
        response.put("dwellTimeSeconds", rec.getDwellTimeSeconds());
        response.put("privacyLevel", rec.getPrivacyLevel());
        response.put("differentialPrivacyEnabled", rec.getDifferentialPrivacyEnabled());
        response.put("privacyBudgetConsumed", rec.getPrivacyBudgetConsumed());
        response.put("status", rec.getStatus());
        response.put("qualityScore", rec.getQualityScore());
        response.put("createdAt", rec.getCreatedAt());
        response.put("processedAt", rec.getProcessedAt());
        
        return response;
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String error, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String error) {
        return createErrorResponse(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ========== 请求体类 ==========

    public static class CreateModelRequest {
        private String modelName;
        private FederatedLearningModelEntity.ModelType modelType;
        private FederatedLearningModelEntity.ModelScope modelScope;
        private Map<String, Object> config;

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public FederatedLearningModelEntity.ModelType getModelType() { return modelType; }
        public void setModelType(FederatedLearningModelEntity.ModelType modelType) { this.modelType = modelType; }
        public FederatedLearningModelEntity.ModelScope getModelScope() { return modelScope; }
        public void setModelScope(FederatedLearningModelEntity.ModelScope modelScope) { this.modelScope = modelScope; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }

    public static class GenerateRecommendationRequest {
        private String userId;
        private String sessionId;
        private PrivacyPreservingRecommendationEntity.RecommendationType recommendationType;
        private String context;
        private Map<String, Object> options;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public PrivacyPreservingRecommendationEntity.RecommendationType getRecommendationType() { return recommendationType; }
        public void setRecommendationType(PrivacyPreservingRecommendationEntity.RecommendationType recommendationType) { this.recommendationType = recommendationType; }
        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }
        public Map<String, Object> getOptions() { return options; }
        public void setOptions(Map<String, Object> options) { this.options = options; }
    }

    public static class GradientUpdateRequest {
        private String clientId;
        private Map<String, Object> gradientUpdate;
        private int trainingSamples;

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public Map<String, Object> getGradientUpdate() { return gradientUpdate; }
        public void setGradientUpdate(Map<String, Object> gradientUpdate) { this.gradientUpdate = gradientUpdate; }
        public int getTrainingSamples() { return trainingSamples; }
        public void setTrainingSamples(int trainingSamples) { this.trainingSamples = trainingSamples; }
    }

    public static class FeedbackRequest {
        private String interactionType;
        private Double feedbackScore;
        private Integer dwellTimeSeconds;

        public String getInteractionType() { return interactionType; }
        public void setInteractionType(String interactionType) { this.interactionType = interactionType; }
        public Double getFeedbackScore() { return feedbackScore; }
        public void setFeedbackScore(Double feedbackScore) { this.feedbackScore = feedbackScore; }
        public Integer getDwellTimeSeconds() { return dwellTimeSeconds; }
        public void setDwellTimeSeconds(Integer dwellTimeSeconds) { this.dwellTimeSeconds = dwellTimeSeconds; }
    }
}