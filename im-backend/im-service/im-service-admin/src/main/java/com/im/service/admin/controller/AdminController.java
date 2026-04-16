package com.im.service.admin.controller;

import com.im.service.admin.entity.AdminLog;
import com.im.service.admin.service.AdminService;
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
 * 管理员控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 记录操作日志
     */
    @PostMapping("/logs")
    public ResponseEntity<Map<String, Object>> createLog(@RequestBody AdminLog adminLog) {
        log.info("Creating admin log: {} - {}", adminLog.getAdminId(), adminLog.getOperationType());
        
        AdminLog saved = adminService.logOperation(adminLog);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "日志记录成功");
        result.put("data", saved);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 获取日志详情
     */
    @GetMapping("/logs/{logId}")
    public ResponseEntity<Map<String, Object>> getLogById(@PathVariable Long logId) {
        return adminService.getLogById(logId)
                .map(log -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("data", log);
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "日志不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                });
    }

    /**
     * 分页查询日志
     */
    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getLogs(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String filterResult,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AdminLog> logPage = adminService.getLogs(
                adminId, module, operationType, filterResult, startTime, endTime, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", logPage.getContent());
        response.put("totalElements", logPage.getTotalElements());
        response.put("totalPages", logPage.getTotalPages());
        response.put("currentPage", logPage.getNumber());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取管理员操作日志
     */
    @GetMapping("/logs/admin/{adminId}")
    public ResponseEntity<Map<String, Object>> getAdminLogs(
            @PathVariable Long adminId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminLog> logPage = adminService.getAdminLogs(adminId, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", logPage.getContent());
        result.put("totalElements", logPage.getTotalElements());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取最近的日志
     */
    @GetMapping("/logs/recent")
    public ResponseEntity<Map<String, Object>> getRecentLogs(
            @RequestParam(defaultValue = "20") int limit) {
        
        List<AdminLog> logs = adminService.getRecentLogs(limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", logs);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取最近登录记录
     */
    @GetMapping("/logs/logins")
    public ResponseEntity<Map<String, Object>> getRecentLogins(
            @RequestParam(defaultValue = "20") int limit) {
        
        List<AdminLog> logs = adminService.getRecentLogins(limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", logs);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取失败的操作
     */
    @GetMapping("/logs/failed")
    public ResponseEntity<Map<String, Object>> getFailedOperations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<AdminLog> failedLogs = adminService.getFailedOperations(pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", failedLogs);
        result.put("totalElements", failedLogs.size());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 统计操作（按模块）
     */
    @GetMapping("/statistics/by-module")
    public ResponseEntity<Map<String, Object>> getStatisticsByModule(
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Map<String, Long> stats = adminService.getOperationCountByModule(since);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 统计操作（按类型）
     */
    @GetMapping("/statistics/by-type")
    public ResponseEntity<Map<String, Object>> getStatisticsByType(
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Map<String, Long> stats = adminService.getOperationCountByType(since);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 统计操作结果
     */
    @GetMapping("/statistics/by-result")
    public ResponseEntity<Map<String, Object>> getStatisticsByResult(
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Map<String, Long> stats = adminService.getOperationResultCount(since);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取平均操作耗时
     */
    @GetMapping("/statistics/duration")
    public ResponseEntity<Map<String, Object>> getAverageDuration(
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Double avgDuration = adminService.getAverageDuration(since);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("averageDuration", avgDuration != null ? avgDuration : 0);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取管理员的最后一次登录
     */
    @GetMapping("/logs/last-login/{adminId}")
    public ResponseEntity<Map<String, Object>> getLastLogin(@PathVariable Long adminId) {
        
        return adminService.getLastLogin(adminId)
                .map(log -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("data", log);
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "未找到登录记录");
                    return ResponseEntity.ok(result);
                });
    }

    /**
     * 获取管理员操作统计
     */
    @GetMapping("/statistics/admin/{adminId}")
    public ResponseEntity<Map<String, Object>> getAdminStatistics(@PathVariable Long adminId) {
        
        AdminService.AdminStatistics stats = adminService.getAdminStatistics(adminId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取系统统计信息
     */
    @GetMapping("/statistics/system")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        
        AdminService.SystemStatistics stats = adminService.getSystemStatistics();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 删除旧日志
     */
    @DeleteMapping("/logs/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldLogs(
            @RequestParam(defaultValue = "90") int daysBefore) {
        
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(daysBefore);
        int count = adminService.deleteOldLogs(beforeTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "成功删除 " + count + " 条旧日志");
        result.put("deletedCount", count);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "admin");
        return ResponseEntity.ok(result);
    }
}
