package com.im.backend.controller;

import com.im.backend.entity.SmartMessageSummaryEntity;
import com.im.backend.enums.SummaryQuality;
import com.im.backend.enums.SummaryStatus;
import com.im.backend.enums.SummaryType;
import com.im.backend.service.SmartMessageSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 智能消息摘要 REST API 控制器
 * 提供完整的摘要 CRUD、生成、统计和管理 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/smart-summary")
@RequiredArgsConstructor
public class SmartMessageSummaryController {

    private final SmartMessageSummaryService summaryService;

    // ==================== CRUD 操作 ====================

    /**
     * 创建摘要
     * POST /api/v1/smart-summary
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSummary(@RequestBody Map<String, Object> request) {
        log.info("创建摘要请求: {}", request);

        try {
            SmartMessageSummaryEntity summary = new SmartMessageSummaryEntity();
            summary.setSessionId((String) request.get("sessionId"));
            summary.setUserId((String) request.get("userId"));
            summary.setMessageId((String) request.get("messageId"));
            
            String typeStr = (String) request.get("summaryType");
            if (typeStr != null) {
                summary.setSummaryType(SummaryType.fromCode(typeStr));
            }
            
            summary.setSummaryContent((String) request.get("summaryContent"));
            summary.setOriginalContent((String) request.get("originalContent"));
            summary.setLanguageCode((String) request.get("languageCode"));
            summary.setSummaryStyle((String) request.get("summaryStyle"));
            summary.setTargetLength((Integer) request.get("targetLength"));

            SmartMessageSummaryEntity created = summaryService.createSummary(summary);

            return ResponseEntity.ok(buildSuccessResponse(created, "摘要创建成功"));
        } catch (Exception e) {
            log.error("创建摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取摘要详情
     * GET /api/v1/smart-summary/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long id) {
        log.info("获取摘要详情: id={}", id);

        return summaryService.getSummaryById(id)
                .map(summary -> ResponseEntity.ok(buildSuccessResponse(summary, null)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取摘要（验证用户权限）
     * GET /api/v1/smart-summary/{id}/user/{userId}
     */
    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<Map<String, Object>> getSummaryByUser(@PathVariable Long id, 
                                                                @PathVariable String userId) {
        log.info("获取摘要详情 (用户验证): id={}, userId={}", id, userId);

        return summaryService.getSummaryByIdAndUser(id, userId)
                .map(summary -> ResponseEntity.ok(buildSuccessResponse(summary, null)))
                .orElse(ResponseEntity.status(403).body(buildErrorResponse("无权访问该摘要")));
    }

    /**
     * 更新摘要
     * PUT /api/v1/smart-summary/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSummary(@PathVariable Long id,
                                                             @RequestBody Map<String, Object> request) {
        log.info("更新摘要: id={}", id);

        try {
            SmartMessageSummaryEntity updateData = new SmartMessageSummaryEntity();
            
            if (request.containsKey("summaryContent")) {
                updateData.setSummaryContent((String) request.get("summaryContent"));
            }
            if (request.containsKey("status")) {
                updateData.setStatus(SummaryStatus.fromCode((String) request.get("status")));
            }
            if (request.containsKey("quality")) {
                updateData.setQuality(SummaryQuality.fromCode((String) request.get("quality")));
            }
            if (request.containsKey("qualityScore")) {
                updateData.setQualityScore((Integer) request.get("qualityScore"));
            }
            if (request.containsKey("summaryStyle")) {
                updateData.setSummaryStyle((String) request.get("summaryStyle"));
            }
            if (request.containsKey("userRating")) {
                updateData.setUserRating((Integer) request.get("userRating"));
            }
            if (request.containsKey("userFeedback")) {
                updateData.setUserFeedback((String) request.get("userFeedback"));
            }
            if (request.containsKey("isFavorite")) {
                updateData.setIsFavorite((Boolean) request.get("isFavorite"));
            }

            SmartMessageSummaryEntity updated = summaryService.updateSummary(id, updateData);
            return ResponseEntity.ok(buildSuccessResponse(updated, "摘要更新成功"));
        } catch (Exception e) {
            log.error("更新摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 删除摘要（逻辑删除）
     * DELETE /api/v1/smart-summary/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSummary(@PathVariable Long id) {
        log.info("删除摘要: id={}", id);

        try {
            summaryService.deleteSummary(id);
            return ResponseEntity.ok(buildSuccessResponse(null, "摘要删除成功"));
        } catch (Exception e) {
            log.error("删除摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    // ==================== 查询接口 ====================

    /**
     * 查询用户摘要列表
     * GET /api/v1/smart-summary/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserSummaries(@PathVariable String userId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        log.info("查询用户摘要: userId={}, page={}, size={}", userId, page, size);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SmartMessageSummaryEntity> summaries = summaryService.getUserSummaries(userId, pageable);

            Map<String, Object> data = new HashMap<>();
            data.put("summaries", summaries.getContent());
            data.put("pagination", buildPaginationInfo(summaries));

            return ResponseEntity.ok(buildSuccessResponse(data, null));
        } catch (Exception e) {
            log.error("查询用户摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 查询会话摘要列表
     * GET /api/v1/smart-summary/session/{sessionId}
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSessionSummaries(@PathVariable String sessionId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "20") int size) {
        log.info("查询会话摘要: sessionId={}, page={}, size={}", sessionId, page, size);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SmartMessageSummaryEntity> summaries = summaryService.getSessionSummaries(sessionId, pageable);

            Map<String, Object> data = new HashMap<>();
            data.put("summaries", summaries.getContent());
            data.put("pagination", buildPaginationInfo(summaries));

            return ResponseEntity.ok(buildSuccessResponse(data, null));
        } catch (Exception e) {
            log.error("查询会话摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 查询指定状态的摘要
     * GET /api/v1/smart-summary/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getSummariesByStatus(@PathVariable String status,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "20") int size) {
        log.info("查询指定状态摘要: status={}, page={}, size={}", status, page, size);

        try {
            SummaryStatus summaryStatus = SummaryStatus.fromCode(status);
            Pageable pageable = PageRequest.of(page, size);
            Page<SmartMessageSummaryEntity> summaries = summaryService.getSummariesByStatus(summaryStatus, pageable);

            Map<String, Object> data = new HashMap<>();
            data.put("summaries", summaries.getContent());
            data.put("pagination", buildPaginationInfo(summaries));

            return ResponseEntity.ok(buildSuccessResponse(data, null));
        } catch (Exception e) {
            log.error("查询状态摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 搜索摘要
     * GET /api/v1/smart-summary/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSummaries(@RequestParam String keyword,
                                                               @RequestParam(required = false) String userId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        log.info("搜索摘要: keyword={}, userId={}, page={}, size={}", keyword, userId, page, size);

        try {
            Pageable pageable = PageRequest.of(page, size);
            List<SmartMessageSummaryEntity> summaries;
            
            if (userId != null) {
                summaries = summaryService.searchUserSummaries(keyword, userId);
            } else {
                summaries = summaryService.searchSummaries(keyword);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("summaries", summaries.stream()
                    .skip(page * size)
                    .limit(size)
                    .collect(Collectors.toList()));
            data.put("total", summaries.size());
            data.put("page", page);
            data.put("size", size);

            return ResponseEntity.ok(buildSuccessResponse(data, null));
        } catch (Exception e) {
            log.error("搜索摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    // ==================== 摘要生成接口 ====================

    /**
     * 生成消息摘要
     * POST /api/v1/smart-summary/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateSummary(@RequestBody Map<String, Object> request) {
        log.info("生成摘要请求: {}", request);

        try {
            String sessionId = (String) request.get("sessionId");
            String userId = (String) request.get("userId");
            String typeStr = (String) request.get("summaryType");
            String originalContent = (String) request.get("originalContent");

            if (sessionId == null || userId == null || originalContent == null) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("缺少必要参数：sessionId, userId, originalContent"));
            }

            SummaryType summaryType = typeStr != null ? SummaryType.fromCode(typeStr) : SummaryType.SINGLE_MESSAGE;

            SmartMessageSummaryEntity summary = summaryService.generateSummary(sessionId, userId, summaryType, originalContent);

            return ResponseEntity.ok(buildSuccessResponse(summary, "摘要生成成功"));
        } catch (Exception e) {
            log.error("生成摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 重新生成摘要
     * POST /api/v1/smart-summary/{id}/regenerate
     */
    @PostMapping("/{id}/regenerate")
    public ResponseEntity<Map<String, Object>> regenerateSummary(@PathVariable Long id) {
        log.info("重新生成摘要: id={}", id);

        try {
            SmartMessageSummaryEntity summary = summaryService.regenerateSummary(id);
            return ResponseEntity.ok(buildSuccessResponse(summary, "摘要重新生成成功"));
        } catch (Exception e) {
            log.error("重新生成摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    // ==================== 批量操作接口 ====================

    /**
     * 批量更新状态
     * PUT /api/v1/smart-summary/batch/status
     */
    @PutMapping("/batch/status")
    public ResponseEntity<Map<String, Object>> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        log.info("批量更新状态: {}", request);

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            String statusStr = (String) request.get("status");

            if (ids == null || ids.isEmpty() || statusStr == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("缺少必要参数"));
            }

            SummaryStatus status = SummaryStatus.fromCode(statusStr);
            int updated = summaryService.updateSummariesStatus(ids, status);

            Map<String, Object> data = new HashMap<>();
            data.put("updatedCount", updated);

            return ResponseEntity.ok(buildSuccessResponse(data, "批量更新成功"));
        } catch (Exception e) {
            log.error("批量更新状态失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 批量标记为已读
     * PUT /api/v1/smart-summary/batch/read
     */
    @PutMapping("/batch/read")
    public ResponseEntity<Map<String, Object>> batchMarkAsRead(@RequestBody Map<String, Object> request) {
        log.info("批量标记已读: {}", request);

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");

            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(buildErrorResponse("缺少必要参数"));
            }

            int updated = summaryService.markSummariesAsRead(ids);

            Map<String, Object> data = new HashMap<>();
            data.put("updatedCount", updated);

            return ResponseEntity.ok(buildSuccessResponse(data, "批量标记成功"));
        } catch (Exception e) {
            log.error("批量标记已读失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    // ==================== 统计接口 ====================

    /**
     * 获取用户摘要统计
     * GET /api/v1/smart-summary/stats/user/{userId}
     */
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String userId) {
        log.info("获取用户统计: userId={}", userId);

        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", summaryService.countUserSummaries(userId));
            stats.put("completedCount", summaryService.countUserSummariesByStatus(userId, SummaryStatus.COMPLETED));
            stats.put("pendingCount", summaryService.countUserSummariesByStatus(userId, SummaryStatus.PENDING));
            stats.put("highQualityCount", summaryService.countUserSummariesByQuality(userId, SummaryQuality.HIGH));
            stats.put("excellentCount", summaryService.countUserSummariesByQuality(userId, SummaryQuality.EXCELLENT));
            stats.put("qualityDistribution", summaryService.getUserQualityDistribution(userId));
            stats.put("styleStats", summaryService.getUserStyleStats(userId));

            return ResponseEntity.ok(buildSuccessResponse(stats, null));
        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取系统统计
     * GET /api/v1/smart-summary/stats/system
     */
    @GetMapping("/stats/system")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        log.info("获取系统统计");

        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSummaries", summaryService.countSummariesNeedingRegeneration() + 
                    summaryService.countUserSummaries("system"));
            stats.put("needsRegenerationCount", summaryService.countSummariesNeedingRegeneration());
            stats.put("expiredCacheCount", summaryService.countExpiredCacheSummaries());
            stats.put("highQualitySummaries", summaryService.getHighQualitySummaries().size());
            stats.put("lowQualitySummaries", summaryService.getLowQualitySummaries().size());

            return ResponseEntity.ok(buildSuccessResponse(stats, null));
        } catch (Exception e) {
            log.error("获取系统统计失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    // ==================== 清理和维护接口 ====================

    /**
     * 清理过期缓存
     * POST /api/v1/smart-summary/maintenance/cleanup-cache
     */
    @PostMapping("/maintenance/cleanup-cache")
    public ResponseEntity<Map<String, Object>> cleanupExpiredCache() {
        log.info("清理过期缓存");

        try {
            int cleaned = summaryService.cleanupExpiredCache();
            Map<String, Object> data = new HashMap<>();
            data.put("cleanedCount", cleaned);

            return ResponseEntity.ok(buildSuccessResponse(data, "清理完成"));
        } catch (Exception e) {
            log.error("清理过期缓存失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 归档旧摘要
     * POST /api/v1/smart-summary/maintenance/archive
     */
    @PostMapping("/maintenance/archive")
    public ResponseEntity<Map<String, Object>> archiveOldSummaries(@RequestBody Map<String, Object> request) {
        log.info("归档旧摘要: {}", request);

        try {
            String beforeTimeStr = (String) request.get("beforeTime");
            LocalDateTime beforeTime = LocalDateTime.parse(beforeTimeStr);

            int archived = summaryService.archiveOldSummaries(beforeTime);
            Map<String, Object> data = new HashMap<>();
            data.put("archivedCount", archived);

            return ResponseEntity.ok(buildSuccessResponse(data, "归档完成"));
        } catch (Exception e) {
            log.error("归档旧摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 标记低质量摘要需要重新生成
     * POST /api/v1/smart-summary/maintenance/mark-regen
     */
    @PostMapping("/maintenance/mark-regen")
    public ResponseEntity<Map<String, Object>> markLowQualityForRegeneration() {
        log.info("标记低质量摘要");

        try {
            int marked = summaryService.markLowQualityForRegeneration();
            Map<String, Object> data = new HashMap<>();
            data.put("markedCount", marked);

            return ResponseEntity.ok(buildSuccessResponse(data, "标记完成"));
        } catch (Exception e) {
            log.error("标记低质量摘要失败", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        }
    }

    // ==================== 辅助方法 ====================

    private Map<String, Object> buildSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        if (message != null) {
            response.put("message", message);
        }
        return response;
    }

    private Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }

    private Map<String, Object> buildPaginationInfo(Page<?> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("currentPage", page.getNumber());
        pagination.put("pageSize", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        return pagination;
    }
}