package com.im.backend.controller;

import com.im.backend.entity.CollaborationAIEntity;
import com.im.backend.service.CollaborationAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协作增强 AI 助手 REST API 控制器
 * 提供协作 AI 配置的完整管理接口
 */
@RestController
@RequestMapping("/api/v1/collaboration-ai")
@RequiredArgsConstructor
@Tag(name = "协作增强AI助手", description = "协作增强AI助手配置管理API")
public class CollaborationAIController {

    private final CollaborationAIService collaborationAIService;

    /**
     * 创建新的协作 AI 配置
     */
    @PostMapping
    @Operation(summary = "创建协作AI配置", description = "创建新的协作增强AI助手配置")
    public ResponseEntity<CollaborationAIEntity> createCollaborationAI(
            @RequestBody CollaborationAIEntity collaborationAI) {
        try {
            CollaborationAIEntity created = collaborationAIService.createCollaborationAI(collaborationAI);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取协作 AI 配置
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取协作AI配置", description = "根据ID获取协作增强AI助手配置")
    public ResponseEntity<CollaborationAIEntity> getCollaborationAI(
            @PathVariable Long id) {
        try {
            CollaborationAIEntity collaborationAI = collaborationAIService.getCollaborationAI(id);
            return ResponseEntity.ok(collaborationAI);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据会话ID获取协作 AI 配置
     */
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "根据会话ID获取配置", description = "根据会话ID获取协作增强AI助手配置")
    public ResponseEntity<CollaborationAIEntity> getCollaborationAIBySessionId(
            @PathVariable String sessionId) {
        try {
            CollaborationAIEntity collaborationAI = collaborationAIService.getCollaborationAIBySessionId(sessionId);
            return ResponseEntity.ok(collaborationAI);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 更新协作 AI 配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新协作AI配置", description = "更新现有的协作增强AI助手配置")
    public ResponseEntity<CollaborationAIEntity> updateCollaborationAI(
            @PathVariable Long id,
            @RequestBody CollaborationAIEntity updates) {
        try {
            CollaborationAIEntity updated = collaborationAIService.updateCollaborationAI(id, updates);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 删除协作 AI 配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除协作AI配置", description = "删除指定的协作增强AI助手配置")
    public ResponseEntity<Void> deleteCollaborationAI(
            @PathVariable Long id) {
        try {
            collaborationAIService.deleteCollaborationAI(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取用户的所有协作 AI 配置
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户配置列表", description = "获取指定用户的所有协作增强AI助手配置")
    public ResponseEntity<List<CollaborationAIEntity>> getUserCollaborationAIs(
            @PathVariable String userId) {
        try {
            List<CollaborationAIEntity> collaborationAIs = collaborationAIService.getUserCollaborationAIs(userId);
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取群组的所有协作 AI 配置
     */
    @GetMapping("/group/{groupId}")
    @Operation(summary = "获取群组配置列表", description = "获取指定群组的所有协作增强AI助手配置")
    public ResponseEntity<List<CollaborationAIEntity>> getGroupCollaborationAIs(
            @PathVariable String groupId) {
        try {
            List<CollaborationAIEntity> collaborationAIs = collaborationAIService.getGroupCollaborationAIs(groupId);
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取所有启用的协作 AI 配置
     */
    @GetMapping("/enabled")
    @Operation(summary = "获取启用配置列表", description = "获取所有启用的协作增强AI助手配置")
    public ResponseEntity<List<CollaborationAIEntity>> getAllEnabledCollaborationAIs() {
        try {
            List<CollaborationAIEntity> collaborationAIs = collaborationAIService.getAllEnabledCollaborationAIs();
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 分页获取协作 AI 配置
     */
    @GetMapping
    @Operation(summary = "分页获取配置", description = "分页获取协作增强AI助手配置")
    public ResponseEntity<Page<CollaborationAIEntity>> getCollaborationAIs(
            Pageable pageable) {
        try {
            Page<CollaborationAIEntity> collaborationAIs = collaborationAIService.getCollaborationAIs(pageable);
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 分析协作会议并生成纪要
     */
    @PostMapping("/{id}/analyze-meeting")
    @Operation(summary = "分析会议生成纪要", description = "分析协作会议内容并自动生成会议纪要")
    public ResponseEntity<CollaborationAIEntity> analyzeMeetingAndGenerateMinutes(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String conversationContent = request.get("conversationContent");
            if (conversationContent == null || conversationContent.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            CollaborationAIEntity updated = collaborationAIService.analyzeMeetingAndGenerateMinutes(id, conversationContent);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 跟踪项目进度
     */
    @PostMapping("/{id}/track-progress")
    @Operation(summary = "跟踪项目进度", description = "跟踪和分析项目进度")
    public ResponseEntity<CollaborationAIEntity> trackProjectProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Object> projectData) {
        try {
            CollaborationAIEntity updated = collaborationAIService.trackProjectProgress(id, projectData);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 识别任务分配
     */
    @PostMapping("/{id}/identify-tasks")
    @Operation(summary = "识别任务分配", description = "识别协作中的任务分配情况")
    public ResponseEntity<CollaborationAIEntity> identifyTaskAssignments(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> participantMessages = (List<String>) request.get("participantMessages");
            if (participantMessages == null) {
                return ResponseEntity.badRequest().body(null);
            }
            CollaborationAIEntity updated = collaborationAIService.identifyTaskAssignments(id, participantMessages);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 分析协作模式
     */
    @PostMapping("/{id}/analyze-patterns")
    @Operation(summary = "分析协作模式", description = "分析团队的协作模式和效率")
    public ResponseEntity<CollaborationAIEntity> analyzeCollaborationPatterns(
            @PathVariable Long id,
            @RequestBody Map<String, Object> collaborationMetrics) {
        try {
            CollaborationAIEntity updated = collaborationAIService.analyzeCollaborationPatterns(id, collaborationMetrics);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 提供实时协作建议
     */
    @PostMapping("/{id}/provide-suggestions")
    @Operation(summary = "提供实时建议", description = "根据当前上下文提供实时协作建议")
    public ResponseEntity<CollaborationAIEntity> provideRealtimeSuggestions(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String currentContext = request.get("currentContext");
            if (currentContext == null || currentContext.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            CollaborationAIEntity updated = collaborationAIService.provideRealtimeSuggestions(id, currentContext);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 生成个性化效率报告
     */
    @PostMapping("/{id}/generate-report")
    @Operation(summary = "生成效率报告", description = "生成个性化的协作效率报告")
    public ResponseEntity<CollaborationAIEntity> generateEfficiencyReport(
            @PathVariable Long id) {
        try {
            CollaborationAIEntity updated = collaborationAIService.generateEfficiencyReport(id);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 构建团队知识库
     */
    @PostMapping("/{id}/build-knowledge")
    @Operation(summary = "构建团队知识库", description = "构建和聚合团队知识库")
    public ResponseEntity<CollaborationAIEntity> buildTeamKnowledge(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> knowledgeSources = (List<String>) request.get("knowledgeSources");
            if (knowledgeSources == null) {
                return ResponseEntity.badRequest().body(null);
            }
            CollaborationAIEntity updated = collaborationAIService.buildTeamKnowledge(id, knowledgeSources);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 识别协作瓶颈
     */
    @PostMapping("/{id}/identify-bottlenecks")
    @Operation(summary = "识别协作瓶颈", description = "识别和分析协作中的瓶颈问题")
    public ResponseEntity<CollaborationAIEntity> identifyBottlenecks(
            @PathVariable Long id,
            @RequestBody Map<String, Object> performanceData) {
        try {
            CollaborationAIEntity updated = collaborationAIService.identifyBottlenecks(id, performanceData);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 优化角色分配
     */
    @PostMapping("/{id}/optimize-roles")
    @Operation(summary = "优化角色分配", description = "优化团队角色分配建议")
    public ResponseEntity<CollaborationAIEntity> optimizeRoleAllocation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> participantSkills = (List<Map<String, Object>>) request.get("participantSkills");
            if (participantSkills == null) {
                return ResponseEntity.badRequest().body(null);
            }
            CollaborationAIEntity updated = collaborationAIService.optimizeRoleAllocation(id, participantSkills);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 评估会议质量
     */
    @PostMapping("/{id}/assess-meeting")
    @Operation(summary = "评估会议质量", description = "评估会议质量和效果")
    public ResponseEntity<CollaborationAIEntity> assessMeetingQuality(
            @PathVariable Long id,
            @RequestBody Map<String, Object> meetingMetrics) {
        try {
            CollaborationAIEntity updated = collaborationAIService.assessMeetingQuality(id, meetingMetrics);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 添加洞察
     */
    @PostMapping("/{id}/insights")
    @Operation(summary = "添加洞察", description = "添加协作洞察")
    public ResponseEntity<CollaborationAIEntity> addInsight(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String insight = request.get("insight");
            if (insight == null || insight.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            CollaborationAIEntity updated = collaborationAIService.addInsight(id, insight);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 添加推荐
     */
    @PostMapping("/{id}/recommendations")
    @Operation(summary = "添加推荐", description = "添加协作推荐")
    public ResponseEntity<CollaborationAIEntity> addRecommendation(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String recommendation = request.get("recommendation");
            if (recommendation == null || recommendation.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            CollaborationAIEntity updated = collaborationAIService.addRecommendation(id, recommendation);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 更新性能指标
     */
    @PostMapping("/{id}/metrics")
    @Operation(summary = "更新性能指标", description = "更新协作性能指标")
    public ResponseEntity<CollaborationAIEntity> updatePerformanceMetric(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String metricName = (String) request.get("metricName");
            Object metricValueObj = request.get("metricValue");
            if (metricName == null || metricValueObj == null) {
                return ResponseEntity.badRequest().body(null);
            }
            Double metricValue;
            if (metricValueObj instanceof Number) {
                metricValue = ((Number) metricValueObj).doubleValue();
            } else if (metricValueObj instanceof String) {
                try {
                    metricValue = Double.parseDouble((String) metricValueObj);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(null);
                }
            } else {
                return ResponseEntity.badRequest().body(null);
            }
            CollaborationAIEntity updated = collaborationAIService.updatePerformanceMetric(id, metricName, metricValue);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取需要进行分析的协作 AI 配置
     */
    @GetMapping("/for-analysis")
    @Operation(summary = "获取待分析配置", description = "获取需要进行分析的协作AI配置")
    public ResponseEntity<List<CollaborationAIEntity>> getCollaborationAIsForAnalysis() {
        try {
            List<CollaborationAIEntity> collaborationAIs = collaborationAIService.getCollaborationAIsForAnalysis();
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 批量启用协作 AI 配置
     */
    @PostMapping("/batch-enable")
    @Operation(summary = "批量启用配置", description = "批量启用多个协作AI配置")
    public ResponseEntity<Map<String, Object>> batchEnableCollaborationAIs(
            @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID列表不能为空"));
            }
            int updated = collaborationAIService.batchEnableCollaborationAIs(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("updatedCount", updated);
            response.put("message", "成功启用 " + updated + " 个配置");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "批量启用失败: " + e.getMessage()));
        }
    }

    /**
     * 批量禁用协作 AI 配置
     */
    @PostMapping("/batch-disable")
    @Operation(summary = "批量禁用配置", description = "批量禁用多个协作AI配置")
    public ResponseEntity<Map<String, Object>> batchDisableCollaborationAIs(
            @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID列表不能为空"));
            }
            int updated = collaborationAIService.batchDisableCollaborationAIs(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("updatedCount", updated);
            response.put("message", "成功禁用 " + updated + " 个配置");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "批量禁用失败: " + e.getMessage()));
        }
    }

    /**
     * 获取协作类型统计
     */
    @GetMapping("/statistics/types")
    @Operation(summary = "获取协作类型统计", description = "获取各种协作类型的配置数量统计")
    public ResponseEntity<Map<String, Long>> getCollaborationTypeStatistics() {
        try {
            Map<String, Long> statistics = collaborationAIService.getCollaborationTypeStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取分析频率统计
     */
    @GetMapping("/statistics/frequencies")
    @Operation(summary = "获取分析频率统计", description = "获取各种分析频率的配置数量统计")
    public ResponseEntity<Map<Integer, Long>> getAnalysisFrequencyStatistics() {
        try {
            Map<Integer, Long> statistics = collaborationAIService.getAnalysisFrequencyStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 搜索会议纪要
     */
    @GetMapping("/search/minutes")
    @Operation(summary = "搜索会议纪要", description = "搜索包含特定关键词的会议纪要")
    public ResponseEntity<List<CollaborationAIEntity>> searchMeetingMinutes(
            @RequestParam String keyword) {
        try {
            List<CollaborationAIEntity> results = collaborationAIService.searchMeetingMinutes(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 搜索项目进度
     */
    @GetMapping("/search/progress")
    @Operation(summary = "搜索项目进度", description = "搜索包含特定关键词的项目进度")
    public ResponseEntity<List<CollaborationAIEntity>> searchProjectProgress(
            @RequestParam String keyword) {
        try {
            List<CollaborationAIEntity> results = collaborationAIService.searchProjectProgress(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 搜索任务分配
     */
    @GetMapping("/search/tasks")
    @Operation(summary = "搜索任务分配", description = "搜索包含特定关键词的任务分配")
    public ResponseEntity<List<CollaborationAIEntity>> searchTaskAssignments(
            @RequestParam String keyword) {
        try {
            List<CollaborationAIEntity> results = collaborationAIService.searchTaskAssignments(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取最近更新的协作 AI 配置
     */
    @GetMapping("/recently-updated")
    @Operation(summary = "获取最近更新配置", description = "获取最近更新的协作AI配置")
    public ResponseEntity<List<CollaborationAIEntity>> getRecentlyUpdatedCollaborationAIs(
            @RequestParam(defaultValue = "24") int hours) {
        try {
            List<CollaborationAIEntity> collaborationAIs = collaborationAIService.getRecentlyUpdatedCollaborationAIs(hours);
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取需要生成报告的配置
     */
    @GetMapping("/for-report-generation")
    @Operation(summary = "获取待生成报告配置", description = "获取需要生成效率报告的配置")
    public ResponseEntity<List<CollaborationAIEntity>> getCollaborationAIsForReportGeneration(
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<CollaborationAIEntity> collaborationAIs = collaborationAIService.getCollaborationAIsForReportGeneration(days);
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取有洞察但无推荐的配置
     */
    @GetMapping("/insights-without-recommendations")
    @Operation(summary = "获取有洞察无推荐配置", description = "获取有洞察但无推荐的协作AI配置")
    public ResponseEntity<List<CollaborationAIEntity>> getCollaborationAIsWithInsightsButNoRecommendations() {
        try {
            List<CollaborationAIEntity> collaborationAIs = collaborationAIService.getCollaborationAIsWithInsightsButNoRecommendations();
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取有瓶颈但无建议的配置
     */
    @GetMapping("/bottlenecks-without-suggestions")
    @Operation(summary = "获取有瓶颈无建议配置", description = "获取有瓶颈但无建议的协作AI配置")
    public ResponseEntity<List<CollaborationAIEntity>> getCollaborationAIsWithBottlenecksButNoSuggestions() {
        try {
            List<CollaborationAIEntity> collaborationAIs = collaborationAIService.getCollaborationAIsWithBottlenecksButNoSuggestions();
            return ResponseEntity.ok(collaborationAIs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "获取系统健康状态", description = "获取协作AI助手的系统健康状态")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        try {
            long totalConfigs = collaborationAIService.getCollaborationAIs(Pageable.unpaged()).getTotalElements();
            long enabledConfigs = collaborationAIService.getAllEnabledCollaborationAIs().size();
            List<CollaborationAIEntity> forAnalysis = collaborationAIService.getCollaborationAIsForAnalysis();
            List<CollaborationAIEntity> forReport = collaborationAIService.getCollaborationAIsForReportGeneration(7);
            
            Map<String, Object> healthStatus = new HashMap<>();
            healthStatus.put("status", "healthy");
            healthStatus.put("timestamp", LocalDateTime.now());
            healthStatus.put("totalConfigurations", totalConfigs);
            healthStatus.put("enabledConfigurations", enabledConfigs);
            healthStatus.put("configurationsForAnalysis", forAnalysis.size());
            healthStatus.put("configurationsForReport", forReport.size());
            healthStatus.put("uptime", "100%");
            
            return ResponseEntity.ok(healthStatus);
        } catch (Exception e) {
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("status", "unhealthy");
            errorStatus.put("timestamp", LocalDateTime.now());
            errorStatus.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorStatus);
        }
    }

    /**
     * 导出协作 AI 配置
     */
    @GetMapping("/export")
    @Operation(summary = "导出配置", description = "导出协作AI配置数据")
    public ResponseEntity<Map<String, Object>> exportCollaborationAIs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            // 这里可以添加导出逻辑
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("message", "导出功能开发中");
            exportData.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(exportData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "导出失败: " + e.getMessage()));
        }
    }
}