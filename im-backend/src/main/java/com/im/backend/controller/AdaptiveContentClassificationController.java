package com.im.backend.controller;

import com.im.backend.entity.AdaptiveContentClassificationConfigEntity;
import com.im.backend.entity.ContentClassificationResultEntity;
import com.im.backend.service.AdaptiveContentClassificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自适应内容分类控制器
 * 提供REST API用于管理分类配置和执行内容分类
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/adaptive-content-classification")
public class AdaptiveContentClassificationController {
    
    private final AdaptiveContentClassificationService classificationService;
    
    @Autowired
    public AdaptiveContentClassificationController(AdaptiveContentClassificationService classificationService) {
        this.classificationService = classificationService;
    }
    
    // ========== 配置管理 API ==========
    
    /**
     * 创建分类配置
     * POST /api/v1/adaptive-content-classification/configs
     */
    @PostMapping("/configs")
    public ResponseEntity<?> createConfig(@RequestBody AdaptiveContentClassificationConfigEntity config) {
        try {
            AdaptiveContentClassificationConfigEntity createdConfig = classificationService.createConfig(config);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("配置创建成功", createdConfig));
        } catch (Exception e) {
            log.error("创建分类配置失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取配置详情
     * GET /api/v1/adaptive-content-classification/configs/{configId}
     */
    @GetMapping("/configs/{configId}")
    public ResponseEntity<?> getConfig(@PathVariable Long configId) {
        try {
            AdaptiveContentClassificationConfigEntity config = classificationService.getConfig(configId);
            return ResponseEntity.ok(createSuccessResponse("获取配置成功", config));
        } catch (Exception e) {
            log.error("获取配置详情失败，ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 更新分类配置
     * PUT /api/v1/adaptive-content-classification/configs/{configId}
     */
    @PutMapping("/configs/{configId}")
    public ResponseEntity<?> updateConfig(@PathVariable Long configId, 
                                          @RequestBody AdaptiveContentClassificationConfigEntity updatedConfig) {
        try {
            AdaptiveContentClassificationConfigEntity config = classificationService.updateConfig(configId, updatedConfig);
            return ResponseEntity.ok(createSuccessResponse("配置更新成功", config));
        } catch (Exception e) {
            log.error("更新分类配置失败，ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 删除分类配置
     * DELETE /api/v1/adaptive-content-classification/configs/{configId}
     */
    @DeleteMapping("/configs/{configId}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long configId) {
        try {
            classificationService.deleteConfig(configId);
            return ResponseEntity.ok(createSuccessResponse("配置删除成功", null));
        } catch (Exception e) {
            log.error("删除分类配置失败，ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 查询用户配置列表
     * GET /api/v1/adaptive-content-classification/configs/user/{userId}
     */
    @GetMapping("/configs/user/{userId}")
    public ResponseEntity<?> getUserConfigs(@PathVariable Long userId) {
        try {
            List<AdaptiveContentClassificationConfigEntity> configs = classificationService.getUserConfigs(userId);
            return ResponseEntity.ok(createSuccessResponse("获取用户配置成功", configs));
        } catch (Exception e) {
            log.error("查询用户配置列表失败，用户ID: {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 分页查询用户配置
     * GET /api/v1/adaptive-content-classification/configs/user/{userId}/page
     */
    @GetMapping("/configs/user/{userId}/page")
    public ResponseEntity<?> getUserConfigsPage(@PathVariable Long userId,
                                                @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        try {
            Page<AdaptiveContentClassificationConfigEntity> configs = classificationService.getUserConfigsPage(userId, pageable);
            return ResponseEntity.ok(createSuccessResponse("获取用户配置分页成功", configs));
        } catch (Exception e) {
            log.error("分页查询用户配置失败，用户ID: {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 搜索配置
     * GET /api/v1/adaptive-content-classification/configs/search
     */
    @GetMapping("/configs/search")
    public ResponseEntity<?> searchConfigs(@RequestParam String keyword) {
        try {
            List<AdaptiveContentClassificationConfigEntity> configs = classificationService.searchConfigs(keyword);
            return ResponseEntity.ok(createSuccessResponse("搜索配置成功", configs));
        } catch (Exception e) {
            log.error("搜索配置失败，关键词: {}: {}", keyword, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取配置统计信息
     * GET /api/v1/adaptive-content-classification/configs/{configId}/stats
     */
    @GetMapping("/configs/{configId}/stats")
    public ResponseEntity<?> getConfigStats(@PathVariable Long configId) {
        try {
            AdaptiveContentClassificationConfigEntity config = classificationService.getConfig(configId);
            Map<String, Object> stats = classificationService.getConfigStats(config.getUserId());
            return ResponseEntity.ok(createSuccessResponse("获取配置统计成功", stats));
        } catch (Exception e) {
            log.error("获取配置统计失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取用户配置统计
     * GET /api/v1/adaptive-content-classification/configs/user/{userId}/stats
     */
    @GetMapping("/configs/user/{userId}/stats")
    public ResponseEntity<?> getUserConfigStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = classificationService.getConfigStats(userId);
            return ResponseEntity.ok(createSuccessResponse("获取用户配置统计成功", stats));
        } catch (Exception e) {
            log.error("获取用户配置统计失败，用户ID: {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    // ========== 内容分类 API ==========
    
    /**
     * 分类单个内容
     * POST /api/v1/adaptive-content-classification/configs/{configId}/classify
     */
    @PostMapping("/configs/{configId}/classify")
    public ResponseEntity<?> classifyContent(@PathVariable Long configId,
                                             @RequestBody Map<String, Object> contentData) {
        try {
            ContentClassificationResultEntity result = classificationService.classifyContent(configId, contentData);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("内容分类成功", result));
        } catch (Exception e) {
            log.error("分类内容失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 批量分类内容
     * POST /api/v1/adaptive-content-classification/configs/{configId}/batch-classify
     */
    @PostMapping("/configs/{configId}/batch-classify")
    public ResponseEntity<?> batchClassifyContent(@PathVariable Long configId,
                                                  @RequestBody List<Map<String, Object>> contentDataList) {
        try {
            List<ContentClassificationResultEntity> results = classificationService.batchClassifyContent(configId, contentDataList);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("批量分类成功", results));
        } catch (Exception e) {
            log.error("批量分类内容失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取分类结果
     * GET /api/v1/adaptive-content-classification/results/{resultId}
     */
    @GetMapping("/results/{resultId}")
    public ResponseEntity<?> getClassificationResult(@PathVariable Long resultId) {
        try {
            ContentClassificationResultEntity result = classificationService.getClassificationResult(resultId);
            return ResponseEntity.ok(createSuccessResponse("获取分类结果成功", result));
        } catch (Exception e) {
            log.error("获取分类结果失败，结果ID: {}: {}", resultId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 查询配置的分类结果
     * GET /api/v1/adaptive-content-classification/configs/{configId}/results
     */
    @GetMapping("/configs/{configId}/results")
    public ResponseEntity<?> getConfigResults(@PathVariable Long configId) {
        try {
            List<ContentClassificationResultEntity> results = classificationService.getConfigResults(configId);
            return ResponseEntity.ok(createSuccessResponse("获取配置分类结果成功", results));
        } catch (Exception e) {
            log.error("查询配置分类结果失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 分页查询配置的分类结果
     * GET /api/v1/adaptive-content-classification/configs/{configId}/results/page
     */
    @GetMapping("/configs/{configId}/results/page")
    public ResponseEntity<?> getConfigResultsPage(@PathVariable Long configId,
                                                  @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {
        try {
            Page<ContentClassificationResultEntity> results = classificationService.getConfigResultsPage(configId, pageable);
            return ResponseEntity.ok(createSuccessResponse("获取配置分类结果分页成功", results));
        } catch (Exception e) {
            log.error("分页查询配置分类结果失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 查询高置信度结果
     * GET /api/v1/adaptive-content-classification/configs/{configId}/results/high-confidence
     */
    @GetMapping("/configs/{configId}/results/high-confidence")
    public ResponseEntity<?> getHighConfidenceResults(@PathVariable Long configId,
                                                      @RequestParam(required = false) Integer minConfidence) {
        try {
            List<ContentClassificationResultEntity> results = classificationService.getHighConfidenceResults(configId, minConfidence);
            return ResponseEntity.ok(createSuccessResponse("获取高置信度结果成功", results));
        } catch (Exception e) {
            log.error("查询高置信度结果失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 查询低置信度结果
     * GET /api/v1/adaptive-content-classification/configs/{configId}/results/low-confidence
     */
    @GetMapping("/configs/{configId}/results/low-confidence")
    public ResponseEntity<?> getLowConfidenceResults(@PathVariable Long configId,
                                                     @RequestParam(required = false) Integer maxConfidence) {
        try {
            List<ContentClassificationResultEntity> results = classificationService.getLowConfidenceResults(configId, maxConfidence);
            return ResponseEntity.ok(createSuccessResponse("获取低置信度结果成功", results));
        } catch (Exception e) {
            log.error("查询低置信度结果失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 搜索分类结果
     * GET /api/v1/adaptive-content-classification/configs/{configId}/results/search
     */
    @GetMapping("/configs/{configId}/results/search")
    public ResponseEntity<?> searchResults(@PathVariable Long configId,
                                           @RequestParam String keyword) {
        try {
            List<ContentClassificationResultEntity> results = classificationService.searchResults(configId, keyword);
            return ResponseEntity.ok(createSuccessResponse("搜索分类结果成功", results));
        } catch (Exception e) {
            log.error("搜索分类结果失败，配置ID: {}，关键词: {}: {}", configId, keyword, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取分类统计信息
     * GET /api/v1/adaptive-content-classification/configs/{configId}/classification-stats
     */
    @GetMapping("/configs/{configId}/classification-stats")
    public ResponseEntity<?> getClassificationStats(@PathVariable Long configId) {
        try {
            Map<String, Object> stats = classificationService.getClassificationStats(configId);
            return ResponseEntity.ok(createSuccessResponse("获取分类统计成功", stats));
        } catch (Exception e) {
            log.error("获取分类统计失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取分类趋势分析
     * GET /api/v1/adaptive-content-classification/configs/{configId}/trend
     */
    @GetMapping("/configs/{configId}/trend")
    public ResponseEntity<?> getClassificationTrend(@PathVariable Long configId,
                                                    @RequestParam(required = false) Integer days) {
        try {
            Map<String, Object> trend = classificationService.getClassificationTrend(configId, days);
            return ResponseEntity.ok(createSuccessResponse("获取分类趋势成功", trend));
        } catch (Exception e) {
            log.error("获取分类趋势失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    // ========== 增量学习 API ==========
    
    /**
     * 执行增量学习
     * POST /api/v1/adaptive-content-classification/configs/{configId}/incremental-learning
     */
    @PostMapping("/configs/{configId}/incremental-learning")
    public ResponseEntity<?> performIncrementalLearning(@PathVariable Long configId) {
        try {
            classificationService.performIncrementalLearning(configId);
            return ResponseEntity.ok(createSuccessResponse("增量学习执行成功", null));
        } catch (Exception e) {
            log.error("执行增量学习失败，配置ID: {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 批量执行增量学习
     * POST /api/v1/adaptive-content-classification/configs/batch-incremental-learning
     */
    @PostMapping("/configs/batch-incremental-learning")
    public ResponseEntity<?> batchIncrementalLearning(@RequestBody List<Long> configIds) {
        try {
            classificationService.batchIncrementalLearning(configIds);
            return ResponseEntity.ok(createSuccessResponse("批量增量学习执行成功", null));
        } catch (Exception e) {
            log.error("批量执行增量学习失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 自动执行增量学习
     * POST /api/v1/adaptive-content-classification/configs/auto-incremental-learning
     */
    @PostMapping("/configs/auto-incremental-learning")
    public ResponseEntity<?> autoIncrementalLearning() {
        try {
            classificationService.autoIncrementalLearning();
            return ResponseEntity.ok(createSuccessResponse("自动增量学习执行成功", null));
        } catch (Exception e) {
            log.error("自动执行增量学习失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    // ========== 系统管理 API ==========
    
    /**
     * 系统健康检查
     * GET /api/v1/adaptive-content-classification/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());
            health.put("service", "Adaptive Content Classification Service");
            health.put("version", "1.0.0");
            
            return ResponseEntity.ok(createSuccessResponse("服务健康", health));
        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage(), e);
            Map<String, Object> errorHealth = new HashMap<>();
            errorHealth.put("status", "DOWN");
            errorHealth.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorHealth);
        }
    }
    
    /**
     * 获取系统统计
     * GET /api/v1/adaptive-content-classification/system-stats
     */
    @GetMapping("/system-stats")
    public ResponseEntity<?> getSystemStats() {
        try {
            // 模拟系统统计
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalConfigs", 0); // 实际应从数据库获取
            stats.put("totalClassifications", 0);
            stats.put("averageConfidence", 0.0);
            stats.put("activeUsers", 0);
            stats.put("uptime", "0 days");
            
            return ResponseEntity.ok(createSuccessResponse("获取系统统计成功", stats));
        } catch (Exception e) {
            log.error("获取系统统计失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }
    
    // ========== 辅助方法 ==========
    
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", error);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}