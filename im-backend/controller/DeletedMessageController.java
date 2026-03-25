package com.im.backend.controller;

import com.im.backend.entity.DeletedMessageEntity;
import com.im.backend.service.DeletedMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 已删除消息REST API控制器
 * 提供已删除消息的管理、审核和查询接口
 */
@RestController
@RequestMapping("/api/deleted-messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "已删除消息管理", description = "已删除消息的查询、审核和管理API")
public class DeletedMessageController {

    private final DeletedMessageService deletedMessageService;

    // ========== 基本CRUD API ==========

    @Operation(summary = "记录已删除消息", description = "创建一条已删除消息记录")
    @PostMapping("/record")
    public ResponseEntity<DeletedMessageEntity> recordDeletedMessage(
            @RequestParam String originalMessageId,
            @RequestParam String senderId,
            @RequestParam String receiverId,
            @RequestParam DeletedMessageEntity.ReceiverType receiverType,
            @RequestParam String deletedByUserId,
            @RequestParam DeletedMessageEntity.DeleteReason deleteReason,
            @RequestParam(required = false) String originalContent,
            @RequestParam(defaultValue = "TEXT") DeletedMessageEntity.MessageType messageType) {
        
        try {
            DeletedMessageEntity entity = deletedMessageService.recordDeletedMessage(
                originalMessageId, senderId, receiverId, receiverType, deletedByUserId, 
                deleteReason, originalContent, messageType);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            log.error("Failed to record deleted message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "根据原始消息ID查询", description = "根据原始消息ID查找已删除记录")
    @GetMapping("/by-original-id/{originalMessageId}")
    public ResponseEntity<DeletedMessageEntity> getByOriginalMessageId(
            @PathVariable String originalMessageId) {
        
        return deletedMessageService.findByOriginalMessageId(originalMessageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "检查消息是否存在", description = "检查原始消息是否已被删除并记录")
    @GetMapping("/exists/{originalMessageId}")
    public ResponseEntity<Map<String, Boolean>> existsByOriginalMessageId(
            @PathVariable String originalMessageId) {
        
        boolean exists = deletedMessageService.existsByOriginalMessageId(originalMessageId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // ========== 查询API ==========

    @Operation(summary = "按发送者查询", description = "获取指定发送者的已删除消息列表")
    @GetMapping("/by-sender/{senderId}")
    public ResponseEntity<List<DeletedMessageEntity>> getBySender(
            @PathVariable String senderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("deletedAt").descending());
        Page<DeletedMessageEntity> result = deletedMessageService.getMessagesBySender(senderId, pageable);
        return ResponseEntity.ok(result.getContent());
    }

    @Operation(summary = "按接收者查询", description = "获取指定接收者的已删除消息列表")
    @GetMapping("/by-receiver/{receiverId}")
    public ResponseEntity<List<DeletedMessageEntity>> getByReceiver(
            @PathVariable String receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("deletedAt").descending());
        Page<DeletedMessageEntity> result = deletedMessageService.getMessagesByReceiver(receiverId, pageable);
        return ResponseEntity.ok(result.getContent());
    }

    @Operation(summary = "按删除者查询", description = "获取指定删除者操作的已删除消息列表")
    @GetMapping("/by-deleter/{deletedByUserId}")
    public ResponseEntity<List<DeletedMessageEntity>> getByDeleter(
            @PathVariable String deletedByUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("deletedAt").descending());
        Page<DeletedMessageEntity> result = deletedMessageService.getMessagesByDeleter(deletedByUserId, pageable);
        return ResponseEntity.ok(result.getContent());
    }

    @Operation(summary = "按删除原因查询", description = "获取指定删除原因的已删除消息列表")
    @GetMapping("/by-reason/{deleteReason}")
    public ResponseEntity<List<DeletedMessageEntity>> getByDeleteReason(
            @PathVariable DeletedMessageEntity.DeleteReason deleteReason,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("deletedAt").descending());
        Page<DeletedMessageEntity> result = deletedMessageService.getMessagesByDeleteReason(deleteReason, pageable);
        return ResponseEntity.ok(result.getContent());
    }

    @Operation(summary = "按时间范围查询", description = "获取指定时间范围内的已删除消息")
    @GetMapping("/by-time-range")
    public ResponseEntity<List<DeletedMessageEntity>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<DeletedMessageEntity> result = deletedMessageService.advancedSearch(
            null, null, null, null, start, end);
        return ResponseEntity.ok(result);
    }

    // ========== 审核管理API ==========

    @Operation(summary = "获取待审核消息", description = "获取所有待管理员审核的已删除消息")
    @GetMapping("/pending-review")
    public ResponseEntity<List<DeletedMessageEntity>> getPendingReview() {
        List<DeletedMessageEntity> result = deletedMessageService.getPendingReviewMessages();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "获取需要审核的消息", description = "获取所有需要审核的已删除消息")
    @GetMapping("/needing-review")
    public ResponseEntity<List<DeletedMessageEntity>> getNeedingReview() {
        List<DeletedMessageEntity> result = deletedMessageService.getMessagesNeedingReview();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "审核消息", description = "审核指定的已删除消息")
    @PostMapping("/audit/{messageId}")
    public ResponseEntity<DeletedMessageEntity> auditMessage(
            @PathVariable Long messageId,
            @RequestParam DeletedMessageEntity.AuditStatus status,
            @RequestParam(required = false) String notes,
            @RequestParam String auditorId) {
        
        try {
            DeletedMessageEntity result = deletedMessageService.auditMessage(messageId, status, notes, auditorId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("Message not found: {}", messageId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to audit message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "批量审核", description = "批量审核多条已删除消息")
    @PostMapping("/batch-audit")
    public ResponseEntity<List<DeletedMessageEntity>> batchAudit(
            @RequestBody List<Long> messageIds,
            @RequestParam DeletedMessageEntity.AuditStatus status,
            @RequestParam(required = false) String notes,
            @RequestParam String auditorId) {
        
        try {
            List<DeletedMessageEntity> result = deletedMessageService.batchAuditMessages(
                messageIds, status, notes, auditorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to batch audit messages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== 清理操作API ==========

    @Operation(summary = "标记为彻底删除", description = "将已删除消息标记为彻底删除")
    @PostMapping("/permanent-delete/{messageId}")
    public ResponseEntity<DeletedMessageEntity> markAsPermanentlyDeleted(@PathVariable Long messageId) {
        try {
            DeletedMessageEntity result = deletedMessageService.markAsPermanentlyDeleted(messageId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("Message not found: {}", messageId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to mark as permanently deleted", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "批量标记彻底删除", description = "批量将消息标记为彻底删除")
    @PostMapping("/batch-permanent-delete")
    public ResponseEntity<List<DeletedMessageEntity>> batchMarkAsPermanentlyDeleted(
            @RequestBody List<Long> messageIds) {
        
        try {
            List<DeletedMessageEntity> result = deletedMessageService.batchMarkAsPermanentlyDeleted(messageIds);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to batch mark as permanently deleted", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "清理过期消息", description = "自动清理已过期的已删除消息")
    @PostMapping("/cleanup-expired")
    public ResponseEntity<Map<String, Object>> cleanupExpiredMessages() {
        try {
            int count = deletedMessageService.cleanupExpiredMessages();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully cleaned up " + count + " expired messages");
            response.put("count", count);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to cleanup expired messages", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Cleanup failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "清理旧的彻底删除记录", description = "清理旧的彻底删除记录")
    @PostMapping("/cleanup-old-permanent")
    public ResponseEntity<Map<String, Object>> cleanupOldPermanentlyDeleted() {
        try {
            int count = deletedMessageService.cleanupOldPermanentlyDeleted();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully cleaned up " + count + " old permanently deleted records");
            response.put("count", count);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to cleanup old permanently deleted records", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Cleanup failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== 统计API ==========

    @Operation(summary = "获取发送者统计", description = "获取指定发送者的已删除消息统计")
    @GetMapping("/stats/by-sender/{senderId}")
    public ResponseEntity<Map<String, Long>> getStatsBySender(@PathVariable String senderId) {
        try {
            long count = deletedMessageService.countBySender(senderId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get stats by sender", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "获取接收者统计", description = "获取指定接收者的已删除消息统计")
    @GetMapping("/stats/by-receiver/{receiverId}")
    public ResponseEntity<Map<String, Long>> getStatsByReceiver(@PathVariable String receiverId) {
        try {
            long count = deletedMessageService.countByReceiver(receiverId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get stats by receiver", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "获取删除者统计", description = "获取指定删除者的已删除消息统计")
    @GetMapping("/stats/by-deleter/{deletedByUserId}")
    public ResponseEntity<Map<String, Long>> getStatsByDeleter(@PathVariable String deletedByUserId) {
        try {
            long count = deletedMessageService.countByDeleter(deletedByUserId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get stats by deleter", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "获取时间范围统计", description = "获取指定时间范围内的已删除消息统计")
    @GetMapping("/stats/by-time-range")
    public ResponseEntity<Map<String, Long>> getStatsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        try {
            long count = deletedMessageService.countByDeleteTimeRange(start, end);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get stats by time range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "获取删除原因分布", description = "获取删除原因分布统计")
    @GetMapping("/stats/distribution/reason")
    public ResponseEntity<List<Object[]>> getDeleteReasonDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        try {
            List<Object[]> distribution = deletedMessageService.getDeleteReasonDistribution(start, end);
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            log.error("Failed to get delete reason distribution", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "获取删除类型分布", description = "获取删除者类型分布统计")
    @GetMapping("/stats/distribution/type")
    public ResponseEntity<List<Object[]>> getDeleteTypeDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        try {
            List<Object[]> distribution = deletedMessageService.getDeleteTypeDistribution(start, end);
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            log.error("Failed to get delete type distribution", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== 高级功能API ==========

    @Operation(summary = "高级搜索", description = "高级搜索已删除消息")
    @GetMapping("/advanced-search")
    public ResponseEntity<List<DeletedMessageEntity>> advancedSearch(
            @RequestParam(required = false) String senderId,
            @RequestParam(required = false) String receiverId,
            @RequestParam(required = false) DeletedMessageEntity.DeleteReason deleteReason,
            @RequestParam(required = false) DeletedMessageEntity.AuditStatus auditStatus,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            List<DeletedMessageEntity> result = deletedMessageService.advancedSearch(
                senderId, receiverId, deleteReason, auditStatus, startDate, endDate);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to perform advanced search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "导出数据", description = "导出已删除消息数据")
    @GetMapping("/export")
    public ResponseEntity<List<Object[]>> exportData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        try {
            List<Object[]> exportData = deletedMessageService.getExportData(start, end);
            return ResponseEntity.ok(exportData);
        } catch (Exception e) {
            log.error("Failed to export data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "获取审计记录", description = "获取已审核的记录")
    @GetMapping("/audited-records")
    public ResponseEntity<List<Object[]>> getAuditedRecords() {
        try {
            List<Object[]> auditedRecords = deletedMessageService.getAuditedRecords();
            return ResponseEntity.ok(auditedRecords);
        } catch (Exception e) {
            log.error("Failed to get audited records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== 管理API ==========

    @Operation(summary = "获取清理候选", description = "获取待清理的消息列表")
    @GetMapping("/cleanup-candidates")
    public ResponseEntity<List<DeletedMessageEntity>> getCleanupCandidates() {
        try {
            List<DeletedMessageEntity> candidates = deletedMessageService.getCleanupCandidates();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            log.error("Failed to get cleanup candidates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "获取短期保留消息", description = "获取保留期限较短的消息")
    @GetMapping("/short-retention")
    public ResponseEntity<List<DeletedMessageEntity>> getShortRetentionMessages(
            @RequestParam(defaultValue = "7") int daysRemaining) {
        
        try {
            List<DeletedMessageEntity> messages = deletedMessageService.getMessagesWithShortRetention(daysRemaining);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Failed to get short retention messages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== 健康检查API ==========

    @Operation(summary = "健康检查", description = "检查已删除消息服务状态")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "DeletedMessageService");
        health.put("timestamp", LocalDateTime.now());
        
        try {
            // 简单的数据库连接检查
            long totalCount = deletedMessageService.countByDeleteTimeRange(
                LocalDateTime.now().minusDays(1), LocalDateTime.now());
            health.put("recent_records", totalCount);
            health.put("database", "CONNECTED");
        } catch (Exception e) {
            health.put("database", "ERROR: " + e.getMessage());
            health.put("status", "DEGRADED");
        }
        
        return ResponseEntity.ok(health);
    }

    // ========== 错误处理 ==========

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "Bad Request");
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Unexpected error", e);
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}