package com.im.backend.websocket.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 一致性哈希路由器
 * 用于WebSocket连接的分布式路由，确保相同用户总是路由到相同的服务器节点
 */
@Component
public class ConsistentHashRouter {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsistentHashRouter.class);
    
    // 虚拟节点数量（每个物理节点对应多个虚拟节点，用于负载均衡）
    private static final int VIRTUAL_NODES_PER_SERVER = 150;
    
    // 哈希环：哈希值 -> 服务器节点
    private final TreeMap<Long, String> hashRing = new TreeMap<>();
    
    // 物理节点列表
    private final List<String> physicalNodes = new CopyOnWriteArrayList<>();
    
    // 节点权重配置
    private final Map<String, Integer> nodeWeights = new ConcurrentHashMap<>();
    
    // 节点健康状态
    private final Map<String, NodeHealth> nodeHealthMap = new ConcurrentHashMap<>();
    
    // 哈希算法
    private final MessageDigest md5;
    
    public ConsistentHashRouter() {
        try {
            this.md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
    
    /**
     * 添加服务器节点
     */
    public synchronized void addNode(String nodeId) {
        addNode(nodeId, 1);
    }
    
    /**
     * 添加服务器节点（带权重）
     */
    public synchronized void addNode(String nodeId, int weight) {
        if (physicalNodes.contains(nodeId)) {
            logger.warn("节点 {} 已存在，跳过添加", nodeId);
            return;
        }
        
        physicalNodes.add(nodeId);
        nodeWeights.put(nodeId, weight);
        
        // 计算虚拟节点数量
        int virtualNodes = VIRTUAL_NODES_PER_SERVER * weight;
        
        // 添加虚拟节点到哈希环
        for (int i = 0; i < virtualNodes; i++) {
            String virtualNodeKey = nodeId + "#" + i;
            long hash = hash(virtualNodeKey);
            hashRing.put(hash, nodeId);
        }
        
        // 初始化健康状态
        nodeHealthMap.put(nodeId, new NodeHealth(nodeId));
        
        logger.info("服务器节点已添加到哈希环 - 节点: {}, 权重: {}, 虚拟节点: {}", 
                   nodeId, weight, virtualNodes);
    }
    
    /**
     * 移除服务器节点
     */
    public synchronized void removeNode(String nodeId) {
        if (!physicalNodes.contains(nodeId)) {
            logger.warn("节点 {} 不存在，跳过移除", nodeId);
            return;
        }
        
        physicalNodes.remove(nodeId);
        nodeWeights.remove(nodeId);
        nodeHealthMap.remove(nodeId);
        
        // 从哈希环中移除所有虚拟节点
        int weight = nodeWeights.getOrDefault(nodeId, 1);
        int virtualNodes = VIRTUAL_NODES_PER_SERVER * weight;
        
        for (int i = 0; i < virtualNodes; i++) {
            String virtualNodeKey = nodeId + "#" + i;
            long hash = hash(virtualNodeKey);
            hashRing.remove(hash);
        }
        
        logger.info("服务器节点已从哈希环移除 - 节点: {}", nodeId);
    }
    
    /**
     * 根据用户ID路由到服务器节点
     */
    public String route(String userId) {
        if (hashRing.isEmpty()) {
            logger.error("哈希环为空，无法路由用户 {}", userId);
            return null;
        }
        
        long hash = hash(userId);
        
        // 找到第一个大于等于hash的节点
        Map.Entry<Long, String> entry = hashRing.ceilingEntry(hash);
        
        // 如果找不到，则返回环的第一个节点（环形结构）
        if (entry == null) {
            entry = hashRing.firstEntry();
        }
        
        String selectedNode = entry.getValue();
        
        // 检查节点健康状态
        NodeHealth health = nodeHealthMap.get(selectedNode);
        if (health != null && !health.isHealthy()) {
            // 如果首选节点不健康，尝试找到下一个健康节点
            String healthyNode = findNextHealthyNode(hash);
            if (healthyNode != null) {
                selectedNode = healthyNode;
            }
        }
        
        logger.debug("用户 {} 路由到服务器节点 {}", userId, selectedNode);
        return selectedNode;
    }
    
    /**
     * 查找下一个健康的节点
     */
    private String findNextHealthyNode(long startHash) {
        // 从startHash开始向后查找
        Map.Entry<Long, String> entry = hashRing.higherEntry(startHash);
        
        while (entry != null) {
            String nodeId = entry.getValue();
            NodeHealth health = nodeHealthMap.get(nodeId);
            if (health != null && health.isHealthy()) {
                return nodeId;
            }
            entry = hashRing.higherEntry(entry.getKey());
        }
        
        // 绕回环的开头继续查找
        entry = hashRing.firstEntry();
        while (entry != null && entry.getKey() <= startHash) {
            String nodeId = entry.getValue();
            NodeHealth health = nodeHealthMap.get(nodeId);
            if (health != null && health.isHealthy()) {
                return nodeId;
            }
            entry = hashRing.higherEntry(entry.getKey());
        }
        
        return null;
    }
    
    /**
     * 获取节点的所有用户（用于节点下线时迁移）
     */
    public List<String> getUsersForNode(String nodeId, Collection<String> allUserIds) {
        List<String> users = new ArrayList<>();
        
        for (String userId : allUserIds) {
            String routedNode = route(userId);
            if (nodeId.equals(routedNode)) {
                users.add(userId);
            }
        }
        
        return users;
    }
    
    /**
     * 更新节点权重
     */
    public synchronized void updateNodeWeight(String nodeId, int newWeight) {
        if (!physicalNodes.contains(nodeId)) {
            logger.warn("节点 {} 不存在，无法更新权重", nodeId);
            return;
        }
        
        // 移除旧节点
        removeNode(nodeId);
        
        // 添加新权重的节点
        addNode(nodeId, newWeight);
        
        logger.info("节点 {} 权重已更新为 {}", nodeId, newWeight);
    }
    
    /**
     * 设置节点健康状态
     */
    public void setNodeHealth(String nodeId, boolean healthy, String reason) {
        NodeHealth health = nodeHealthMap.get(nodeId);
        if (health != null) {
            health.setHealthy(healthy);
            health.setLastCheckTime(System.currentTimeMillis());
            if (reason != null) {
                health.setStatusReason(reason);
            }
            
            logger.info("节点 {} 健康状态更新为 {} - {}", nodeId, healthy, reason);
        }
    }
    
    /**
     * 获取所有节点
     */
    public List<String> getAllNodes() {
        return new ArrayList<>(physicalNodes);
    }
    
    /**
     * 获取健康节点列表
     */
    public List<String> getHealthyNodes() {
        List<String> healthyNodes = new ArrayList<>();
        for (Map.Entry<String, NodeHealth> entry : nodeHealthMap.entrySet()) {
            if (entry.getValue().isHealthy()) {
                healthyNodes.add(entry.getKey());
            }
        }
        return healthyNodes;
    }
    
    /**
     * 获取节点数量
     */
    public int getNodeCount() {
        return physicalNodes.size();
    }
    
    /**
     * 获取健康节点数量
     */
    public int getHealthyNodeCount() {
        return getHealthyNodes().size();
    }
    
    /**
     * 检查节点是否存在
     */
    public boolean hasNode(String nodeId) {
        return physicalNodes.contains(nodeId);
    }
    
    /**
     * 获取节点健康状态
     */
    public NodeHealth getNodeHealth(String nodeId) {
        return nodeHealthMap.get(nodeId);
    }
    
    /**
     * 获取所有节点健康状态
     */
    public Map<String, NodeHealth> getAllNodeHealth() {
        return new HashMap<>(nodeHealthMap);
    }
    
    /**
     * 计算哈希值（使用MD5）
     */
    private long hash(String key) {
        md5.reset();
        md5.update(key.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md5.digest();
        
        // 取前8字节作为long值
        long hash = 0;
        for (int i = 0; i < 8; i++) {
            hash = (hash << 8) | (digest[i] & 0xFF);
        }
        
        return hash;
    }
    
    /**
     * 获取哈希环分布统计
     */
    public Map<String, Integer> getNodeVirtualNodeCount() {
        Map<String, Integer> countMap = new HashMap<>();
        for (String nodeId : physicalNodes) {
            int weight = nodeWeights.getOrDefault(nodeId, 1);
            countMap.put(nodeId, VIRTUAL_NODES_PER_SERVER * weight);
        }
        return countMap;
    }
    
    /**
     * 计算节点间的负载均衡度（标准差）
     */
    public double calculateBalanceScore() {
        if (physicalNodes.isEmpty()) {
            return 0.0;
        }
        
        Map<String, Integer> virtualNodeCounts = getNodeVirtualNodeCount();
        double sum = 0;
        for (int count : virtualNodeCounts.values()) {
            sum += count;
        }
        double average = sum / virtualNodeCounts.size();
        
        double varianceSum = 0;
        for (int count : virtualNodeCounts.values()) {
            varianceSum += Math.pow(count - average, 2);
        }
        
        return Math.sqrt(varianceSum / virtualNodeCounts.size());
    }
    
    /**
     * 获取路由统计信息
     */
    public RoutingStats getRoutingStats() {
        return new RoutingStats(
            physicalNodes.size(),
            getHealthyNodeCount(),
            hashRing.size(),
            calculateBalanceScore()
        );
    }
    
    // ==================== 内部类 ====================
    
    /**
     * 节点健康状态
     */
    public static class NodeHealth {
        private final String nodeId;
        private volatile boolean healthy = true;
        private volatile String statusReason = "OK";
        private volatile long lastCheckTime = System.currentTimeMillis();
        private volatile int consecutiveFailures = 0;
        
        public NodeHealth(String nodeId) {
            this.nodeId = nodeId;
        }
        
        // Getters and Setters
        public String getNodeId() { return nodeId; }
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { 
            this.healthy = healthy;
            if (healthy) {
                consecutiveFailures = 0;
            } else {
                consecutiveFailures++;
            }
        }
        public String getStatusReason() { return statusReason; }
        public void setStatusReason(String reason) { this.statusReason = reason; }
        public long getLastCheckTime() { return lastCheckTime; }
        public void setLastCheckTime(long time) { this.lastCheckTime = time; }
        public int getConsecutiveFailures() { return consecutiveFailures; }
        
        public long getSecondsSinceLastCheck() {
            return (System.currentTimeMillis() - lastCheckTime) / 1000;
        }
    }
    
    /**
     * 路由统计信息
     */
    public static class RoutingStats {
        private final int totalNodes;
        private final int healthyNodes;
        private final int totalVirtualNodes;
        private final double balanceScore;
        private final long timestamp;
        
        public RoutingStats(int totalNodes, int healthyNodes, 
                          int totalVirtualNodes, double balanceScore) {
            this.totalNodes = totalNodes;
            this.healthyNodes = healthyNodes;
            this.totalVirtualNodes = totalVirtualNodes;
            this.balanceScore = balanceScore;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public int getTotalNodes() { return totalNodes; }
        public int getHealthyNodes() { return healthyNodes; }
        public int getUnhealthyNodes() { return totalNodes - healthyNodes; }
        public int getTotalVirtualNodes() { return totalVirtualNodes; }
        public double getBalanceScore() { return balanceScore; }
        public long getTimestamp() { return timestamp; }
        
        public double getHealthRatio() {
            return totalNodes > 0 ? (double) healthyNodes / totalNodes : 0;
        }
    }
}
