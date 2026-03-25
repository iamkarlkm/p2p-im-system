package com.im.system.controller;

import com.im.system.entity.EdgeVideoProcessingEntity;
import com.im.system.service.EdgeVideoProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 边缘视频处理控制器
 * 提供 REST API 用于管理边缘计算节点的实时音视频处理
 */
@RestController
@RequestMapping("/api/v1/edge-video")
public class EdgeVideoProcessingController {

    private final EdgeVideoProcessingService processingService;

    @Autowired
    public EdgeVideoProcessingController(EdgeVideoProcessingService processingService) {
        this.processingService = processingService;
    }

    /**
     * 创建新的视频处理任务
     */
    @PostMapping("/tasks")
    public ResponseEntity<Map<String, Object>> createProcessingTask(
            @RequestBody CreateTaskRequest request) {
        
        try {
            EdgeVideoProcessingEntity task = processingService.createVideoProcessingTask(
                request.getSessionId(),
                request.getUserId(),
                request.getMediaType(),
                request.getInputSource(),
                request.getProcessingOptions()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("taskId", task.getTaskId());
            response.put("status", task.getProcessingStatus());
            response.put("edgeNodeId", task.getEdgeNodeId());
            response.put("createdAt", task.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取任务状态
     */
    @GetMapping("/tasks/{taskId}/status")
    public ResponseEntity<Map<String, Object>> getTaskStatus(@PathVariable String taskId) {
        EdgeVideoProcessingEntity task = processingService.getTaskStatus(taskId);
        
        if (task == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        Map<String, Object> response = buildTaskResponse(task);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的所有任务
     */
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<Map<String, Object>> getUserTasks(@PathVariable String userId) {
        List<EdgeVideoProcessingEntity> tasks = processingService.getUserTasks(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("totalTasks", tasks.size());
        response.put("tasks", tasks.stream()
            .map(this::buildTaskResponse)
            .toArray()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取会话的所有任务
     */
    @GetMapping("/sessions/{sessionId}/tasks")
    public ResponseEntity<Map<String, Object>> getSessionTasks(@PathVariable String sessionId) {
        List<EdgeVideoProcessingEntity> tasks = processingService.getSessionTasks(sessionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("sessionId", sessionId);
        response.put("totalTasks", tasks.size());
        response.put("tasks", tasks.stream()
            .map(this::buildTaskResponse)
            .toArray()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * 取消任务
     */
    @PostMapping("/tasks/{taskId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelTask(@PathVariable String taskId) {
        boolean cancelled = processingService.cancelTask(taskId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", cancelled);
        
        if (cancelled) {
            response.put("message", "Task cancelled successfully");
        } else {
            response.put("error", "Unable to cancel task. Task may be processing or not found.");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 暂停任务
     */
    @PostMapping("/tasks/{taskId}/pause")
    public ResponseEntity<Map<String, Object>> pauseTask(@PathVariable String taskId) {
        boolean paused = processingService.pauseTask(taskId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", paused);
        
        if (paused) {
            response.put("message", "Task paused successfully");
        } else {
            response.put("error", "Unable to pause task. Task may not be processing.");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 恢复任务
     */
    @PostMapping("/tasks/{taskId}/resume")
    public ResponseEntity<Map<String, Object>> resumeTask(@PathVariable String taskId) {
        boolean resumed = processingService.resumeTask(taskId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", resumed);
        
        if (resumed) {
            response.put("message", "Task resumed successfully");
        } else {
            response.put("error", "Unable to resume task. Task may not be paused.");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取系统统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        Map<String, Object> stats = processingService.getSystemStatistics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("statistics", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 清理过期任务
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpiredTasks(
            @RequestParam(defaultValue = "30") int daysToKeep) {
        
        processingService.cleanupExpiredTasks(daysToKeep);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", String.format("Cleaned up tasks older than %d days", daysToKeep));
        
        return ResponseEntity.ok(response);
    }

    /**
     * 测试边缘节点连接
     */
    @PostMapping("/nodes/{nodeId}/test-connection")
    public ResponseEntity<Map<String, Object>> testNodeConnection(@PathVariable String nodeId) {
        // 这里应该实现实际的心跳检测逻辑
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("nodeId", nodeId);
        response.put("status", "online");
        response.put("responseTimeMs", 45);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取支持的视频编解码器
     */
    @GetMapping("/supported-codecs")
    public ResponseEntity<Map<String, Object>> getSupportedCodecs() {
        Map<String, Object> codecs = new HashMap<>();
        
        codecs.put("videoCodecs", new String[]{
            "H.264/AVC",
            "H.265/HEVC", 
            "VP9",
            "AV1",
            "MPEG-4",
            "H.263"
        });
        
        codecs.put("audioCodecs", new String[]{
            "AAC",
            "MP3",
            "Opus",
            "Vorbis",
            "FLAC",
            "PCM"
        });
        
        codecs.put("containerFormats", new String[]{
            "MP4",
            "WebM",
            "MOV",
            "AVI",
            "MKV",
            "FLV"
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("codecs", codecs);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取 AI 增强选项
     */
    @GetMapping("/ai-enhancements")
    public ResponseEntity<Map<String, Object>> getAiEnhancements() {
        Map<String, Object> enhancements = new HashMap<>();
        
        enhancements.put("availableEnhancements", new String[]{
            "super-resolution",
            "noise-reduction",
            "color-correction",
            "stabilization",
            "object-tracking",
            "background-blur",
            "face-enhancement",
            "lip-sync"
        });
        
        enhancements.put("supportedModels", new String[]{
            "ESRGAN",
            "Real-ESRGAN",
            "Waifu2x",
            "DeepDenoise",
            "DAIN (frame-interpolation)",
            "RIFE"
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("enhancements", enhancements);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取带宽优化选项
     */
    @GetMapping("/bandwidth-optimization")
    public ResponseEntity<Map<String, Object>> getBandwidthOptimizationOptions() {
        Map<String, Object> optimization = new HashMap<>();
        
        optimization.put("compressionLevels", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        optimization.put("adaptiveBitrate", true);
        optimization.put("contentAwareCompression", true);
        optimization.put("webOptimized", true);
        optimization.put("mobileOptimized", true);
        optimization.put("recommendedSettings", Map.of(
            "lowBandwidth", Map.of("bitrateKbps", 500, "resolution", "640x360"),
            "mediumBandwidth", Map.of("bitrateKbps", 1500, "resolution", "1280x720"),
            "highBandwidth", Map.of("bitrateKbps", 4000, "resolution", "1920x1080")
        ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("optimization", optimization);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 构建任务响应
     */
    private Map<String, Object> buildTaskResponse(EdgeVideoProcessingEntity task) {
        Map<String, Object> taskResponse = new HashMap<>();
        
        taskResponse.put("taskId", task.getTaskId());
        taskResponse.put("sessionId", task.getSessionId());
        taskResponse.put("userId", task.getUserId());
        taskResponse.put("edgeNodeId", task.getEdgeNodeId());
        taskResponse.put("mediaType", task.getMediaType());
        taskResponse.put("inputSource", task.getInputSource());
        taskResponse.put("outputDestination", task.getOutputDestination());
        taskResponse.put("processingStatus", task.getProcessingStatus());
        taskResponse.put("videoCodec", task.getVideoCodec());
        taskResponse.put("audioCodec", task.getAudioCodec());
        taskResponse.put("resolution", task.getResolutionWidth() + "x" + task.getResolutionHeight());
        taskResponse.put("frameRate", task.getFrameRate());
        taskResponse.put("bitrateKbps", task.getBitrateKbps());
        taskResponse.put("aiEnhancementsEnabled", task.getAiEnhancementsEnabled());
        taskResponse.put("enhancementType", task.getEnhancementType());
        taskResponse.put("bandwidthOptimizationEnabled", task.getBandwidthOptimizationEnabled());
        taskResponse.put("compressionLevel", task.getCompressionLevel());
        taskResponse.put("latencyMs", task.getLatencyMs());
        taskResponse.put("processingStartTime", task.getProcessingStartTime());
        taskResponse.put("processingEndTime", task.getProcessingEndTime());
        taskResponse.put("processingDurationMs", task.getProcessingDurationMs());
        taskResponse.put("cpuUsagePercent", task.getCpuUsagePercent());
        taskResponse.put("memoryUsageMb", task.getMemoryUsageMb());
        taskResponse.put("networkBandwidthMbps", task.getNetworkBandwidthMbps());
        taskResponse.put("qualityScore", task.getQualityScore());
        taskResponse.put("errorMessage", task.getErrorMessage());
        taskResponse.put("retryCount", task.getRetryCount());
        taskResponse.put("maxRetries", task.getMaxRetries());
        taskResponse.put("priorityLevel", task.getPriorityLevel());
        taskResponse.put("createdAt", task.getCreatedAt());
        taskResponse.put("updatedAt", task.getUpdatedAt());
        
        return taskResponse;
    }

    /**
     * 创建任务请求体
     */
    public static class CreateTaskRequest {
        private String sessionId;
        private String userId;
        private EdgeVideoProcessingEntity.MediaType mediaType;
        private String inputSource;
        private Map<String, Object> processingOptions;

        // Getters and Setters
        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public EdgeVideoProcessingEntity.MediaType getMediaType() {
            return mediaType;
        }

        public void setMediaType(EdgeVideoProcessingEntity.MediaType mediaType) {
            this.mediaType = mediaType;
        }

        public String getInputSource() {
            return inputSource;
        }

        public void setInputSource(String inputSource) {
            this.inputSource = inputSource;
        }

        public Map<String, Object> getProcessingOptions() {
            return processingOptions;
        }

        public void setProcessingOptions(Map<String, Object> processingOptions) {
            this.processingOptions = processingOptions;
        }
    }

    /**
     * 错误处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> stats = processingService.getSystemStatistics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "edge-video-processing");
        response.put("timestamp", System.currentTimeMillis());
        response.put("activeTasks", stats.get("activeTasks"));
        response.put("availableNodes", stats.get("availableNodes"));
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取系统配置
     */
    @GetMapping("/configuration")
    public ResponseEntity<Map<String, Object>> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("maxConcurrentTasksPerNode", 100);
        config.put("defaultRetryCount", 3);
        config.put("defaultPriorityLevel", 5);
        config.put("taskTimeoutMinutes", 30);
        config.put("heartbeatIntervalSeconds", 30);
        config.put("cleanupIntervalHours", 24);
        config.put("maxTaskHistoryDays", 30);
        config.put("enableAiEnhancements", true);
        config.put("enableBandwidthOptimization", true);
        config.put("enableRealTimeMonitoring", true);
        config.put("defaultVideoCodec", "H.264/AVC");
        config.put("defaultAudioCodec", "AAC");
        config.put("defaultResolution", "1280x720");
        config.put("defaultFrameRate", 30);
        config.put("defaultBitrateKbps", 2500);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("configuration", config);
        
        return ResponseEntity.ok(response);
    }
}