package com.im.backend.controller;

import com.im.backend.entity.MessageStorageLayerEntity;
import com.im.backend.service.MessageArchiveService;
import com.im.backend.service.MessageStorageLayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息存储分层配置控制器
 */
@RestController
@RequestMapping("/api/v1/message-storage")
@Slf4j
@RequiredArgsConstructor
public class MessageStorageLayerController {
    
    private final MessageStorageLayerService messageStorageLayerService;
    private final MessageArchiveService messageArchiveService;
    
    /**
     * 创建存储分层策略
     */
    @PostMapping("/strategies")
    public ResponseEntity<Map<String, Object>> createStrategy(@RequestBody Map<String, Object> params) {
        try {
            MessageStorageLayerEntity strategy = new MessageStorageLayerEntity();
            strategy.setStrategyName((String) params.get("strategyName"));
            strategy.setDescription((String) params.get("description"));
            strategy.setHotStorageDays((Integer) params.get("hotStorageDays"));
            strategy.setWarmStorageDays((Integer) params.get("warmStorageDays"));
            strategy.setColdStorageType((String) params.get("coldStorageType"));
            strategy.setColdStorageBucket((String) params.get("coldStorageBucket"));
            strategy.setColdStoragePrefix((String) params.get("coldStoragePrefix"));
            strategy.setCompressionFormat((String) params.get("compressionFormat"));
            strategy.setEncryptionEnabled((Boolean) params.get("encryptionEnabled"));
            strategy.setEncryptionAlgorithm((String) params.get("encryptionAlgorithm"));
            strategy.setArchiveBatchSize((Integer) params.get("archiveBatchSize"));
            strategy.setArchiveIntervalMinutes((Integer) params.get("archiveIntervalMinutes"));
            strategy.setArchiveConcurrency((Integer) params.get("archiveConcurrency"));
            strategy.setAutoArchiveEnabled((Boolean) params.get("autoArchiveEnabled"));
            strategy.setAutoCleanupEnabled((Boolean) params.get("autoCleanupEnabled"));
            strategy.setCleanupRetentionDays((Integer) params.get("cleanupRetentionDays"));
            strategy.setSmartLayeringEnabled((Boolean) params.get("smartLayeringEnabled"));
            strategy.setSmartAccessThreshold((Integer) params.get("smartAccessThreshold"));
            strategy.setStatus("ENABLED");
            
            messageStorageLayerService.validateStrategy(strategy);
            MessageStorageLayerEntity created = messageStorageLayerService.createStrategy(strategy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", created);
            response.put("message", "策略创建成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("创建策略失败", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "服务器错误"));
        }
    }
    
    /**
     * 更新存储分层策略
     */
    @PutMapping("/strategies/{id}")
    public ResponseEntity<Map<String, Object>> updateStrategy(@PathVariable Long id, 
                                                              @RequestBody Map<String, Object> params) {
        try {
            MessageStorageLayerEntity existing = messageStorageLayerService.getStrategyById(id);
            
            existing.setStrategyName((String) params.get("strategyName"));
            existing.setDescription((String) params.get("description"));
            if (params.get("hotStorageDays") != null) 
                existing.setHotStorageDays((Integer) params.get("hotStorageDays"));
            if (params.get("warmStorageDays") != null) 
                existing.setWarmStorageDays((Integer) params.get("warmStorageDays"));
            if (params.get("coldStorageType") != null) 
                existing.setColdStorageType((String) params.get("coldStorageType"));
            if (params.get("coldStorageBucket") != null) 
                existing.setColdStorageBucket((String) params.get("coldStorageBucket"));
            if (params.get("coldStoragePrefix") != null) 
                existing.setColdStoragePrefix((String) params.get("coldStoragePrefix"));
            if (params.get("compressionFormat") != null) 
                existing.setCompressionFormat((String) params.get("compressionFormat"));
            if (params.get("encryptionEnabled") != null) 
                existing.setEncryptionEnabled((Boolean) params.get("encryptionEnabled"));
            if (params.get("encryptionAlgorithm") != null) 
                existing.setEncryptionAlgorithm((String) params.get("encryptionAlgorithm"));
            if (params.get("archiveBatchSize") != null) 
                existing.setArchiveBatchSize((Integer) params.get("archiveBatchSize"));
            if (params.get("archiveIntervalMinutes") != null) 
                existing.setArchiveIntervalMinutes((Integer) params.get("archiveIntervalMinutes"));
            if (params.get("archiveConcurrency") != null) 
                existing.setArchiveConcurrency((Integer) params.get("archiveConcurrency"));
            if (params.get("autoArchiveEnabled") != null) 
                existing.setAutoArchiveEnabled((Boolean) params.get("autoArchiveEnabled"));
            if (params.get("autoCleanupEnabled") != null) 
                existing.setAutoCleanupEnabled((Boolean) params.get("autoCleanupEnabled"));
            if (params.get("cleanupRetentionDays") != null) 
                existing.setCleanupRetentionDays((Integer) params.get("cleanupRetentionDays"));
            if (params.get("smartLayeringEnabled") != null) 
                existing.setSmartLayeringEnabled((Boolean) params.get("smartLayeringEnabled"));
            if (params.get("smartAccessThreshold") != null) 
                existing.setSmartAccessThreshold((Integer) params.get("smartAccessThreshold"));
            
            messageStorageLayerService.validateStrategy(existing);
            MessageStorageLayerEntity updated = messageStorageLayerService.updateStrategy(id, existing);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updated);
            response.put("message", "策略更新成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("更新策略失败，ID: {}", id, e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "服务器错误"));
        }
    }
    
    /**
     * 获取策略详情
     */
    @GetMapping("/strategies/{id}")
    public ResponseEntity<Map<String, Object>> getStrategy(@PathVariable Long id) {
        try {
            MessageStorageLayerEntity strategy = messageStorageLayerService.getStrategyById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", strategy);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * 获取所有策略（分页）
     */
    @GetMapping("/strategies")
    public ResponseEntity<Map<String, Object>> getAllStrategies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Pageable pageable = PageRequest.of(page, size, 
                "asc".equalsIgnoreCase(direction) ? Sort.by(sort).ascending() : Sort.by(sort).descending());
            Page<MessageStorageLayerEntity> strategies = messageStorageLayerService.getAllStrategies(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", strategies.getContent());
            response.put("totalElements", strategies.getTotalElements());
            response.put("totalPages", strategies.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取策略列表失败", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "服务器错误"));
        }
    }
    
    /**
     * 获取所有启用的策略
     */
    @GetMapping("/strategies/enabled")
    public ResponseEntity<Map<String, Object>> getEnabledStrategies() {
        try {
            List<MessageStorageLayerEntity> strategies = messageStorageLayerService.getAllEnabledStrategies();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", strategies);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取启用策略列表失败", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "服务器错误"));
        }
    }
    
    /**
     * 删除策略
     */
    @DeleteMapping("/strategies/{id}")
    public ResponseEntity<Map<String, Object>> deleteStrategy(@PathVariable Long id) {
        try {
            messageStorageLayerService.deleteStrategy(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "策略删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * 启用策略
     */
    @PostMapping("/strategies/{id}/enable")
    public ResponseEntity<Map<String, Object>> enableStrategy(@PathVariable Long id) {
        try {
            messageStorageLayerService.enableStrategy(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "策略已启用");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * 禁用策略
     */
    @PostMapping("/strategies/{id}/disable")
    public ResponseEntity<Map<String, Object>> disableStrategy(@PathVariable Long id) {
        try {
            messageStorageLayerService.disableStrategy(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "策略已禁用");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            MessageStorageLayerService.StorageLayerStatistics stats = messageStorageLayerService.getStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "enabledCount", stats.getEnabledCount(),
                "totalArchivedMessages", stats.getTotalArchivedMessages(),
                "totalArchivedSize", stats.getTotalArchivedSize(),
                "totalArchivedSizeFormatted", stats.getTotalArchivedSizeFormatted(),
                "totalCleanedMessages", stats.getTotalCleanedMessages(),
                "errorStrategyCount", stats.getErrorStrategyCount()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "服务器错误"));
        }
    }
    
    /**
     * 执行手动归档
     */
    @PostMapping("/archive/execute")
    public ResponseEntity<Map<String, Object>> executeArchive(@RequestParam Long strategyId) {
        try {
            MessageArchiveService.ArchiveResult result = messageArchiveService.archiveMessages(strategyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("data", result);
            response.put("message", result.getMessage());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("执行归档失败，策略ID: {}", strategyId, e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "服务器错误: " + e.getMessage()));
        }
    }
    
    /**
     * 执行自动归档（调度任务调用）
     */
    @PostMapping("/archive/auto")
    public ResponseEntity<Map<String, Object>> executeAutoArchive() {
        try {
            MessageArchiveService.AutoArchiveResult result = messageArchiveService.executeAutoArchive();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("data", result);
            response.put("message", result.getMessage());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("执行自动归档失败", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "服务器错误: " + e.getMessage()));
        }
    }
}