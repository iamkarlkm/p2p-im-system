package com.im.backend.controller;

import com.im.backend.dto.GroupManagementLogDTO;
import com.im.backend.entity.GroupManagementLogEntity;
import com.im.backend.service.GroupManagementLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 群管理日志控制器
 * 提供群管理日志的REST API接口
 */
@RestController
@RequestMapping("/api/v1/group-management-logs")
@Tag(name = "Group Management Logs", description = "群管理日志API")
@Slf4j
@RequiredArgsConstructor
public class GroupManagementLogController {

    private final GroupManagementLogService logService;

    /**
     * 记录单个操作日志
     */
    @Operation(summary = "记录群管理操作日志", description = "记录群组成员变动、管理员操作等日志")
    @PostMapping
    public ResponseEntity<GroupManagementLogEntity> logOperation(
            @Valid @RequestBody GroupManagementLogDTO logDTO) {
        log.info("Recording group management log: groupId={}, actionType={}", 
                 logDTO.getGroupId(), logDTO.getActionType());
        
        GroupManagementLogEntity savedLog = logService.logOperation(logDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLog);
    }

    /**
     * 批量记录操作日志
     */
    @Operation(summary = "批量记录群管理操作日志", description = "批量记录群管理操作日志")
    @PostMapping("/batch")
    public ResponseEntity<List<GroupManagementLogEntity>> batchLogOperations(
            @Valid @RequestBody List<GroupManagementLogDTO> logDTOs) {
        log.info("Batch recording group management logs: count={}", logDTOs.size());
        
        List<GroupManagementLogEntity> savedLogs = logService.batchLogOperations(logDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLogs);
    }

    /**
     * 根据ID查询日志
     */
    @Operation(summary = "根据ID查询日志", description = "根据日志ID查询详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<GroupManagementLogEntity> getLogById(
            @Parameter(description = "日志ID") @PathVariable UUID id) {
        return logService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据群组ID查询日志
     */
    @Operation(summary = "根据群组ID查询日志", description = "查询指定群组的所有管理操作日志")
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GroupManagementLogEntity>> getLogsByGroupId(
            @Parameter(description = "群组ID") @PathVariable UUID groupId) {
        List<GroupManagementLogEntity> logs = logService.findByGroupId(groupId);
        return ResponseEntity.ok(logs);
    }

    /**
     * 根据群组ID分页查询日志
     */
    @Operation(summary = "根据群组ID分页查询日志", description = "分页查询指定群组的管理操作日志")
    @GetMapping("/group/{groupId}/page")
    public ResponseEntity<Page<GroupManagementLogEntity>> getLogsByGroupIdPage(
            @Parameter(description = "群组ID") @PathVariable UUID groupId,
            Pageable pageable) {
        Page<GroupManagementLogEntity> logs = logService.findByGroupId(groupId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * 根据操作者ID查询日志
     */
    @Operation(summary = "根据操作者ID查询日志", description = "查询指定操作者的所有管理操作日志")
    @GetMapping("/operator/{operatorId}")
    public ResponseEntity<List<GroupManagementLogEntity>> getLogsByOperatorId(
            @Parameter(description = "操作者ID") @PathVariable UUID operatorId) {
        List<GroupManagementLogEntity> logs = logService.findByOperatorId(operatorId);
        return ResponseEntity.ok(logs);
    }

    /**
     * 根据目标用户ID查询日志
     */
    @Operation(summary = "根据目标用户ID查询日志", description = "查询指定目标用户被操作的所有日志")
    @GetMapping("/target/{targetUserId}")
    public ResponseEntity<List<GroupManagementLogEntity>> getLogsByTargetUserId(
            @Parameter(description = "目标用户ID") @PathVariable UUID targetUserId) {
        List<GroupManagementLogEntity> logs = logService.findByTargetUserId(targetUserId);
        return ResponseEntity.ok(logs);
    }

    /**
     * 根据操作类型查询日志
     */
    @Operation(summary = "根据操作类型查询日志", description = "查询指定操作类型的所有日志")
    @GetMapping("/action/{actionType}")
    public ResponseEntity<List<GroupManagementLogEntity>> getLogsByActionType(
            @Parameter(description = "操作类型") @PathVariable String actionType) {
        List<GroupManagementLogEntity> logs = logService.findByActionType(actionType);
        return ResponseEntity.ok(logs);
    }

    /**
     * 根据时间范围查询日志
     */
    @Operation(summary = "根据时间范围查询日志", description = "查询指定时间范围内的所有日志")
    @GetMapping("/time-range")
    public ResponseEntity<List<GroupManagementLogEntity>> getLogsByTimeRange(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<GroupManagementLogEntity> logs = logService.findByTimeRange(start, end);
        return ResponseEntity.ok(logs);
    }

    /**
     * 高级搜索日志
     */
    @Operation(summary = "高级搜索日志", description = "根据多个条件高级搜索日志")
    @GetMapping("/search")
    public ResponseEntity<Page<GroupManagementLogEntity>> searchLogs(
            @Parameter(description = "群组ID") @RequestParam(required = false) UUID groupId,
            @Parameter(description = "操作者ID") @RequestParam(required = false) UUID operatorId,
            @Parameter(description = "目标用户ID") @RequestParam(required = false) UUID targetUserId,
            @Parameter(description = "操作类型") @RequestParam(required = false) String actionType,
            @Parameter(description = "操作子类型") @RequestParam(required = false) String actionSubType,
            @Parameter(description = "操作结果") @RequestParam(required = false) String result,
            @Parameter(description = "是否重要") @RequestParam(required = false) Boolean important,
            @Parameter(description = "是否需要通知") @RequestParam(required = false) Boolean needNotification,
            @Parameter(description = "是否已通知") @RequestParam(required = false) Boolean notified,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        
        Page<GroupManagementLogEntity> logs = logService.advancedSearch(
            groupId, operatorId, targetUserId, actionType, actionSubType, result,
            important, needNotification, notified, startDate, endDate, pageable
        );
        return ResponseEntity.ok(logs);
    }

    /**
     * 获取群组最近的操作日志
     */
    @Operation(summary = "获取群组最近的操作日志", description = "获取指定群组最近的N条操作日志")
    @GetMapping("/group/{groupId}/recent")
    public ResponseEntity<List<GroupManagementLogEntity>> getRecentLogsByGroupId(
            @Parameter(description = "群组ID") @PathVariable UUID groupId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") int limit) {
        List<GroupManagementLogEntity> logs = logService.getRecentLogsByGroupId(groupId, limit);
        return ResponseEntity.ok(logs);
    }

    /**
     * 获取需要通知的日志
     */
    @Operation(summary = "获取需要通知的日志", description = "获取所有需要通知但未通知的日志")
    @GetMapping("/pending-notifications")
    public ResponseEntity<List<GroupManagementLogEntity>> getPendingNotificationLogs() {
        List<GroupManagementLogEntity> logs = logService.getPendingNotificationLogs();
        return ResponseEntity.ok(logs);
    }

    /**
     * 获取重要操作日志
     */
    @Operation(summary = "获取重要操作日志", description = "获取所有标记为重要的操作日志")
    @GetMapping("/important")
    public ResponseEntity<List<GroupManagementLogEntity>> getImportantLogs() {
        List<GroupManagementLogEntity> logs = logService.getImportantLogs();
        return ResponseEntity.ok(logs);
    }

    /**
     * 标记日志为已通知
     */
    @Operation(summary = "标记日志为已通知", description = "批量标记日志为已通知状态")
    @PutMapping("/mark-notified")
    public ResponseEntity<Integer> markLogsAsNotified(
            @Parameter(description = "日志ID列表") @RequestBody List<UUID> logIds) {
        int updated = logService.markAsNotified(logIds);
        return ResponseEntity.ok(updated);
    }

    /**
     * 获取操作统计信息
     */
    @Operation(summary = "获取操作统计信息", description = "获取全局操作统计信息")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = logService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取群组操作统计信息
     */
    @Operation(summary = "获取群组操作统计信息", description = "获取指定群组的操作统计信息")
    @GetMapping("/statistics/group/{groupId}")
    public ResponseEntity<Map<String, Object>> getStatisticsByGroupId(
            @Parameter(description = "群组ID") @PathVariable UUID groupId) {
        Map<String, Object> stats = logService.getStatisticsByGroupId(groupId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取热门操作类型
     */
    @Operation(summary = "获取热门操作类型", description = "获取最常出现的操作类型统计")
    @GetMapping("/statistics/top-actions")
    public ResponseEntity<List<Map<String, Object>>> getTopActionTypes() {
        List<Map<String, Object>> topActions = logService.getTopActionTypes();
        return ResponseEntity.ok(topActions);
    }

    /**
     * 获取活跃操作者
     */
    @Operation(summary = "获取活跃操作者", description = "获取操作最频繁的操作者统计")
    @GetMapping("/statistics/top-operators")
    public ResponseEntity<List<Map<String, Object>>> getTopOperators() {
        List<Map<String, Object>> topOperators = logService.getTopOperators();
        return ResponseEntity.ok(topOperators);
    }

    /**
     * 导出日志为CSV格式
     */
    @Operation(summary = "导出日志为CSV格式", description = "导出指定条件的日志为CSV文件")
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportToCsv(
            @Parameter(description = "群组ID") @RequestParam(required = false) UUID groupId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<GroupManagementLogEntity> logs;
        if (groupId != null && startDate != null && endDate != null) {
            logs = logService.findByGroupIdAndTimeRange(groupId, startDate, endDate);
        } else if (groupId != null) {
            logs = logService.findByGroupId(groupId);
        } else if (startDate != null && endDate != null) {
            logs = logService.findByTimeRange(startDate, endDate);
        } else {
            logs = logService.findByGroupId(null); // 需要实现获取所有日志的方法
        }
        
        String csvContent = logService.exportToCsv(logs);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "group-management-logs.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent.getBytes());
    }

    /**
     * 导出日志为JSON格式
     */
    @Operation(summary = "导出日志为JSON格式", description = "导出指定条件的日志为JSON文件")
    @GetMapping("/export/json")
    public ResponseEntity<byte[]> exportToJson(
            @Parameter(description = "群组ID") @RequestParam(required = false) UUID groupId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<GroupManagementLogEntity> logs;
        if (groupId != null && startDate != null && endDate != null) {
            logs = logService.findByGroupIdAndTimeRange(groupId, startDate, endDate);
        } else if (groupId != null) {
            logs = logService.findByGroupId(groupId);
        } else if (startDate != null && endDate != null) {
            logs = logService.findByTimeRange(startDate, endDate);
        } else {
            logs = logService.findByGroupId(null); // 需要实现获取所有日志的方法
        }
        
        try {
            String jsonContent = logService.exportToJson(logs);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "group-management-logs.json");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(jsonContent.getBytes());
        } catch (Exception e) {
            log.error("Failed to export logs to JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 批量归档日志
     */
    @Operation(summary = "批量归档日志", description = "批量归档指定的日志")
    @PutMapping("/archive")
    public ResponseEntity<Integer> archiveLogs(
            @Parameter(description = "日志ID列表") @RequestBody List<UUID> logIds) {
        int archived = logService.archiveLogs(logIds);
        return ResponseEntity.ok(archived);
    }

    /**
     * 清理已归档的旧日志
     */
    @Operation(summary = "清理已归档的旧日志", description = "清理指定时间之前的已归档日志")
    @DeleteMapping("/cleanup")
    public ResponseEntity<Integer> cleanupArchivedLogs(
            @Parameter(description = "截止时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cutoffDate) {
        int deleted = logService.cleanupArchivedLogs(cutoffDate);
        return ResponseEntity.ok(deleted);
    }

    /**
     * 检查重复操作
     */
    @Operation(summary = "检查重复操作", description = "检查在指定时间内是否有重复的相同操作")
    @GetMapping("/check-duplicate")
    public ResponseEntity<Boolean> checkDuplicateOperation(
            @Parameter(description = "群组ID") @RequestParam UUID groupId,
            @Parameter(description = "操作者ID") @RequestParam UUID operatorId,
            @Parameter(description = "操作类型") @RequestParam String actionType,
            @Parameter(description = "目标用户ID") @RequestParam(required = false) UUID targetUserId,
            @Parameter(description = "时间范围(分钟)") @RequestParam(defaultValue = "5") int withinMinutes) {
        
        LocalDateTime within = LocalDateTime.now().minusMinutes(withinMinutes);
        boolean isDuplicate = logService.checkDuplicateOperation(groupId, operatorId, actionType, targetUserId, within);
        return ResponseEntity.ok(isDuplicate);
    }

    /**
     * 健康检查端点
     */
    @Operation(summary = "健康检查", description = "检查群管理日志服务是否正常")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = Map.of(
            "status", "UP",
            "service", "GroupManagementLogService",
            "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(health);
    }
}