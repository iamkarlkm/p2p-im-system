package com.im.system.controller;

import com.im.system.entity.MessageDraftEntity;
import com.im.system.service.MessageDraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息草稿跨设备同步 REST API 控制器
 * 提供草稿的CRUD、同步和冲突解决接口
 */
@RestController
@RequestMapping("/api/v1/drafts")
@RequiredArgsConstructor
@Tag(name = "消息草稿", description = "消息草稿跨设备同步管理")
public class MessageDraftController {
    
    private final MessageDraftService messageDraftService;
    
    /**
     * 保存或更新草稿
     */
    @PostMapping("/save")
    @Operation(summary = "保存或更新草稿", description = "保存用户在当前设备上的消息草稿")
    public ResponseEntity<MessageDraftEntity> saveDraft(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "设备ID") String deviceId,
            @RequestParam @Parameter(description = "会话ID") String conversationId,
            @RequestParam(required = false) @Parameter(description = "草稿内容") String draftContent,
            @RequestParam(defaultValue = "TEXT") @Parameter(description = "草稿类型") String draftType,
            @RequestParam(defaultValue = "false") @Parameter(description = "是否自动保存") boolean autoSave) {
        
        try {
            MessageDraftEntity draft = messageDraftService.saveOrUpdateDraft(userId, deviceId, conversationId, 
                    draftContent, draftType, autoSave);
            return ResponseEntity.ok(draft);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取指定会话的草稿
     */
    @GetMapping("/{conversationId}")
    @Operation(summary = "获取草稿", description = "获取指定会话的消息草稿")
    public ResponseEntity<MessageDraftEntity> getDraft(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @PathVariable @Parameter(description = "会话ID") String conversationId) {
        
        return messageDraftService.getDraft(userId, conversationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取用户所有草稿
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户所有草稿", description = "获取用户在所有设备上的所有草稿")
    public ResponseEntity<List<MessageDraftEntity>> getUserDrafts(
            @PathVariable @Parameter(description = "用户ID") Long userId) {
        
        List<MessageDraftEntity> drafts = messageDraftService.getUserDrafts(userId);
        return ResponseEntity.ok(drafts);
    }
    
    /**
     * 获取设备草稿
     */
    @GetMapping("/device/{deviceId}")
    @Operation(summary = "获取设备草稿", description = "获取用户在指定设备上的草稿")
    public ResponseEntity<List<MessageDraftEntity>> getDeviceDrafts(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @PathVariable @Parameter(description = "设备ID") String deviceId) {
        
        List<MessageDraftEntity> drafts = messageDraftService.getDeviceDrafts(userId, deviceId);
        return ResponseEntity.ok(drafts);
    }
    
    /**
     * 删除草稿
     */
    @DeleteMapping("/{conversationId}")
    @Operation(summary = "删除草稿", description = "删除指定会话的草稿")
    public ResponseEntity<Map<String, Object>> deleteDraft(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @PathVariable @Parameter(description = "会话ID") String conversationId) {
        
        boolean deleted = messageDraftService.deleteDraft(userId, conversationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("userId", userId);
        response.put("conversationId", conversationId);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 同步草稿
     */
    @PostMapping("/sync")
    @Operation(summary = "同步草稿", description = "将设备上的草稿同步到服务端")
    public ResponseEntity<MessageDraftEntity> syncDraft(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "设备ID") String deviceId,
            @RequestParam @Parameter(description = "会话ID") String conversationId,
            @RequestParam(required = false) @Parameter(description = "草稿内容") String draftContent,
            @RequestParam @Parameter(description = "本地版本号") Long localVersion) {
        
        try {
            MessageDraftEntity draft = messageDraftService.syncDraft(userId, deviceId, conversationId, 
                    draftContent, localVersion);
            
            // 检查是否存在冲突
            if (MessageDraftEntity.SyncStatus.CONFLICT.name().equals(draft.getSyncStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(draft);
            }
            
            return ResponseEntity.ok(draft);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 批量同步草稿
     */
    @PostMapping("/batch-sync")
    @Operation(summary = "批量同步草稿", description = "批量同步多个草稿")
    public ResponseEntity<List<MessageDraftEntity>> batchSyncDrafts(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestBody List<Map<String, Object>> draftChanges) {
        
        try {
            List<MessageDraftEntity> results = messageDraftService.batchSyncDrafts(userId, draftChanges);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 解决草稿冲突
     */
    @PostMapping("/resolve-conflict/{draftId}")
    @Operation(summary = "解决草稿冲突", description = "解决草稿同步冲突")
    public ResponseEntity<MessageDraftEntity> resolveConflict(
            @PathVariable @Parameter(description = "草稿ID") Long draftId,
            @RequestParam @Parameter(description = "解决后的内容") String resolvedContent,
            @RequestParam @Parameter(description = "新版本号") Long newVersion) {
        
        try {
            MessageDraftEntity draft = messageDraftService.resolveConflict(draftId, resolvedContent, newVersion);
            return ResponseEntity.ok(draft);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取待同步草稿
     */
    @GetMapping("/pending-sync")
    @Operation(summary = "获取待同步草稿", description = "获取需要同步的草稿列表")
    public ResponseEntity<List<MessageDraftEntity>> getPendingSyncDrafts(
            @RequestParam @Parameter(description = "用户ID") Long userId) {
        
        List<MessageDraftEntity> drafts = messageDraftService.getPendingSyncDrafts(userId);
        return ResponseEntity.ok(drafts);
    }
    
    /**
     * 标记为已同步
     */
    @PostMapping("/mark-synced/{draftId}")
    @Operation(summary = "标记为已同步", description = "标记草稿为已同步状态")
    public ResponseEntity<Map<String, Object>> markAsSynced(
            @PathVariable @Parameter(description = "草稿ID") Long draftId) {
        
        messageDraftService.markAsSynced(draftId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("draftId", draftId);
        response.put("syncStatus", "SYNCED");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取草稿统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取草稿统计", description = "获取用户的草稿统计信息")
    public ResponseEntity<Map<String, Object>> getDraftStatistics(
            @RequestParam @Parameter(description = "用户ID") Long userId) {
        
        Map<String, Object> stats = messageDraftService.getDraftStatistics(userId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 清空用户所有草稿
     */
    @DeleteMapping("/clear-all")
    @Operation(summary = "清空所有草稿", description = "清空用户的所有草稿")
    public ResponseEntity<Map<String, Object>> clearAllDrafts(
            @RequestParam @Parameter(description = "用户ID") Long userId) {
        
        int cleared = messageDraftService.clearUserDrafts(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", cleared > 0);
        response.put("clearedCount", cleared);
        response.put("userId", userId);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新草稿活跃状态
     */
    @PostMapping("/active-status")
    @Operation(summary = "更新活跃状态", description = "更新草稿的活跃状态")
    public ResponseEntity<Map<String, Object>> updateActiveStatus(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "设备ID") String deviceId,
            @RequestParam @Parameter(description = "会话ID") String conversationId,
            @RequestParam @Parameter(description = "是否活跃") boolean active) {
        
        messageDraftService.updateActiveStatus(userId, deviceId, conversationId, active);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("deviceId", deviceId);
        response.put("conversationId", conversationId);
        response.put("active", active);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 清理旧草稿（管理员接口）
     */
    @PostMapping("/admin/clean-old")
    @Operation(summary = "清理旧草稿", description = "清理指定天数前的旧草稿")
    public ResponseEntity<Map<String, Object>> cleanOldDrafts(
            @RequestParam(defaultValue = "30") @Parameter(description = "保留天数") int daysToKeep) {
        
        int deleted = messageDraftService.cleanOldDrafts(daysToKeep);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deletedCount", deleted);
        response.put("daysToKeep", daysToKeep);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取所有冲突草稿（管理员接口）
     */
    @GetMapping("/admin/conflicts")
    @Operation(summary = "获取所有冲突", description = "获取系统所有冲突草稿")
    public ResponseEntity<List<MessageDraftEntity>> getAllConflicts() {
        // 这里需要从仓储直接查询，实际项目中需要添加相应的仓储方法
        // List<MessageDraftEntity> conflicts = messageDraftRepository.findAllConflicts();
        // return ResponseEntity.ok(conflicts);
        return ResponseEntity.ok(List.of()); // 占位实现
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查草稿服务健康状态")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", "message-draft-service");
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }
}