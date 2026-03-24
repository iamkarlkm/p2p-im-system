package com.im.system.controller;

import com.im.system.entity.MessageEditEntity;
import com.im.system.service.MessageEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 消息编辑控制器 - REST API for message editing and version management
 */
@RestController
@RequestMapping("/api/message-edits")
public class MessageEditController {
    
    @Autowired
    private MessageEditService messageEditService;
    
    // ==================== CRUD Operations ====================
    
    /**
     * 创建新的消息编辑记录
     */
    @PostMapping
    public ResponseEntity<MessageEditEntity> createMessageEdit(@RequestBody MessageEditEntity messageEdit) {
        try {
            MessageEditEntity created = messageEditService.createMessageEdit(messageEdit);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 批量创建消息编辑记录
     */
    @PostMapping("/batch")
    public ResponseEntity<List<MessageEditEntity>> batchCreateMessageEdits(@RequestBody List<MessageEditEntity> messageEdits) {
        try {
            List<MessageEditEntity> created = messageEdits.stream()
                    .map(messageEditService::createMessageEdit)
                    .toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取消息编辑记录详情
     */
    @GetMapping("/{editId}")
    public ResponseEntity<MessageEditEntity> getMessageEdit(@PathVariable UUID editId) {
        return messageEditService.getMessageEditByVersion(editId, null)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 更新消息编辑记录
     */
    @PutMapping("/{editId}")
    public ResponseEntity<MessageEditEntity> updateMessageEdit(@PathVariable UUID editId,
                                                              @RequestBody MessageEditEntity messageEdit) {
        try {
            MessageEditEntity updated = messageEditService.updateMessageEdit(editId, messageEdit);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除消息编辑记录（软删除）
     */
    @DeleteMapping("/{editId}")
    public ResponseEntity<Void> deleteMessageEdit(@PathVariable UUID editId) {
        try {
            messageEditService.deleteMessageEdit(editId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 恢复已删除的消息编辑记录
     */
    @PostMapping("/{editId}/restore")
    public ResponseEntity<MessageEditEntity> restoreMessageEdit(@PathVariable UUID editId) {
        try {
            messageEditService.restoreMessageEdit(editId);
            return messageEditService.getMessageEditByVersion(editId, null)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 永久删除消息编辑记录
     */
    @DeleteMapping("/{editId}/permanent")
    public ResponseEntity<Void> permanentlyDeleteMessageEdit(@PathVariable UUID editId) {
        try {
            messageEditService.permanentlyDeleteOldDeletedEdits(LocalDateTime.now());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ==================== Version Management ====================
    
    /**
     * 获取消息的所有编辑版本
     */
    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<MessageEditEntity>> getMessageEdits(@PathVariable UUID messageId) {
        List<MessageEditEntity> edits = messageEditService.getMessageEdits(messageId);
        return ResponseEntity.ok(edits);
    }
    
    /**
     * 获取消息的特定版本
     */
    @GetMapping("/message/{messageId}/version/{version}")
    public ResponseEntity<MessageEditEntity> getMessageEditByVersion(@PathVariable UUID messageId,
                                                                     @PathVariable Integer version) {
        return messageEditService.getMessageEditByVersion(messageId, version)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取消息的最新版本
     */
    @GetMapping("/message/{messageId}/latest")
    public ResponseEntity<MessageEditEntity> getLatestMessageEdit(@PathVariable UUID messageId) {
        return messageEditService.getLatestMessageEdit(messageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取特定版本的前一个版本
     */
    @GetMapping("/message/{messageId}/version/{version}/previous")
    public ResponseEntity<MessageEditEntity> getPreviousVersion(@PathVariable UUID messageId,
                                                                @PathVariable Integer version) {
        List<MessageEditEntity> edits = messageEditService.getMessageEdits(messageId);
        MessageEditEntity previous = null;
        for (MessageEditEntity edit : edits) {
            if (edit.getVersion() < version && (previous == null || edit.getVersion() > previous.getVersion())) {
                previous = edit;
            }
        }
        return previous != null ? ResponseEntity.ok(previous) : ResponseEntity.notFound().build();
    }
    
    /**
     * 获取特定版本的后一个版本
     */
    @GetMapping("/message/{messageId}/version/{version}/next")
    public ResponseEntity<MessageEditEntity> getNextVersion(@PathVariable UUID messageId,
                                                            @PathVariable Integer version) {
        List<MessageEditEntity> edits = messageEditService.getMessageEdits(messageId);
        MessageEditEntity next = null;
        for (MessageEditEntity edit : edits) {
            if (edit.getVersion() > version && (next == null || edit.getVersion() < next.getVersion())) {
                next = edit;
            }
        }
        return next != null ? ResponseEntity.ok(next) : ResponseEntity.notFound().build();
    }
    
    /**
     * 回滚到特定版本
     */
    @PostMapping("/message/{messageId}/rollback/{version}")
    public ResponseEntity<MessageEditEntity> rollbackToVersion(@PathVariable UUID messageId,
                                                               @PathVariable Integer version,
                                                               @RequestBody(required = false) Map<String, String> rollbackData) {
        try {
            // Get the target version
            MessageEditEntity targetVersion = messageEditService.getMessageEditByVersion(messageId, version)
                    .orElseThrow(() -> new IllegalArgumentException("Version not found"));
            
            // Create a new edit that's a copy of the target version
            MessageEditEntity rollbackEdit = new MessageEditEntity();
            rollbackEdit.setMessageId(messageId);
            rollbackEdit.setOriginalMessageId(targetVersion.getOriginalMessageId());
            rollbackEdit.setUserId(targetVersion.getUserId());
            rollbackEdit.setConversationId(targetVersion.getConversationId());
            rollbackEdit.setContentType(targetVersion.getContentType());
            rollbackEdit.setContent(targetVersion.getContent());
            rollbackEdit.setOriginalContent(targetVersion.getContent());
            rollbackEdit.setEditType("ROLLBACK");
            rollbackEdit.setEditReason(rollbackData != null ? rollbackData.get("reason") : "Rollback to version " + version);
            rollbackEdit.setIsLatest(true);
            
            MessageEditEntity created = messageEditService.createMessageEdit(rollbackEdit);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ==================== Search and Filter ====================
    
    /**
     * 搜索消息编辑记录
     */
    @GetMapping("/search")
    public ResponseEntity<List<MessageEditEntity>> searchMessageEdits(
            @RequestParam(required = false) UUID messageId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID conversationId,
            @RequestParam(required = false) String editType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<MessageEditEntity> results = messageEditService.searchMessageEdits(
                messageId, userId, conversationId, editType, status, auditStatus, platform, startDate, endDate);
        return ResponseEntity.ok(results);
    }
    
    /**
     * 按内容搜索
     */
    @GetMapping("/search/content")
    public ResponseEntity<List<MessageEditEntity>> searchByContent(@RequestParam String keyword) {
        // This would require a custom repository method
        // For now, return not implemented
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    /**
     * 按标签搜索
     */
    @GetMapping("/search/tags")
    public ResponseEntity<List<MessageEditEntity>> searchByTags(@RequestParam String tag) {
        // This would require a custom repository method
        // For now, return not implemented
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    // ==================== Audit Operations ====================
    
    /**
     * 审核消息编辑记录
     */
    @PostMapping("/{editId}/audit")
    public ResponseEntity<MessageEditEntity> auditMessageEdit(@PathVariable UUID editId,
                                                              @RequestBody Map<String, String> auditData) {
        try {
            String auditStatus = auditData.get("auditStatus");
            String auditNotes = auditData.get("auditNotes");
            UUID auditorId = auditData.get("auditorId") != null ? 
                    UUID.fromString(auditData.get("auditorId")) : null;
            
            if (auditStatus == null) {
                return ResponseEntity.badRequest().build();
            }
            
            MessageEditEntity audited = messageEditService.auditMessageEdit(editId, auditStatus, auditorId, auditNotes);
            return ResponseEntity.ok(audited);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 批量审核消息编辑记录
     */
    @PostMapping("/batch-audit")
    public ResponseEntity<Map<String, Object>> batchAuditMessageEdits(@RequestBody Map<String, Object> batchAuditData) {
        try {
            @SuppressWarnings("unchecked")
            List<String> editIdsStr = (List<String>) batchAuditData.get("editIds");
            String auditStatus = (String) batchAuditData.get("auditStatus");
            String auditNotes = (String) batchAuditData.get("auditNotes");
            String auditorIdStr = (String) batchAuditData.get("auditorId");
            
            if (editIdsStr == null || auditStatus == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<UUID> editIds = editIdsStr.stream().map(UUID::fromString).toList();
            UUID auditorId = auditorIdStr != null ? UUID.fromString(auditorIdStr) : null;
            
            int updatedCount = messageEditService.batchAuditMessageEdits(editIds, auditStatus, auditorId, auditNotes);
            
            return ResponseEntity.ok(Map.of(
                    "updatedCount", updatedCount,
                    "message", "Batch audit completed"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取待审核的编辑记录
     */
    @GetMapping("/pending-audit")
    public ResponseEntity<List<MessageEditEntity>> getPendingAuditEdits() {
        List<MessageEditEntity> pending = messageEditService.searchMessageEdits(
                null, null, null, null, "ACTIVE", "PENDING", null, null, null);
        return ResponseEntity.ok(pending);
    }
    
    // ==================== Statistics and Reports ====================
    
    /**
     * 获取用户的编辑统计
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserEditStats(@PathVariable UUID userId) {
        try {
            Long editCount = messageEditService.getUserEditCount(userId);
            
            return ResponseEntity.ok(Map.of(
                    "userId", userId.toString(),
                    "editCount", editCount,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取会话的编辑统计
     */
    @GetMapping("/conversation/{conversationId}/stats")
    public ResponseEntity<Map<String, Object>> getConversationEditStats(@PathVariable UUID conversationId) {
        try {
            Long editCount = messageEditService.getConversationEditCount(conversationId);
            
            return ResponseEntity.ok(Map.of(
                    "conversationId", conversationId.toString(),
                    "editCount", editCount,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取编辑类型统计
     */
    @GetMapping("/stats/edit-types")
    public ResponseEntity<List<Map<String, Object>>> getEditTypeStats() {
        try {
            List<Object[]> stats = messageEditService.getEditStatisticsByEditType();
            List<Map<String, Object>> result = stats.stream()
                    .map(row -> Map.of(
                            "editType", row[0],
                            "count", row[1]
                    ))
                    .toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取平台统计
     */
    @GetMapping("/stats/platforms")
    public ResponseEntity<List<Map<String, Object>>> getPlatformStats() {
        try {
            List<Object[]> stats = messageEditService.getEditStatisticsByPlatform();
            List<Map<String, Object>> result = stats.stream()
                    .map(row -> Map.of(
                            "platform", row[0],
                            "count", row[1]
                    ))
                    .toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取审核状态统计
     */
    @GetMapping("/stats/audit-status")
    public ResponseEntity<List<Map<String, Object>>> getAuditStatusStats() {
        try {
            List<Object[]> stats = messageEditService.getEditStatisticsByAuditStatus();
            List<Map<String, Object>> result = stats.stream()
                    .map(row -> Map.of(
                            "auditStatus", row[0],
                            "count", row[1]
                    ))
                    .toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ==================== Maintenance Operations ====================
    
    /**
     * 归档旧的消息编辑记录
     */
    @PostMapping("/maintenance/archive")
    public ResponseEntity<Map<String, Object>> archiveOldEdits(@RequestBody(required = false) Map<String, String> params) {
        try {
            LocalDateTime before = params != null && params.containsKey("before") ?
                    LocalDateTime.parse(params.get("before")) :
                    LocalDateTime.now().minusMonths(6);
            
            int archivedCount = messageEditService.archiveOldEdits(before);
            
            return ResponseEntity.ok(Map.of(
                    "archivedCount", archivedCount,
                    "beforeDate", before,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 清理过期的消息编辑记录
     */
    @PostMapping("/maintenance/cleanup-expired")
    public ResponseEntity<Map<String, Object>> cleanupExpiredEdits() {
        try {
            int deletedCount = messageEditService.deleteExpiredEdits(LocalDateTime.now());
            
            return ResponseEntity.ok(Map.of(
                    "deletedCount", deletedCount,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 清理过期的版本
     */
    @PostMapping("/maintenance/cleanup-expired-versions")
    public ResponseEntity<Map<String, Object>> cleanupExpiredVersions() {
        try {
            int deletedCount = messageEditService.deleteExpiredVersions(LocalDateTime.now());
            
            return ResponseEntity.ok(Map.of(
                    "deletedCount", deletedCount,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 同步待同步的编辑记录
     */
    @PostMapping("/maintenance/sync-pending")
    public ResponseEntity<Map<String, Object>> syncPendingEdits(@RequestBody(required = false) Map<String, String> params) {
        try {
            LocalDateTime before = params != null && params.containsKey("before") ?
                    LocalDateTime.parse(params.get("before")) :
                    LocalDateTime.now().minusHours(1);
            String newStatus = params != null ? params.getOrDefault("newStatus", "SYNCED") : "SYNCED";
            
            int syncedCount = messageEditService.syncPendingEdits(newStatus, before);
            
            return ResponseEntity.ok(Map.of(
                    "syncedCount", syncedCount,
                    "beforeDate", before,
                    "newStatus", newStatus,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ==================== Health and Monitoring ====================
    
    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            // Simple health check - count some records
            long activeCount = messageEditService.searchMessageEdits(null, null, null, null, "ACTIVE", null, null, null, null).size();
            
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "activeEdits", activeCount,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "status", "DOWN",
                            "error", e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }
    
    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        return ResponseEntity.ok(Map.of(
                "service", "Message Edit Service",
                "version", "1.0.0",
                "description", "Service for managing message edits and version history",
                "timestamp", LocalDateTime.now()
        ));
    }
}