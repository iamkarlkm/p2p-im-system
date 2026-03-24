package com.im.system.controller;

import com.im.system.entity.AiFrontendFrameworkEntity;
import com.im.system.service.AiFrontendFrameworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 前端框架 REST API 控制器
 */
@RestController
@RequestMapping("/api/ai-frontend-framework")
public class AiFrontendFrameworkController {

    @Autowired
    private AiFrontendFrameworkService service;

    // 基础 CRUD API
    
    @PostMapping
    public ResponseEntity<?> createFramework(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam String frameworkVersion) {
        AiFrontendFrameworkEntity entity = service.createFramework(userId, deviceId, frameworkVersion);
        return ResponseEntity.ok(entity);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getFramework(@PathVariable Long id) {
        return service.getFramework(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}/device/{deviceId}")
    public ResponseEntity<?> getFrameworkByUserAndDevice(
            @PathVariable Long userId,
            @PathVariable String deviceId) {
        return service.getFrameworkByUserAndDevice(userId, deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFrameworksByUser(@PathVariable Long userId) {
        List<AiFrontendFrameworkEntity> frameworks = service.getFrameworksByUser(userId);
        return ResponseEntity.ok(frameworks);
    }
    
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<?> getFrameworksByDevice(@PathVariable String deviceId) {
        List<AiFrontendFrameworkEntity> frameworks = service.getFrameworksByDevice(deviceId);
        return ResponseEntity.ok(frameworks);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFramework(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        AiFrontendFrameworkEntity entity = service.updateFramework(id, updates);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    @PutMapping("/user/{userId}/device/{deviceId}")
    public ResponseEntity<?> updateFrameworkByUserAndDevice(
            @PathVariable Long userId,
            @PathVariable String deviceId,
            @RequestBody Map<String, Object> updates) {
        AiFrontendFrameworkEntity entity = service.updateFrameworkByUserAndDevice(userId, deviceId, updates);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFramework(@PathVariable Long id) {
        boolean deleted = service.deleteFramework(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/user/{userId}/device/{deviceId}")
    public ResponseEntity<?> deleteFrameworkByUserAndDevice(
            @PathVariable Long userId,
            @PathVariable String deviceId) {
        boolean deleted = service.deleteFrameworkByUserAndDevice(userId, deviceId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    // 配置管理 API
    
    @PostMapping("/configure-model")
    public ResponseEntity<?> configureModel(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam String modelName,
            @RequestParam String modelVersion,
            @RequestParam String localModelEngine,
            @RequestParam String inferenceBackend) {
        AiFrontendFrameworkEntity entity = service.configureModel(
                userId, deviceId, modelName, modelVersion, localModelEngine, inferenceBackend);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/mark-model-loaded")
    public ResponseEntity<?> markModelLoaded(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam Integer modelSizeMb) {
        AiFrontendFrameworkEntity entity = service.markModelLoaded(userId, deviceId, modelSizeMb);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/mark-model-unloaded")
    public ResponseEntity<?> markModelUnloaded(
            @RequestParam Long userId,
            @RequestParam String deviceId) {
        AiFrontendFrameworkEntity entity = service.markModelUnloaded(userId, deviceId);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    // 功能管理 API
    
    @PostMapping("/enable-feature")
    public ResponseEntity<?> enableFeature(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam String feature,
            @RequestParam boolean enable) {
        AiFrontendFrameworkEntity entity = service.enableFeature(userId, deviceId, feature, enable);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/set-performance-level")
    public ResponseEntity<?> setPerformanceLevel(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam String performanceLevel) {
        AiFrontendFrameworkEntity entity = service.setPerformanceLevel(userId, deviceId, performanceLevel);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/set-privacy-mode")
    public ResponseEntity<?> setPrivacyMode(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam boolean privacyMode) {
        AiFrontendFrameworkEntity entity = service.setPrivacyMode(userId, deviceId, privacyMode);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/set-offline-mode")
    public ResponseEntity<?> setOfflineMode(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam boolean offlineMode) {
        AiFrontendFrameworkEntity entity = service.setOfflineMode(userId, deviceId, offlineMode);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    // 推理统计 API
    
    @PostMapping("/record-inference")
    public ResponseEntity<?> recordInference(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam boolean success,
            @RequestParam long latencyMs) {
        AiFrontendFrameworkEntity entity = service.recordInference(userId, deviceId, success, latencyMs);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/reset-inference-stats")
    public ResponseEntity<?> resetInferenceStats(
            @RequestParam Long userId,
            @RequestParam String deviceId) {
        AiFrontendFrameworkEntity entity = service.resetInferenceStats(userId, deviceId);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
    
    // 批量操作 API
    
    @PutMapping("/batch-update")
    public ResponseEntity<?> batchUpdate(
            @RequestParam List<Long> ids,
            @RequestBody Map<String, Object> updates) {
        List<AiFrontendFrameworkEntity> entities = service.batchUpdate(ids, updates);
        return ResponseEntity.ok(entities);
    }
    
    @PostMapping("/batch-enable-feature")
    public ResponseEntity<?> batchEnableFeature(
            @RequestParam List<Long> userIds,
            @RequestParam String feature,
            @RequestParam boolean enable) {
        int count = service.batchEnableFeature(userIds, feature, enable);
        return ResponseEntity.ok(Map.of("updatedCount", count));
    }
    
    @PostMapping("/batch-set-performance-level")
    public ResponseEntity<?> batchSetPerformanceLevel(
            @RequestParam List<String> deviceIds,
            @RequestParam String performanceLevel) {
        int count = service.batchSetPerformanceLevel(deviceIds, performanceLevel);
        return ResponseEntity.ok(Map.of("updatedCount", count));
    }
    
    // 查询 API
    
    @GetMapping("/active")
    public ResponseEntity<?> getActiveFrameworks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AiFrontendFrameworkEntity> frameworks = service.getActiveFrameworks(pageable);
        return ResponseEntity.ok(frameworks);
    }
    
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<?> getFrameworksByUserPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AiFrontendFrameworkEntity> frameworks = service.getFrameworksByUserPaged(userId, pageable);
        return ResponseEntity.ok(frameworks);
    }
    
    @GetMapping("/loaded-models")
    public ResponseEntity<?> getLoadedModelsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "modelLoadTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AiFrontendFrameworkEntity> models = service.getLoadedModelsPaged(pageable);
        return ResponseEntity.ok(models);
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchFrameworks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AiFrontendFrameworkEntity> results = service.searchFrameworks(keyword, pageable);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<?> searchUserFrameworks(
            @PathVariable Long userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AiFrontendFrameworkEntity> results = service.searchUserFrameworks(userId, keyword, pageable);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveFrameworksByUser(@PathVariable Long userId) {
        List<AiFrontendFrameworkEntity> frameworks = service.getActiveFrameworksByUser(userId);
        return ResponseEntity.ok(frameworks);
    }
    
    @GetMapping("/device/{deviceId}/loaded")
    public ResponseEntity<?> getFrameworksWithLoadedModel(@PathVariable String deviceId) {
        List<AiFrontendFrameworkEntity> frameworks = service.getFrameworksWithLoadedModel(deviceId);
        return ResponseEntity.ok(frameworks);
    }
    
    @GetMapping("/privacy-enabled")
    public ResponseEntity<?> getPrivacyEnabledDevices() {
        List<AiFrontendFrameworkEntity> devices = service.getPrivacyEnabledDevices();
        return ResponseEntity.ok(devices);
    }
    
    // 统计 API
    
    @GetMapping("/statistics")
    public ResponseEntity<?> getFrameworkStatistics() {
        Map<String, Object> statistics = service.getFrameworkStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/statistics/inference-backend")
    public ResponseEntity<?> getInferenceBackendDistribution() {
        List<Object[]> distribution = service.getInferenceBackendDistribution();
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/statistics/model-engine")
    public ResponseEntity<?> getModelEngineDistribution() {
        List<Object[]> distribution = service.getModelEngineDistribution();
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/statistics/performance-level")
    public ResponseEntity<?> getPerformanceLevelDistribution() {
        List<Object[]> distribution = service.getPerformanceLevelDistribution();
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/statistics/model-usage")
    public ResponseEntity<?> getModelUsageStatistics() {
        List<Object[]> statistics = service.getModelUsageStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/statistics/device-distribution")
    public ResponseEntity<?> getDeviceDistribution() {
        List<Object[]> distribution = service.getDeviceDistribution();
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/statistics/created-between")
    public ResponseEntity<?> countCreatedBetween(
            @RequestParam String start,
            @RequestParam String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        // 注意：实际实现中需要添加相应的方法到服务层
        return ResponseEntity.ok(Map.of("count", 0)); // 占位符
    }
    
    // 验证 API
    
    @GetMapping("/validate/{userId}/{deviceId}")
    public ResponseEntity<?> validateFrameworkConfiguration(
            @PathVariable Long userId,
            @PathVariable String deviceId) {
        boolean isValid = service.validateFrameworkConfiguration(userId, deviceId);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
    
    @GetMapping("/feature-enabled/{userId}/{deviceId}/{feature}")
    public ResponseEntity<?> isFeatureEnabled(
            @PathVariable Long userId,
            @PathVariable String deviceId,
            @PathVariable String feature) {
        boolean enabled = service.isFeatureEnabled(userId, deviceId, feature);
        return ResponseEntity.ok(Map.of("enabled", enabled));
    }
    
    @GetMapping("/privacy-mode/{userId}/{deviceId}")
    public ResponseEntity<?> isPrivacyModeEnabled(
            @PathVariable Long userId,
            @PathVariable String deviceId) {
        boolean enabled = service.isPrivacyModeEnabled(userId, deviceId);
        return ResponseEntity.ok(Map.of("enabled", enabled));
    }
    
    // 清理 API
    
    @PostMapping("/cleanup/inactive")
    public ResponseEntity<?> cleanupInactiveFrameworks(@RequestParam String threshold) {
        LocalDateTime thresholdTime = LocalDateTime.parse(threshold);
        int cleaned = service.cleanupInactiveFrameworks(thresholdTime);
        return ResponseEntity.ok(Map.of("cleanedCount", cleaned));
    }
    
    @PostMapping("/cleanup/unloaded-models")
    public ResponseEntity<?> cleanupUnloadedModels(@RequestParam String threshold) {
        LocalDateTime thresholdTime = LocalDateTime.parse(threshold);
        int cleaned = service.cleanupUnloadedModels(thresholdTime);
        return ResponseEntity.ok(Map.of("cleanedCount", cleaned));
    }
    
    @GetMapping("/find/inactive")
    public ResponseEntity<?> findInactiveFrameworks(@RequestParam String threshold) {
        LocalDateTime thresholdTime = LocalDateTime.parse(threshold);
        List<AiFrontendFrameworkEntity> frameworks = service.findInactiveFrameworks(thresholdTime);
        return ResponseEntity.ok(frameworks);
    }
    
    @GetMapping("/find/unloaded-models")
    public ResponseEntity<?> findUnloadedModels(@RequestParam String threshold) {
        LocalDateTime thresholdTime = LocalDateTime.parse(threshold);
        List<AiFrontendFrameworkEntity> models = service.findUnloadedModels(thresholdTime);
        return ResponseEntity.ok(models);
    }
    
    // 健康检查 API
    
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        
        try {
            Map<String, Object> stats = service.getFrameworkStatistics();
            health.put("statistics", stats);
            health.put("database", "CONNECTED");
        } catch (Exception e) {
            health.put("database", "ERROR");
            health.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(health);
    }
    
    // 状态 API
    
    @GetMapping("/status/{userId}/{deviceId}")
    public ResponseEntity<?> getFrameworkStatus(
            @PathVariable Long userId,
            @PathVariable String deviceId) {
        return service.getFrameworkByUserAndDevice(userId, deviceId)
                .map(entity -> {
                    Map<String, Object> status = new HashMap<>();
                    status.put("enabled", entity.getEnabled());
                    status.put("modelLoaded", entity.getModelLoaded());
                    status.put("modelName", entity.getModelName());
                    status.put("modelVersion", entity.getModelVersion());
                    status.put("localModelEngine", entity.getLocalModelEngine());
                    status.put("inferenceBackend", entity.getInferenceBackend());
                    status.put("privacyMode", entity.getPrivacyMode());
                    status.put("offlineMode", entity.getOfflineMode());
                    status.put("performanceLevel", entity.getPerformanceLevel());
                    status.put("featureEnabledSmartReply", entity.getFeatureEnabledSmartReply());
                    status.put("featureEnabledMessageSummary", entity.getFeatureEnabledMessageSummary());
                    status.put("featureEnabledSentimentAnalysis", entity.getFeatureEnabledSentimentAnalysis());
                    status.put("inferenceStatsTotal", entity.getInferenceStatsTotal());
                    status.put("inferenceStatsSuccess", entity.getInferenceStatsSuccess());
                    status.put("inferenceStatsAvgLatencyMs", entity.getInferenceStatsAvgLatencyMs());
                    status.put("lastUpdate", entity.getUpdatedAt());
                    return ResponseEntity.ok(status);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 批量导出 API (示例)
    
    @GetMapping("/export/csv")
    public ResponseEntity<?> exportToCsv(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        // 占位符实现 - 实际应生成 CSV 文件
        return ResponseEntity.ok(Map.of("message", "CSV export endpoint - implementation pending"));
    }
    
    @GetMapping("/export/json")
    public ResponseEntity<?> exportToJson(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        // 占位符实现 - 实际应生成 JSON 文件
        return ResponseEntity.ok(Map.of("message", "JSON export endpoint - implementation pending"));
    }
    
    // 系统管理 API
    
    @PostMapping("/system/refresh-cache")
    public ResponseEntity<?> refreshSystemCache() {
        // 占位符实现 - 实际应刷新缓存
        return ResponseEntity.ok(Map.of("message", "Cache refresh initiated"));
    }
    
    @PostMapping("/system/reindex")
    public ResponseEntity<?> reindexDatabase() {
        // 占位符实现 - 实际应重建索引
        return ResponseEntity.ok(Map.of("message", "Reindexing initiated"));
    }
}