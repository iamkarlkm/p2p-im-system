package com.im.system.service;

import com.im.system.entity.EdgeVideoProcessingEntity;
import com.im.system.entity.EdgeNodeEntity;
import com.im.system.repository.EdgeVideoProcessingRepository;
import com.im.system.repository.EdgeNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 边缘视频处理服务
 * 提供边缘计算节点的实时音视频处理能力
 */
@Service
public class EdgeVideoProcessingService {

    private final EdgeVideoProcessingRepository processingRepository;
    private final EdgeNodeRepository nodeRepository;
    
    // 任务调度器缓存
    private final Map<String, EdgeVideoProcessingEntity> activeTasks = new ConcurrentHashMap<>();
    private final Map<String, EdgeNodeEntity> availableNodes = new ConcurrentHashMap<>();
    
    @Autowired
    public EdgeVideoProcessingService(EdgeVideoProcessingRepository processingRepository,
                                     EdgeNodeRepository nodeRepository) {
        this.processingRepository = processingRepository;
        this.nodeRepository = nodeRepository;
        initializeService();
    }
    
    private void initializeService() {
        // 加载所有可用的边缘节点
        List<EdgeNodeEntity> nodes = nodeRepository.findByConnectionStatus(EdgeNodeEntity.ConnectionStatus.ONLINE);
        nodes.forEach(node -> availableNodes.put(node.getNodeId(), node));
        
        // 加载活跃的处理任务
        List<EdgeVideoProcessingEntity> activeTasksList = processingRepository.findByProcessingStatusIn(
            Arrays.asList(
                EdgeVideoProcessingEntity.ProcessingStatus.PENDING,
                EdgeVideoProcessingEntity.ProcessingStatus.QUEUED,
                EdgeVideoProcessingEntity.ProcessingStatus.PROCESSING
            )
        );
        activeTasksList.forEach(task -> activeTasks.put(task.getTaskId(), task));
    }
    
    /**
     * 创建新的视频处理任务
     */
    @Transactional
    public EdgeVideoProcessingEntity createVideoProcessingTask(String sessionId, String userId,
                                                              EdgeVideoProcessingEntity.MediaType mediaType,
                                                              String inputSource,
                                                              Map<String, Object> processingOptions) {
        
        String taskId = "TASK-" + UUID.randomUUID().toString().substring(0, 8);
        
        EdgeVideoProcessingEntity task = new EdgeVideoProcessingEntity();
        task.setTaskId(taskId);
        task.setSessionId(sessionId);
        task.setUserId(userId);
        task.setMediaType(mediaType);
        task.setInputSource(inputSource);
        task.setProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        // 设置处理选项
        applyProcessingOptions(task, processingOptions);
        
        // 选择最优边缘节点
        EdgeNodeEntity selectedNode = selectOptimalEdgeNode(task);
        if (selectedNode != null) {
            task.setEdgeNodeId(selectedNode.getNodeId());
            selectedNode.incrementSessionCount();
            nodeRepository.save(selectedNode);
        }
        
        EdgeVideoProcessingEntity savedTask = processingRepository.save(task);
        activeTasks.put(taskId, savedTask);
        
        // 异步调度任务处理
        scheduleTaskProcessing(savedTask);
        
        return savedTask;
    }
    
    /**
     * 选择最优边缘节点
     */
    private EdgeNodeEntity selectOptimalEdgeNode(EdgeVideoProcessingEntity task) {
        List<EdgeNodeEntity> availableNodesList = availableNodes.values().stream()
            .filter(EdgeNodeEntity::isAvailableForProcessing)
            .collect(Collectors.toList());
        
        if (availableNodesList.isEmpty()) {
            return null;
        }
        
        // 根据多种因素选择最优节点
        return availableNodesList.stream()
            .max(Comparator.comparingDouble(this::calculateNodeScore))
            .orElse(availableNodesList.get(0));
    }
    
    /**
     * 计算节点得分
     */
    private double calculateNodeScore(EdgeNodeEntity node) {
        double score = 0.0;
        
        // 基于可用容量（越高越好）
        score += node.getAvailableCapacityPercentage() * 0.4;
        
        // 基于 CPU 使用率（越低越好）
        if (node.getCpuUsagePercent() != null) {
            score += (100 - node.getCpuUsagePercent()) * 0.3;
        }
        
        // 基于网络延迟（越低越好）
        if (node.getNetworkLatencyMs() != null) {
            score += Math.max(0, 100 - node.getNetworkLatencyMs()) * 0.2;
        }
        
        // 基于健康状态
        switch (node.getHealthStatus()) {
            case HEALTHY:
                score += 20;
                break;
            case WARNING:
                score += 10;
                break;
            default:
                score -= 10;
        }
        
        return score;
    }
    
    /**
     * 应用处理选项
     */
    private void applyProcessingOptions(EdgeVideoProcessingEntity task, Map<String, Object> options) {
        if (options == null) return;
        
        if (options.containsKey("videoCodec")) {
            task.setVideoCodec((String) options.get("videoCodec"));
        }
        if (options.containsKey("audioCodec")) {
            task.setAudioCodec((String) options.get("audioCodec"));
        }
        if (options.containsKey("resolutionWidth")) {
            task.setResolutionWidth((Integer) options.get("resolutionWidth"));
        }
        if (options.containsKey("resolutionHeight")) {
            task.setResolutionHeight((Integer) options.get("resolutionHeight"));
        }
        if (options.containsKey("frameRate")) {
            task.setFrameRate((Integer) options.get("frameRate"));
        }
        if (options.containsKey("bitrateKbps")) {
            task.setBitrateKbps((Integer) options.get("bitrateKbps"));
        }
        if (options.containsKey("aiEnhancementsEnabled")) {
            task.setAiEnhancementsEnabled((Boolean) options.get("aiEnhancementsEnabled"));
            if (task.getAiEnhancementsEnabled()) {
                task.setEnhancementType((String) options.getOrDefault("enhancementType", "general"));
            }
        }
        if (options.containsKey("bandwidthOptimizationEnabled")) {
            task.setBandwidthOptimizationEnabled((Boolean) options.get("bandwidthOptimizationEnabled"));
            if (task.getBandwidthOptimizationEnabled()) {
                task.setCompressionLevel((Integer) options.getOrDefault("compressionLevel", 5));
            }
        }
        if (options.containsKey("priorityLevel")) {
            task.setPriorityLevel((Integer) options.get("priorityLevel"));
        }
        if (options.containsKey("maxRetries")) {
            task.setMaxRetries((Integer) options.get("maxRetries"));
        }
    }
    
    /**
     * 调度任务处理
     */
    private void scheduleTaskProcessing(EdgeVideoProcessingEntity task) {
        // 这里实际应该使用消息队列或异步任务调度器
        // 简化实现：直接标记为排队状态
        task.setProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.QUEUED);
        processingRepository.save(task);
        
        // 模拟异步处理
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 模拟排队延迟
                processVideoTask(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * 处理视频任务
     */
    private void processVideoTask(EdgeVideoProcessingEntity task) {
        try {
            task.startProcessing();
            processingRepository.save(task);
            
            // 模拟视频处理逻辑
            simulateVideoProcessing(task);
            
            // 生成输出路径
            String outputDestination = generateOutputDestination(task);
            double qualityScore = calculateQualityScore(task);
            
            task.completeProcessing(outputDestination, qualityScore);
            processingRepository.save(task);
            
            // 更新节点会话计数
            EdgeNodeEntity node = availableNodes.get(task.getEdgeNodeId());
            if (node != null) {
                node.decrementSessionCount();
                nodeRepository.save(node);
            }
            
            // 从活跃任务中移除
            activeTasks.remove(task.getTaskId());
            
        } catch (Exception e) {
            handleProcessingFailure(task, e);
        }
    }
    
    /**
     * 模拟视频处理
     */
    private void simulateVideoProcessing(EdgeVideoProcessingEntity task) throws InterruptedException {
        // 基于任务复杂度模拟处理时间
        int processingTimeMs = calculateProcessingTime(task);
        Thread.sleep(Math.min(processingTimeMs, 5000)); // 最大 5 秒模拟
        
        // 更新处理指标
        task.setLatencyMs(processingTimeMs);
        task.setCpuUsagePercent(25.0 + Math.random() * 40); // 25-65% CPU 使用率
        task.setMemoryUsageMb(512 + (int)(Math.random() * 1024)); // 512-1536 MB 内存
        task.setNetworkBandwidthMbps(10.0 + Math.random() * 90); // 10-100 Mbps
        
        processingRepository.save(task);
    }
    
    /**
     * 计算处理时间
     */
    private int calculateProcessingTime(EdgeVideoProcessingEntity task) {
        int baseTime = 1000; // 1 秒基础时间
        
        // 基于分辨率调整
        if (task.getResolutionWidth() != null && task.getResolutionHeight() != null) {
            int pixels = task.getResolutionWidth() * task.getResolutionHeight();
            baseTime += pixels / 100000; // 每 10 万像素增加 1 毫秒
        }
        
        // 基于帧率调整
        if (task.getFrameRate() != null) {
            baseTime += task.getFrameRate() * 10;
        }
        
        // AI 增强会增加时间
        if (Boolean.TRUE.equals(task.getAiEnhancementsEnabled())) {
            baseTime *= 2;
        }
        
        return Math.min(baseTime, 10000); // 最大 10 秒
    }
    
    /**
     * 生成输出路径
     */
    private String generateOutputDestination(EdgeVideoProcessingEntity task) {
        return String.format("edge-processed/%s/%s/output.%s",
            task.getUserId(),
            task.getTaskId(),
            getOutputFormat(task)
        );
    }
    
    /**
     * 获取输出格式
     */
    private String getOutputFormat(EdgeVideoProcessingEntity task) {
        if (task.getVideoCodec() != null) {
            switch (task.getVideoCodec().toLowerCase()) {
                case "h264":
                    return "mp4";
                case "h265":
                case "hevc":
                    return "mp4";
                case "vp9":
                    return "webm";
                case "av1":
                    return "mp4";
                default:
                    return "mp4";
            }
        }
        return "mp4";
    }
    
    /**
     * 计算质量得分
     */
    private double calculateQualityScore(EdgeVideoProcessingEntity task) {
        double score = 80.0; // 基础得分 80
        
        // 基于分辨率加分
        if (task.getResolutionWidth() != null && task.getResolutionHeight() != null) {
            int pixels = task.getResolutionWidth() * task.getResolutionHeight();
            if (pixels >= 3840 * 2160) score += 10; // 4K
            else if (pixels >= 1920 * 1080) score += 5; // 1080p
        }
        
        // 基于码率加分
        if (task.getBitrateKbps() != null) {
            if (task.getBitrateKbps() >= 8000) score += 5;
            else if (task.getBitrateKbps() >= 4000) score += 3;
        }
        
        // AI 增强加分
        if (Boolean.TRUE.equals(task.getAiEnhancementsEnabled())) {
            score += 8;
        }
        
        // 带宽优化可能减分（质量 vs 大小权衡）
        if (Boolean.TRUE.equals(task.getBandwidthOptimizationEnabled())) {
            score -= 3;
        }
        
        // 随机波动 ±2
        score += (Math.random() * 4) - 2;
        
        return Math.min(Math.max(score, 0), 100);
    }
    
    /**
     * 处理失败处理
     */
    private void handleProcessingFailure(EdgeVideoProcessingEntity task, Exception e) {
        task.failProcessing(e.getMessage());
        
        if (task.canRetry()) {
            task.incrementRetryCount();
            task.setProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.PENDING);
            
            // 重新调度
            scheduleTaskProcessing(task);
        } else {
            processingRepository.save(task);
            activeTasks.remove(task.getTaskId());
            
            // 更新节点会话计数
            EdgeNodeEntity node = availableNodes.get(task.getEdgeNodeId());
            if (node != null) {
                node.decrementSessionCount();
                nodeRepository.save(node);
            }
        }
    }
    
    /**
     * 获取任务状态
     */
    public EdgeVideoProcessingEntity getTaskStatus(String taskId) {
        return activeTasks.getOrDefault(taskId, 
            processingRepository.findByTaskId(taskId).orElse(null));
    }
    
    /**
     * 获取用户的所有任务
     */
    public List<EdgeVideoProcessingEntity> getUserTasks(String userId) {
        return processingRepository.findByUserId(userId);
    }
    
    /**
     * 获取会话的所有任务
     */
    public List<EdgeVideoProcessingEntity> getSessionTasks(String sessionId) {
        return processingRepository.findBySessionId(sessionId);
    }
    
    /**
     * 取消任务
     */
    @Transactional
    public boolean cancelTask(String taskId) {
        EdgeVideoProcessingEntity task = activeTasks.get(taskId);
        if (task == null) {
            task = processingRepository.findByTaskId(taskId).orElse(null);
        }
        
        if (task != null && task.getProcessingStatus() == EdgeVideoProcessingEntity.ProcessingStatus.PROCESSING) {
            // 无法取消正在处理的任务
            return false;
        }
        
        if (task != null) {
            task.setProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.CANCELLED);
            processingRepository.save(task);
            activeTasks.remove(taskId);
            
            // 更新节点会话计数
            EdgeNodeEntity node = availableNodes.get(task.getEdgeNodeId());
            if (node != null) {
                node.decrementSessionCount();
                nodeRepository.save(node);
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 暂停任务
     */
    @Transactional
    public boolean pauseTask(String taskId) {
        EdgeVideoProcessingEntity task = activeTasks.get(taskId);
        if (task != null && task.getProcessingStatus() == EdgeVideoProcessingEntity.ProcessingStatus.PROCESSING) {
            task.setProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.PAUSED);
            processingRepository.save(task);
            return true;
        }
        return false;
    }
    
    /**
     * 恢复任务
     */
    @Transactional
    public boolean resumeTask(String taskId) {
        EdgeVideoProcessingEntity task = activeTasks.get(taskId);
        if (task != null && task.getProcessingStatus() == EdgeVideoProcessingEntity.ProcessingStatus.PAUSED) {
            task.setProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.PROCESSING);
            processingRepository.save(task);
            return true;
        }
        return false;
    }
    
    /**
     * 获取系统统计信息
     */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalTasks = processingRepository.count();
        long activeTaskCount = activeTasks.size();
        long completedTasks = processingRepository.countByProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.COMPLETED);
        long failedTasks = processingRepository.countByProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.FAILED);
        
        stats.put("totalTasks", totalTasks);
        stats.put("activeTasks", activeTaskCount);
        stats.put("completedTasks", completedTasks);
        stats.put("failedTasks", failedTasks);
        stats.put("availableNodes", availableNodes.size());
        stats.put("totalNodeCapacity", availableNodes.values().stream()
            .mapToInt(EdgeNodeEntity::getMaxConcurrentSessions).sum());
        stats.put("usedNodeCapacity", availableNodes.values().stream()
            .mapToInt(EdgeNodeEntity::getCurrentSessions).sum());
        
        // 计算平均处理时间
        List<EdgeVideoProcessingEntity> completed = processingRepository
            .findByProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.COMPLETED);
        double avgProcessingTime = completed.stream()
            .filter(t -> t.getProcessingDurationMs() != null)
            .mapToLong(EdgeVideoProcessingEntity::getProcessingDurationMs)
            .average()
            .orElse(0.0);
        
        stats.put("averageProcessingTimeMs", avgProcessingTime);
        
        // 计算平均质量得分
        double avgQualityScore = completed.stream()
            .filter(t -> t.getQualityScore() != null)
            .mapToDouble(EdgeVideoProcessingEntity::getQualityScore)
            .average()
            .orElse(0.0);
        
        stats.put("averageQualityScore", avgQualityScore);
        
        return stats;
    }
    
    /**
     * 更新边缘节点信息
     */
    public void updateEdgeNode(EdgeNodeEntity node) {
        availableNodes.put(node.getNodeId(), node);
        nodeRepository.save(node);
    }
    
    /**
     * 移除边缘节点
     */
    public void removeEdgeNode(String nodeId) {
        availableNodes.remove(nodeId);
        
        // 将该节点的任务重新分配
        List<EdgeVideoProcessingEntity> nodeTasks = processingRepository
            .findByEdgeNodeIdAndProcessingStatusIn(nodeId, 
                Arrays.asList(
                    EdgeVideoProcessingEntity.ProcessingStatus.PENDING,
                    EdgeVideoProcessingEntity.ProcessingStatus.QUEUED,
                    EdgeVideoProcessingEntity.ProcessingStatus.PROCESSING
                )
            );
        
        nodeTasks.forEach(task -> {
            task.setEdgeNodeId(null);
            task.setProcessingStatus(EdgeVideoProcessingEntity.ProcessingStatus.PENDING);
            processingRepository.save(task);
            
            // 重新调度
            scheduleTaskProcessing(task);
        });
    }
    
    /**
     * 清理过期任务
     */
    @Transactional
    public void cleanupExpiredTasks(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        
        List<EdgeVideoProcessingEntity> expiredTasks = processingRepository
            .findByCreatedAtBeforeAndProcessingStatusIn(cutoffDate,
                Arrays.asList(
                    EdgeVideoProcessingEntity.ProcessingStatus.COMPLETED,
                    EdgeVideoProcessingEntity.ProcessingStatus.FAILED,
                    EdgeVideoProcessingEntity.ProcessingStatus.CANCELLED
                )
            );
        
        processingRepository.deleteAll(expiredTasks);
        
        // 从活跃任务中移除
        expiredTasks.forEach(task -> activeTasks.remove(task.getTaskId()));
    }
}