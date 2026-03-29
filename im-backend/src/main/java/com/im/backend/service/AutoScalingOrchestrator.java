package com.im.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WebSocket集群自动扩缩容编排服务
 * 负责决策、执行和管理集群扩容/缩容操作
 */
@Service
public class AutoScalingOrchestrator {
    
    @Autowired
    private LoadPredictionService predictionService;
    
    @Autowired
    private NodeManagementService nodeManagement;
    
    @Autowired
    private ConnectionMigrationService migrationService;
    
    @Autowired
    private ClusterMetricsCollector metricsCollector;
    
    // 扩缩容操作线程池
    private final ExecutorService scalingExecutor = Executors.newFixedThreadPool(3);
    
    // 调度线程池
    private ScheduledExecutorService scheduler;
    
    // 当前扩缩容操作状态
    private final AtomicBoolean scalingInProgress = new AtomicBoolean(false);
    private final AtomicLong lastScaleUpTime = new AtomicLong(0);
    private final AtomicLong lastScaleDownTime = new AtomicLong(0);
    private final AtomicInteger currentNodeCount = new AtomicInteger(0);
    
    // 扩缩容配置
    private static final long SCALE_UP_COOLDOWN_MS = 3 * 60 * 1000; // 3分钟冷却
    private static final long SCALE_DOWN_COOLDOWN_MS = 10 * 60 * 1000; // 10分钟冷却
    private static final int MIN_NODE_COUNT = 2;
    private static final int MAX_NODE_COUNT = 50;
    private static final int SCALE_UP_BATCH_SIZE = 2;
    private static final int SCALE_DOWN_BATCH_SIZE = 1;
    private static final long MIGRATION_TIMEOUT_MS = 5 * 60 * 1000; // 5分钟
    
    // 扩缩容历史记录
    private final List<ScalingOperation> scalingHistory = new CopyOnWriteArrayList<>();
    private static final int MAX_HISTORY_SIZE = 100;
    
    /**
     * 扩缩容操作记录
     */
    public static class ScalingOperation {
        public enum Type {
            SCALE_UP, SCALE_DOWN, SCALE_UP_FAILED, SCALE_DOWN_FAILED
        }
        
        public enum Status {
            PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
        }
        
        public String operationId;
        public Type type;
        public Status status;
        public long startTime;
        public long endTime;
        public int nodeCountBefore;
        public int nodeCountAfter;
        public List<String> affectedNodes;
        public String reason;
        public String errorMessage;
        public Map<String, Object> metadata;
        
        public ScalingOperation() {
            this.operationId = UUID.randomUUID().toString();
            this.startTime = System.currentTimeMillis();
            this.affectedNodes = new ArrayList<>();
            this.metadata = new HashMap<>();
        }
        
        public long getDurationMs() {
            if (endTime > 0) {
                return endTime - startTime;
            }
            return System.currentTimeMillis() - startTime;
        }
    }
    
    /**
     * 扩缩容决策配置
     */
    public static class ScalingPolicy {
        public boolean autoScalingEnabled = true;
        public int minNodes = MIN_NODE_COUNT;
        public int maxNodes = MAX_NODE_COUNT;
        public int scaleUpThresholdPercent = 75;
        public int scaleDownThresholdPercent = 30;
        public int targetUtilizationPercent = 60;
        public long scaleUpCooldownMs = SCALE_UP_COOLDOWN_MS;
        public long scaleDownCooldownMs = SCALE_DOWN_COOLDOWN_MS;
        public boolean predictiveScaling = true;
        public boolean connectionDraining = true;
        public long drainingTimeoutMs = MIGRATION_TIMEOUT_MS;
    }
    
    private final ScalingPolicy scalingPolicy = new ScalingPolicy();
    
    @PostConstruct
    public void init() {
        scheduler = Executors.newScheduledThreadPool(2);
        
        // 启动预测性扩缩容检查（每30秒）
        scheduler.scheduleWithFixedDelay(
            this::predictiveScalingCheck,
            30, 30, TimeUnit.SECONDS
        );
        
        // 启动响应式扩缩容检查（每10秒）
        scheduler.scheduleWithFixedDelay(
            this::reactiveScalingCheck,
            10, 10, TimeUnit.SECONDS
        );
        
        // 启动指标收集
        scheduler.scheduleWithFixedDelay(
            this::collectAndRecordMetrics,
            5, 10, TimeUnit.SECONDS
        );
        
        currentNodeCount.set(nodeManagement.getActiveNodeCount());
    }
    
    @PreDestroy
    public void destroy() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        scalingExecutor.shutdown();
    }
    
    /**
     * 预测性扩缩容检查
     */
    private void predictiveScalingCheck() {
        if (!scalingPolicy.autoScalingEnabled || scalingInProgress.get()) {
            return;
        }
        
        try {
            // 获取未来10分钟的预测
            LoadPredictionService.ClusterPrediction prediction = 
                predictionService.predictClusterLoad(10);
            
            // 基于预测做出决策
            switch (prediction.recommendation) {
                case SCALE_UP_IMMEDIATE:
                    if (canScaleUp()) {
                        triggerScaleUp("Predictive: " + String.join(", ", prediction.alerts));
                    }
                    break;
                case SCALE_UP_RECOMMENDED:
                    // 检查5分钟预测
                    LoadPredictionService.ClusterPrediction shortPrediction = 
                        predictionService.predictClusterLoad(5);
                    if (shortPrediction.overallRiskScore > 0.6 && canScaleUp()) {
                        triggerScaleUp("Predictive early warning: risk score = " + 
                            String.format("%.2f", shortPrediction.overallRiskScore));
                    }
                    break;
                case SCALE_DOWN_RECOMMENDED:
                    if (canScaleDown()) {
                        triggerScaleDown("Predictive: sustained low load detected");
                    }
                    break;
                default:
                    // 保持现状
                    break;
            }
        } catch (Exception e) {
            recordError("Predictive scaling check failed", e);
        }
    }
    
    /**
     * 响应式扩缩容检查
     */
    private void reactiveScalingCheck() {
        if (!scalingPolicy.autoScalingEnabled || scalingInProgress.get()) {
            return;
        }
        
        try {
            ClusterMetricsCollector.ClusterMetrics currentMetrics = 
                metricsCollector.collectClusterMetrics();
            
            double avgCpuUsage = currentMetrics.getAverageCpuUsage();
            double avgMemoryUsage = currentMetrics.getAverageMemoryUsage();
            double avgConnectionRatio = currentMetrics.getAverageConnectionRatio();
            
            // 扩容检查
            boolean needScaleUp = false;
            StringBuilder reason = new StringBuilder();
            
            if (avgConnectionRatio > scalingPolicy.scaleUpThresholdPercent / 100.0) {
                needScaleUp = true;
                reason.append("Connection ratio high (").append(
                    String.format("%.1f%%", avgConnectionRatio * 100)).append("); ");
            }
            if (avgCpuUsage > CPU_THRESHOLD_CRITICAL) {
                needScaleUp = true;
                reason.append("CPU critical (").append(
                    String.format("%.1f%%", avgCpuUsage)).append("); ");
            }
            if (avgMemoryUsage > MEMORY_THRESHOLD_CRITICAL) {
                needScaleUp = true;
                reason.append("Memory critical (").append(
                    String.format("%.1f%%", avgMemoryUsage)).append("); ");
            }
            
            if (needScaleUp && canScaleUp()) {
                triggerScaleUp("Reactive: " + reason.toString());
                return;
            }
            
            // 缩容检查
            if (canScaleDown() && 
                avgConnectionRatio < scalingPolicy.scaleDownThresholdPercent / 100.0 &&
                avgCpuUsage < 40 && avgMemoryUsage < 50) {
                triggerScaleDown("Reactive: low utilization detected (CPU:" + 
                    String.format("%.1f%%, Mem:%.1f%%, Conn:%.1f%%)", 
                    avgCpuUsage, avgMemoryUsage, avgConnectionRatio * 100));
            }
            
        } catch (Exception e) {
            recordError("Reactive scaling check failed", e);
        }
    }
    
    private static final double CPU_THRESHOLD_CRITICAL = 90.0;
    private static final double MEMORY_THRESHOLD_CRITICAL = 95.0;
    
    /**
     * 触发扩容操作
     */
    public ScalingOperation triggerScaleUp(String reason) {
        if (scalingInProgress.compareAndSet(false, true)) {
            try {
                ScalingOperation operation = new ScalingOperation();
                operation.type = ScalingOperation.Type.SCALE_UP;
                operation.reason = reason;
                operation.nodeCountBefore = currentNodeCount.get();
                
                scalingHistory.add(operation);
                trimHistory();
                
                // 异步执行扩容
                scalingExecutor.submit(() -> executeScaleUp(operation));
                
                return operation;
            } catch (Exception e) {
                scalingInProgress.set(false);
                throw e;
            }
        }
        return null;
    }
    
    /**
     * 执行扩容
     */
    private void executeScaleUp(ScalingOperation operation) {
        operation.status = ScalingOperation.Status.IN_PROGRESS;
        
        try {
            int nodesToAdd = Math.min(SCALE_UP_BATCH_SIZE, 
                scalingPolicy.maxNodes - currentNodeCount.get());
            
            if (nodesToAdd <= 0) {
                operation.status = ScalingOperation.Status.CANCELLED;
                operation.errorMessage = "Max node count reached";
                scalingInProgress.set(false);
                return;
            }
            
            // 启动新节点
            List<String> newNodeIds = new ArrayList<>();
            for (int i = 0; i < nodesToAdd; i++) {
                String nodeId = nodeManagement.provisionNode();
                newNodeIds.add(nodeId);
                operation.affectedNodes.add(nodeId);
                
                // 等待节点就绪
                boolean ready = waitForNodeReady(nodeId, 120000); // 2分钟超时
                if (!ready) {
                    operation.errorMessage = "Node " + nodeId + " failed to become ready";
                    operation.status = ScalingOperation.Status.FAILED;
                    operation.type = ScalingOperation.Type.SCALE_UP_FAILED;
                    operation.endTime = System.currentTimeMillis();
                    lastScaleUpTime.set(System.currentTimeMillis());
                    scalingInProgress.set(false);
                    return;
                }
            }
            
            // 更新节点数
            currentNodeCount.addAndGet(nodesToAdd);
            operation.nodeCountAfter = currentNodeCount.get();
            
            // 均衡负载
            rebalanceConnections();
            
            operation.status = ScalingOperation.Status.COMPLETED;
            operation.endTime = System.currentTimeMillis();
            lastScaleUpTime.set(operation.endTime);
            
            operation.metadata.put("nodesAdded", nodesToAdd);
            operation.metadata.put("avgProvisionTimeMs", operation.getDurationMs() / nodesToAdd);
            
        } catch (Exception e) {
            operation.status = ScalingOperation.Status.FAILED;
            operation.type = ScalingOperation.Type.SCALE_UP_FAILED;
            operation.errorMessage = e.getMessage();
            operation.endTime = System.currentTimeMillis();
            recordError("Scale up failed", e);
        } finally {
            scalingInProgress.set(false);
        }
    }
    
    /**
     * 触发缩容操作
     */
    public ScalingOperation triggerScaleDown(String reason) {
        if (scalingInProgress.compareAndSet(false, true)) {
            try {
                ScalingOperation operation = new ScalingOperation();
                operation.type = ScalingOperation.Type.SCALE_DOWN;
                operation.reason = reason;
                operation.nodeCountBefore = currentNodeCount.get();
                
                scalingHistory.add(operation);
                trimHistory();
                
                // 异步执行缩容
                scalingExecutor.submit(() -> executeScaleDown(operation));
                
                return operation;
            } catch (Exception e) {
                scalingInProgress.set(false);
                throw e;
            }
        }
        return null;
    }
    
    /**
     * 执行缩容
     */
    private void executeScaleDown(ScalingOperation operation) {
        operation.status = ScalingOperation.Status.IN_PROGRESS;
        
        try {
            int nodesToRemove = Math.min(SCALE_DOWN_BATCH_SIZE, 
                currentNodeCount.get() - scalingPolicy.minNodes);
            
            if (nodesToRemove <= 0) {
                operation.status = ScalingOperation.Status.CANCELLED;
                operation.errorMessage = "Min node count reached";
                scalingInProgress.set(false);
                return;
            }
            
            // 选择要移除的节点（负载最低的）
            List<String> candidates = selectNodesForRemoval(nodesToRemove);
            
            if (candidates.isEmpty()) {
                operation.status = ScalingOperation.Status.CANCELLED;
                operation.errorMessage = "No suitable nodes for removal";
                scalingInProgress.set(false);
                return;
            }
            
            // 逐个移除节点
            for (String nodeId : candidates) {
                operation.affectedNodes.add(nodeId);
                
                // 1. 标记节点为 draining（不再接受新连接）
                nodeManagement.markNodeDraining(nodeId);
                
                // 2. 迁移连接
                if (scalingPolicy.connectionDraining) {
                    boolean migrated = migrationService.migrateConnectionsFromNode(
                        nodeId, scalingPolicy.drainingTimeoutMs);
                    
                    if (!migrated) {
                        operation.errorMessage = "Connection migration failed for node " + nodeId;
                        operation.status = ScalingOperation.Status.FAILED;
                        operation.type = ScalingOperation.Type.SCALE_DOWN_FAILED;
                        operation.endTime = System.currentTimeMillis();
                        lastScaleDownTime.set(System.currentTimeMillis());
                        scalingInProgress.set(false);
                        return;
                    }
                }
                
                // 3. 停止节点
                nodeManagement.terminateNode(nodeId);
            }
            
            // 更新节点数
            currentNodeCount.addAndGet(-candidates.size());
            operation.nodeCountAfter = currentNodeCount.get();
            
            operation.status = ScalingOperation.Status.COMPLETED;
            operation.endTime = System.currentTimeMillis();
            lastScaleDownTime.set(operation.endTime);
            
            operation.metadata.put("nodesRemoved", candidates.size());
            operation.metadata.put("totalConnectionsMigrated", 
                migrationService.getRecentMigrationCount());
            
        } catch (Exception e) {
            operation.status = ScalingOperation.Status.FAILED;
            operation.type = ScalingOperation.Type.SCALE_DOWN_FAILED;
            operation.errorMessage = e.getMessage();
            operation.endTime = System.currentTimeMillis();
            recordError("Scale down failed", e);
        } finally {
            scalingInProgress.set(false);
        }
    }
    
    /**
     * 选择要移除的节点
     */
    private List<String> selectNodesForRemoval(int count) {
        List<NodeManagementService.NodeInfo> nodes = nodeManagement.getActiveNodes();
        
        // 过滤掉不能移除的节点
        List<NodeManagementService.NodeInfo> candidates = nodes.stream()
            .filter(n -> !n.isProtected())
            .filter(n -> n.getActiveConnections() < 100) // 优先移除连接少的
            .sorted(Comparator.comparingInt(NodeManagementService.NodeInfo::getActiveConnections))
            .collect(java.util.stream.Collectors.toList());
        
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(count, candidates.size()); i++) {
            result.add(candidates.get(i).getNodeId());
        }
        
        return result;
    }
    
    /**
     * 等待节点就绪
     */
    private boolean waitForNodeReady(String nodeId, long timeoutMs) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            NodeManagementService.NodeInfo node = nodeManagement.getNodeInfo(nodeId);
            if (node != null && node.isReady()) {
                return true;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
    
    /**
     * 重新均衡连接
     */
    private void rebalanceConnections() {
        try {
            migrationService.rebalanceConnections();
        } catch (Exception e) {
            recordError("Connection rebalancing failed", e);
        }
    }
    
    /**
     * 收集并记录指标
     */
    private void collectAndRecordMetrics() {
        try {
            List<NodeManagementService.NodeInfo> nodes = nodeManagement.getActiveNodes();
            
            for (NodeManagementService.NodeInfo node : nodes) {
                LoadPredictionService.LoadDataPoint dataPoint = 
                    new LoadPredictionService.LoadDataPoint(
                        node.getCpuUsage(),
                        node.getMemoryUsage(),
                        node.getActiveConnections(),
                        node.getMaxConnections(),
                        node.getNetworkInRate(),
                        node.getNetworkOutRate(),
                        node.getMessageRate()
                    );
                
                predictionService.recordLoadData(node.getNodeId(), dataPoint);
            }
            
            // 更新预测模型
            for (NodeManagementService.NodeInfo node : nodes) {
                predictionService.updatePredictionModel(node.getNodeId());
            }
            
        } catch (Exception e) {
            recordError("Metrics collection failed", e);
        }
    }
    
    /**
     * 检查是否可以扩容
     */
    private boolean canScaleUp() {
        if (currentNodeCount.get() >= scalingPolicy.maxNodes) {
            return false;
        }
        long lastScale = lastScaleUpTime.get();
        return System.currentTimeMillis() - lastScale > scalingPolicy.scaleUpCooldownMs;
    }
    
    /**
     * 检查是否可以缩容
     */
    private boolean canScaleDown() {
        if (currentNodeCount.get() <= scalingPolicy.minNodes) {
            return false;
        }
        long lastScale = lastScaleDownTime.get();
        return System.currentTimeMillis() - lastScale > scalingPolicy.scaleDownCooldownMs;
    }
    
    /**
     * 修剪历史记录
     */
    private void trimHistory() {
        while (scalingHistory.size() > MAX_HISTORY_SIZE) {
            scalingHistory.remove(0);
        }
    }
    
    /**
     * 记录错误
     */
    private void recordError(String message, Exception e) {
        System.err.println("[AutoScalingOrchestrator] " + message + ": " + e.getMessage());
    }
    
    // ============ 公共API ============
    
    /**
     * 手动触发扩容
     */
    public ScalingOperation manualScaleUp(int nodeCount, String reason) {
        if (nodeCount <= 0) {
            throw new IllegalArgumentException("Node count must be positive");
        }
        
        int availableSlots = scalingPolicy.maxNodes - currentNodeCount.get();
        int actualCount = Math.min(nodeCount, availableSlots);
        
        if (actualCount <= 0) {
            throw new IllegalStateException("Max node count reached");
        }
        
        // 连续触发多次扩容
        ScalingOperation lastOperation = null;
        for (int i = 0; i < actualCount; i += SCALE_UP_BATCH_SIZE) {
            lastOperation = triggerScaleUp(reason + " (manual batch " + (i / SCALE_UP_BATCH_SIZE + 1) + ")");
            if (lastOperation == null) {
                break;
            }
        }
        
        return lastOperation;
    }
    
    /**
     * 手动触发缩容
     */
    public ScalingOperation manualScaleDown(int nodeCount, String reason) {
        if (nodeCount <= 0) {
            throw new IllegalArgumentException("Node count must be positive");
        }
        
        int removableCount = currentNodeCount.get() - scalingPolicy.minNodes;
        int actualCount = Math.min(nodeCount, removableCount);
        
        if (actualCount <= 0) {
            throw new IllegalStateException("Min node count reached");
        }
        
        // 连续触发多次缩容
        ScalingOperation lastOperation = null;
        for (int i = 0; i < actualCount; i += SCALE_DOWN_BATCH_SIZE) {
            lastOperation = triggerScaleDown(reason + " (manual batch " + (i / SCALE_DOWN_BATCH_SIZE + 1) + ")");
            if (lastOperation == null) {
                break;
            }
        }
        
        return lastOperation;
    }
    
    /**
     * 获取扩缩容历史
     */
    public List<ScalingOperation> getScalingHistory(int limit) {
        int start = Math.max(0, scalingHistory.size() - limit);
        return scalingHistory.subList(start, scalingHistory.size());
    }
    
    /**
     * 获取当前状态
     */
    public ScalingStatus getStatus() {
        ScalingStatus status = new ScalingStatus();
        status.currentNodeCount = currentNodeCount.get();
        status.minNodes = scalingPolicy.minNodes;
        status.maxNodes = scalingPolicy.maxNodes;
        status.scalingInProgress = scalingInProgress.get();
        status.autoScalingEnabled = scalingPolicy.autoScalingEnabled;
        status.lastScaleUpTime = lastScaleUpTime.get();
        status.lastScaleDownTime = lastScaleDownTime.get();
        status.activeOperationCount = (int) scalingHistory.stream()
            .filter(op -> op.status == ScalingOperation.Status.IN_PROGRESS)
            .count();
        return status;
    }
    
    /**
     * 扩缩容状态
     */
    public static class ScalingStatus {
        public int currentNodeCount;
        public int minNodes;
        public int maxNodes;
        public boolean scalingInProgress;
        public boolean autoScalingEnabled;
        public long lastScaleUpTime;
        public long lastScaleDownTime;
        public int activeOperationCount;
    }
    
    /**
     * 更新扩缩容策略
     */
    public void updatePolicy(ScalingPolicy newPolicy) {
        this.scalingPolicy.autoScalingEnabled = newPolicy.autoScalingEnabled;
        this.scalingPolicy.minNodes = Math.max(MIN_NODE_COUNT, newPolicy.minNodes);
        this.scalingPolicy.maxNodes = Math.min(MAX_NODE_COUNT, newPolicy.maxNodes);
        this.scalingPolicy.scaleUpThresholdPercent = newPolicy.scaleUpThresholdPercent;
        this.scalingPolicy.scaleDownThresholdPercent = newPolicy.scaleDownThresholdPercent;
        this.scalingPolicy.predictiveScaling = newPolicy.predictiveScaling;
        this.scalingPolicy.connectionDraining = newPolicy.connectionDraining;
    }
    
    /**
     * 获取扩缩容策略
     */
    public ScalingPolicy getPolicy() {
        return this.scalingPolicy;
    }
}