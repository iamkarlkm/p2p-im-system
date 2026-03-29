package com.im.backend.websocket.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

/**
 * WebSocket负载均衡服务
 * 整合连接管理、一致性哈希路由和粘性会话管理，提供完整的负载均衡能力
 */
@Service
public class WebSocketLoadBalanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketLoadBalanceService.class);
    
    @Autowired
    private WebSocketConnectionManager connectionManager;
    
    @Autowired
    private ConsistentHashRouter hashRouter;
    
    @Autowired
    private StickySessionManager stickySessionManager;
    
    // 服务器节点配置
    private final Set<String> serverNodes = ConcurrentHashMap.newKeySet();
    
    // 本节点标识
    private volatile String localNodeId;
    
    // 健康检查调度器
    private final ScheduledExecutorService healthCheckScheduler = Executors.newSingleThreadScheduledExecutor(
        r -> {
            Thread t = new Thread(r, "health-check");
            t.setDaemon(true);
            return t;
        }
    );
    
    // 负载监控调度器
    private final ScheduledExecutorService loadMonitorScheduler = Executors.newSingleThreadScheduledExecutor(
        r -> {
            Thread t = new Thread(r, "load-monitor");
            t.setDaemon(true);
            return t;
        }
    );
    
    // 健康检查间隔（秒）
    private volatile int healthCheckIntervalSeconds = 30;
    
    // 负载监控间隔（秒）
    private volatile int loadMonitorIntervalSeconds = 60;
    
    // 节点健康状态
    private final Map<String, NodeStatus> nodeStatusMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 生成本节点ID
        this.localNodeId = generateNodeId();
        logger.info("WebSocket负载均衡服务初始化完成 - 本地节点: {}", localNodeId);
        
        // 启动健康检查
        startHealthCheck();
        
        // 启动负载监控
        startLoadMonitor();
    }
    
    /**
     * 生成本节点标识
     */
    private String generateNodeId() {
        String host = System.getenv("HOSTNAME");
        if (host == null) {
            host = System.getenv("COMPUTERNAME");
        }
        if (host == null) {
            host = "localhost";
        }
        String port = System.getProperty("server.port", "8080");
        return host + ":" + port;
    }
    
    /**
     * 注册服务器节点
     */
    public void registerServerNode(String nodeId) {
        registerServerNode(nodeId, 1);
    }
    
    /**
     * 注册服务器节点（带权重）
     */
    public void registerServerNode(String nodeId, int weight) {
        serverNodes.add(nodeId);
        hashRouter.addNode(nodeId, weight);
        nodeStatusMap.put(nodeId, new NodeStatus(nodeId));
        logger.info("服务器节点已注册 - 节点: {}, 权重: {}", nodeId, weight);
    }
    
    /**
     * 注销服务器节点
     */
    public void unregisterServerNode(String nodeId) {
        serverNodes.remove(nodeId);
        hashRouter.removeNode(nodeId);
        nodeStatusMap.remove(nodeId);
        logger.info("服务器节点已注销 - 节点: {}", nodeId);
    }
    
    /**
     * 为连接选择目标服务器节点
     */
    public String selectServerNode(String userId, String sessionId) {
        // 1. 检查是否存在粘性会话绑定
        String boundNode = stickySessionManager.getBoundNode(sessionId);
        if (boundNode != null && serverNodes.contains(boundNode)) {
            // 检查绑定节点是否健康
            ConsistentHashRouter.NodeHealth health = hashRouter.getNodeHealth(boundNode);
            if (health != null && health.isHealthy()) {
                logger.debug("使用粘性会话绑定 - 用户: {}, 会话: {}, 节点: {}", 
                           userId, sessionId, boundNode);
                return boundNode;
            }
        }
        
        // 2. 使用一致性哈希路由
        String selectedNode = hashRouter.route(userId);
        if (selectedNode == null) {
            logger.error("无法为用户 {} 选择服务器节点", userId);
            return null;
        }
        
        // 3. 创建粘性会话绑定
        stickySessionManager.createBinding(userId, sessionId, selectedNode);
        
        logger.debug("为用户选择服务器节点 - 用户: {}, 会话: {}, 节点: {}", 
                    userId, sessionId, selectedNode);
        
        return selectedNode;
    }
    
    /**
     * 处理连接建立
     */
    public boolean handleConnectionEstablished(String userId, String sessionId) {
        // 选择服务器节点
        String serverNode = selectServerNode(userId, sessionId);
        if (serverNode == null) {
            logger.error("无法为会话 {} 分配服务器节点", sessionId);
            return false;
        }
        
        // 创建会话对象并注册
        com.im.backend.websocket.session.WebSocketSession session = 
            new com.im.backend.websocket.session.WebSocketSession(
                sessionId, userId, serverNode
            );
        
        boolean registered = connectionManager.registerConnection(session);
        if (!registered) {
            logger.error("注册会话 {} 到连接管理器失败", sessionId);
            return false;
        }
        
        // 更新节点状态
        NodeStatus status = nodeStatusMap.get(serverNode);
        if (status != null) {
            status.incrementConnections();
        }
        
        logger.info("连接已建立 - 用户: {}, 会话: {}, 节点: {}", 
                   userId, sessionId, serverNode);
        
        return true;
    }
    
    /**
     * 处理连接断开
     */
    public void handleConnectionClosed(String sessionId) {
        // 获取绑定信息
        StickySessionManager.SessionBinding binding = stickySessionManager.getBinding(sessionId);
        if (binding != null) {
            String serverNode = binding.getServerNode();
            NodeStatus status = nodeStatusMap.get(serverNode);
            if (status != null) {
                status.decrementConnections();
            }
        }
        
        // 移除绑定和连接
        stickySessionManager.removeBinding(sessionId);
        connectionManager.unregisterConnection(sessionId);
        
        logger.info("连接已断开 - 会话: {}", sessionId);
    }
    
    /**
     * 迁移用户会话（用于节点下线或负载均衡）
     */
    public MigrationResult migrateUserSessions(String fromNode, String toNode) {
        logger.info("开始迁移用户会话 - 从 {} 到 {}", fromNode, toNode);
        
        Set<StickySessionManager.SessionBinding> bindings = 
            stickySessionManager.getNodeBindings(fromNode);
        
        int successCount = 0;
        int failCount = 0;
        List<String> migratedSessions = new ArrayList<>();
        
        for (StickySessionManager.SessionBinding binding : bindings) {
            boolean migrated = stickySessionManager.migrateSession(
                binding.getSessionId(), toNode
            );
            
            if (migrated) {
                successCount++;
                migratedSessions.add(binding.getSessionId());
            } else {
                failCount++;
            }
        }
        
        logger.info("会话迁移完成 - 成功: {}, 失败: {}", successCount, failCount);
        
        return new MigrationResult(fromNode, toNode, successCount, failCount, migratedSessions);
    }
    
    /**
     * 获取最优服务器节点（用于负载均衡重定向）
     */
    public String getOptimalServerNode() {
        String optimalNode = null;
        double minLoad = Double.MAX_VALUE;
        
        for (String nodeId : serverNodes) {
            NodeStatus status = nodeStatusMap.get(nodeId);
            if (status == null || !status.isHealthy()) {
                continue;
            }
            
            double load = status.getLoadPercentage();
            if (load < minLoad) {
                minLoad = load;
                optimalNode = nodeId;
            }
        }
        
        return optimalNode;
    }
    
    /**
     * 启动健康检查
     */
    private void startHealthCheck() {
        healthCheckScheduler.scheduleWithFixedDelay(
            this::performHealthCheck,
            healthCheckIntervalSeconds,
            healthCheckIntervalSeconds,
            TimeUnit.SECONDS
        );
    }
    
    /**
     * 执行健康检查
     */
    private void performHealthCheck() {
        for (String nodeId : serverNodes) {
            boolean healthy = checkNodeHealth(nodeId);
            NodeStatus status = nodeStatusMap.get(nodeId);
            if (status != null) {
                status.setHealthy(healthy);
                status.setLastHealthCheck(System.currentTimeMillis());
            }
            
            // 更新一致性哈希路由器的健康状态
            hashRouter.setNodeHealth(nodeId, healthy, 
                healthy ? "OK" : "Health check failed");
        }
    }
    
    /**
     * 检查节点健康状态
     */
    private boolean checkNodeHealth(String nodeId) {
        // 对于本地节点，直接返回健康
        if (nodeId.equals(localNodeId)) {
            return true;
        }
        
        // TODO: 实现远程节点健康检查（HTTP/RPC调用）
        // 这里简化处理，假设远程节点健康
        return true;
    }
    
    /**
     * 启动负载监控
     */
    private void startLoadMonitor() {
        loadMonitorScheduler.scheduleWithFixedDelay(
            this::monitorLoad,
            loadMonitorIntervalSeconds,
            loadMonitorIntervalSeconds,
            TimeUnit.SECONDS
        );
    }
    
    /**
     * 监控负载
     */
    private void monitorLoad() {
        for (String nodeId : serverNodes) {
            int connectionCount = connectionManager.getServerConnectionCount(nodeId);
            NodeStatus status = nodeStatusMap.get(nodeId);
            if (status != null) {
                status.updateConnectionCount(connectionCount);
            }
        }
        
        // 检查是否需要触发负载均衡
        checkLoadBalanceNeeded();
    }
    
    /**
     * 检查是否需要负载均衡
     */
    private void checkLoadBalanceNeeded() {
        double avgLoad = nodeStatusMap.values().stream()
            .filter(NodeStatus::isHealthy)
            .mapToDouble(NodeStatus::getLoadPercentage)
            .average()
            .orElse(0);
        
        double maxDeviation = nodeStatusMap.values().stream()
            .filter(NodeStatus::isHealthy)
            .mapToDouble(s -> Math.abs(s.getLoadPercentage() - avgLoad))
            .max()
            .orElse(0);
        
        // 如果最大偏差超过30%，触发负载均衡
        if (maxDeviation > 30) {
            logger.warn("检测到负载不均，平均负载: {}%，最大偏差: {}%", avgLoad, maxDeviation);
            triggerLoadBalancing();
        }
    }
    
    /**
     * 触发负载均衡
     */
    private void triggerLoadBalancing() {
        // 找出负载最高和最低的节点
        NodeStatus highestLoad = null;
        NodeStatus lowestLoad = null;
        
        for (NodeStatus status : nodeStatusMap.values()) {
            if (!status.isHealthy()) continue;
            
            if (highestLoad == null || status.getLoadPercentage() > highestLoad.getLoadPercentage()) {
                highestLoad = status;
            }
            if (lowestLoad == null || status.getLoadPercentage() < lowestLoad.getLoadPercentage()) {
                lowestLoad = status;
            }
        }
        
        if (highestLoad != null && lowestLoad != null && 
            highestLoad.getLoadPercentage() - lowestLoad.getLoadPercentage() > 30) {
            
            int sessionsToMigrate = (highestLoad.getConnectionCount() - lowestLoad.getConnectionCount()) / 2;
            logger.info("触发负载均衡 - 从 {} 迁移 {} 个会话到 {}", 
                       highestLoad.getNodeId(), sessionsToMigrate, lowestLoad.getNodeId());
            
            // 执行迁移（实际迁移逻辑需要更精细的控制）
            // migrateUserSessions(highestLoad.getNodeId(), lowestLoad.getNodeId());
        }
    }
    
    /**
     * 获取集群统计信息
     */
    public ClusterStats getClusterStats() {
        int totalNodes = serverNodes.size();
        int healthyNodes = (int) nodeStatusMap.values().stream()
            .filter(NodeStatus::isHealthy).count();
        int totalConnections = connectionManager.getTotalConnectionCount();
        double avgLoad = nodeStatusMap.values().stream()
            .mapToDouble(NodeStatus::getLoadPercentage)
            .average()
            .orElse(0);
        
        return new ClusterStats(totalNodes, healthyNodes, totalConnections, avgLoad);
    }
    
    /**
     * 关闭服务
     */
    public void shutdown() {
        healthCheckScheduler.shutdown();
        loadMonitorScheduler.shutdown();
        stickySessionManager.shutdown();
        
        try {
            healthCheckScheduler.awaitTermination(5, TimeUnit.SECONDS);
            loadMonitorScheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // ==================== 内部类 ====================
    
    /**
     * 节点状态
     */
    public static class NodeStatus {
        private final String nodeId;
        private volatile boolean healthy = true;
        private volatile long lastHealthCheck = System.currentTimeMillis();
        private volatile int connectionCount = 0;
        private volatile int maxConnections = 10000;
        
        public NodeStatus(String nodeId) {
            this.nodeId = nodeId;
        }
        
        // Getters and Setters
        public String getNodeId() { return nodeId; }
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        public long getLastHealthCheck() { return lastHealthCheck; }
        public void setLastHealthCheck(long time) { this.lastHealthCheck = time; }
        public int getConnectionCount() { return connectionCount; }
        
        public void updateConnectionCount(int count) { 
            this.connectionCount = count; 
        }
        
        public void incrementConnections() { 
            this.connectionCount++; 
        }
        
        public void decrementConnections() { 
            this.connectionCount--; 
        }
        
        public double getLoadPercentage() {
            return (double) connectionCount / maxConnections * 100;
        }
    }
    
    /**
     * 迁移结果
     */
    public static class MigrationResult {
        private final String fromNode;
        private final String toNode;
        private final int successCount;
        private final int failCount;
        private final List<String> migratedSessions;
        private final long timestamp;
        
        public MigrationResult(String fromNode, String toNode, int successCount, 
                             int failCount, List<String> migratedSessions) {
            this.fromNode = fromNode;
            this.toNode = toNode;
            this.successCount = successCount;
            this.failCount = failCount;
            this.migratedSessions = migratedSessions;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public String getFromNode() { return fromNode; }
        public String getToNode() { return toNode; }
        public int getSuccessCount() { return successCount; }
        public int getFailCount() { return failCount; }
        public List<String> getMigratedSessions() { return migratedSessions; }
        public long getTimestamp() { return timestamp; }
        
        public boolean isSuccessful() {
            return failCount == 0 && successCount > 0;
        }
    }
    
    /**
     * 集群统计信息
     */
    public static class ClusterStats {
        private final int totalNodes;
        private final int healthyNodes;
        private final int totalConnections;
        private final double averageLoad;
        private final long timestamp;
        
        public ClusterStats(int totalNodes, int healthyNodes, 
                          int totalConnections, double averageLoad) {
            this.totalNodes = totalNodes;
            this.healthyNodes = healthyNodes;
            this.totalConnections = totalConnections;
            this.averageLoad = averageLoad;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public int getTotalNodes() { return totalNodes; }
        public int getHealthyNodes() { return healthyNodes; }
        public int getUnhealthyNodes() { return totalNodes - healthyNodes; }
        public int getTotalConnections() { return totalConnections; }
        public double getAverageLoad() { return averageLoad; }
        public long getTimestamp() { return timestamp; }
        
        public double getHealthRatio() {
            return totalNodes > 0 ? (double) healthyNodes / totalNodes : 0;
        }
    }
}
