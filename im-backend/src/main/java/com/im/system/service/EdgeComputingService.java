package com.im.system.service;

import com.im.system.entity.EdgeNodeEntity;
import com.im.system.entity.EdgeCacheMessageEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 边缘计算服务
 * 负责边缘节点管理、消息缓存、负载均衡和故障转移
 */
@Service
@Transactional
public class EdgeComputingService {

    // 内存缓存（实际应用中会使用Redis等分布式缓存）
    private final Map<UUID, EdgeNodeEntity> nodeCache = new ConcurrentHashMap<>();
    private final Map<String, EdgeCacheMessageEntity> messageCache = new ConcurrentHashMap<>();
    
    // 统计信息
    private final EdgeComputingMetrics metrics = new EdgeComputingMetrics();

    /**
     * 注册边缘节点
     */
    public EdgeNodeEntity registerNode(EdgeNodeEntity node) {
        node.setStatus("ONLINE");
        node.setLastHeartbeat(LocalDateTime.now());
        node.setHealthScore(1.0);
        node.setErrorCount(0);
        
        // 添加到缓存
        nodeCache.put(node.getId(), node);
        
        metrics.incrementNodeRegistrations();
        metrics.updateOnlineNodes(nodeCache.size());
        
        return node;
    }

    /**
     * 心跳检测和节点状态更新
     */
    public boolean heartbeat(UUID nodeId) {
        EdgeNodeEntity node = nodeCache.get(nodeId);
        if (node != null) {
            node.setLastHeartbeat(LocalDateTime.now());
            node.recordHeartbeat();
            metrics.incrementHeartbeats();
            return true;
        }
        return false;
    }

    /**
     * 获取最优边缘节点（负载均衡）
     */
    public EdgeNodeEntity getOptimalNode(String region, String nodeType) {
        List<EdgeNodeEntity> candidates = nodeCache.values().stream()
                .filter(node -> node.isOnline())
                .filter(node -> node.isHealthy())
                .filter(node -> node.hasCapacity())
                .filter(node -> region == null || region.equals(node.getRegion()))
                .filter(node -> nodeType == null || nodeType.equals(node.getNodeType()))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            metrics.incrementNodeSelectionFailures();
            return null;
        }

        // 负载均衡算法：基于健康分数、负载和延迟的综合评分
        EdgeNodeEntity bestNode = candidates.stream()
                .max(Comparator.comparingDouble(node -> {
                    double healthScore = node.getHealthScore() != null ? node.getHealthScore() : 0.5;
                    double loadPenalty = node.getCurrentLoad() != null ? (1.0 - node.getCurrentLoad()) : 0.7;
                    double latencyPenalty = node.getLatencyMs() != null ? 
                        (node.getLatencyMs() <= 100 ? 1.0 : 
                         node.getLatencyMs() <= 200 ? 0.8 : 
                         node.getLatencyMs() <= 500 ? 0.6 : 0.3) : 0.5;
                    double capacityBonus = node.getAvailableCapacity() != null ? 
                        (node.getAvailableCapacity() / 100.0) : 0.5;
                    
                    return (healthScore * 0.4) + (loadPenalty * 0.3) + 
                           (latencyPenalty * 0.2) + (capacityBonus * 0.1);
                }))
                .orElse(null);

        if (bestNode != null) {
            metrics.incrementNodeSelections();
            // 更新节点负载
            double currentLoad = bestNode.getCurrentLoad() != null ? bestNode.getCurrentLoad() : 0.0;
            bestNode.setCurrentLoad(Math.min(1.0, currentLoad + 0.05)); // 增加5%负载
            bestNode.setAvailableCapacity(
                bestNode.getAvailableCapacity() != null ? 
                Math.max(0, bestNode.getAvailableCapacity() - 5) : 95 // 减少5%可用容量
            );
        }

        return bestNode;
    }

    /**
     * 缓存消息到边缘节点
     */
    public EdgeCacheMessageEntity cacheMessage(String messageId, UUID edgeNodeId, 
                                              String userId, String conversationId,
                                              String messageType, String content) {
        
        EdgeCacheMessageEntity cachedMessage = new EdgeCacheMessageEntity(
            messageId, edgeNodeId, userId, conversationId, messageType, content
        );
        
        // 设置TTL
        cachedMessage.setTtlFromNow();
        
        // 压缩内容
        cachedMessage.compressContent();
        
        // 计算内容哈希
        cachedMessage.setContentHash(Integer.toHexString(content.hashCode()));
        
        // 添加到缓存
        messageCache.put(generateCacheKey(messageId, edgeNodeId), cachedMessage);
        
        // 更新节点统计
        EdgeNodeEntity node = nodeCache.get(edgeNodeId);
        if (node != null) {
            // 减少节点可用容量（存储占用）
            Integer currentCapacity = node.getAvailableCapacity();
            if (currentCapacity != null) {
                node.setAvailableCapacity(Math.max(0, currentCapacity - 1));
            }
        }
        
        metrics.incrementMessagesCached();
        metrics.updateCacheSize(messageCache.size());
        
        return cachedMessage;
    }

    /**
     * 从边缘缓存获取消息
     */
    public EdgeCacheMessageEntity getCachedMessage(String messageId, UUID edgeNodeId) {
        String cacheKey = generateCacheKey(messageId, edgeNodeId);
        EdgeCacheMessageEntity message = messageCache.get(cacheKey);
        
        if (message != null && message.isActive() && !message.isExpired()) {
            message.incrementAccess();
            metrics.incrementCacheHits();
            return message;
        }
        
        metrics.incrementCacheMisses();
        return null;
    }

    /**
     * 同步缓存消息到中心服务器
     */
    public boolean syncCachedMessage(UUID cacheId) {
        EdgeCacheMessageEntity message = findCacheById(cacheId);
        if (message == null) {
            return false;
        }

        try {
            // 模拟同步到中心服务器
            Thread.sleep(50); // 模拟网络延迟
            
            message.markAsSynced();
            metrics.incrementMessagesSynced();
            
            // 更新带宽节省统计
            message.calculateBandwidthSavings();
            
            return true;
        } catch (Exception e) {
            message.markAsFailed(e.getMessage());
            metrics.incrementSyncFailures();
            return false;
        }
    }

    /**
     * 清理过期缓存
     */
    public int cleanupExpiredCache() {
        int cleanedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        
        Iterator<Map.Entry<String, EdgeCacheMessageEntity>> iterator = messageCache.entrySet().iterator();
        while (iterator.hasNext()) {
            EdgeCacheMessageEntity message = iterator.next().getValue();
            
            // 清理条件：过期且未被固定
            if (message.isExpired() && !message.getIsPinned()) {
                iterator.remove();
                cleanedCount++;
                
                // 释放节点容量
                EdgeNodeEntity node = nodeCache.get(message.getEdgeNodeId());
                if (node != null) {
                    Integer currentCapacity = node.getAvailableCapacity();
                    if (currentCapacity != null) {
                        node.setAvailableCapacity(Math.min(100, currentCapacity + 1));
                    }
                }
            }
        }
        
        metrics.incrementCacheCleanups();
        metrics.updateCacheSize(messageCache.size());
        
        return cleanedCount;
    }

    /**
     * 故障转移：将消息从故障节点迁移到健康节点
     */
    public int migrateMessagesFromFailedNode(UUID failedNodeId) {
        List<EdgeCacheMessageEntity> messagesToMigrate = messageCache.values().stream()
                .filter(msg -> failedNodeId.equals(msg.getEdgeNodeId()))
                .filter(msg -> msg.isActive())
                .collect(Collectors.toList());

        if (messagesToMigrate.isEmpty()) {
            return 0;
        }

        // 查找可用的健康节点
        EdgeNodeEntity targetNode = getOptimalNode(null, "HYBRID");
        if (targetNode == null) {
            return 0;
        }

        int migratedCount = 0;
        for (EdgeCacheMessageEntity message : messagesToMigrate) {
            // 迁移消息到新节点
            EdgeCacheMessageEntity migrated = cacheMessage(
                message.getMessageId(),
                targetNode.getId(),
                message.getUserId(),
                message.getConversationId(),
                message.getMessageType(),
                message.getContent()
            );
            
            // 复制其他属性
            migrated.setCachePriority(message.getCachePriority());
            migrated.setTtlSeconds(message.getTtlSeconds());
            migrated.setIsPinned(message.getIsPinned());
            migrated.setIsOfflineAvailable(message.getIsOfflineAvailable());
            
            // 移除原缓存条目
            messageCache.remove(generateCacheKey(
                message.getMessageId(), 
                failedNodeId
            ));
            
            migratedCount++;
        }

        metrics.incrementMessagesMigrated(migratedCount);
        return migratedCount;
    }

    /**
     * 获取节点健康状态报告
     */
    public Map<String, Object> getNodeHealthReport() {
        Map<String, Object> report = new HashMap<>();
        
        long onlineCount = nodeCache.values().stream()
                .filter(EdgeNodeEntity::isOnline)
                .count();
        
        long healthyCount = nodeCache.values().stream()
                .filter(EdgeNodeEntity::isHealthy)
                .count();
        
        double avgHealthScore = nodeCache.values().stream()
                .filter(node -> node.getHealthScore() != null)
                .mapToDouble(EdgeNodeEntity::getHealthScore)
                .average()
                .orElse(0.0);
        
        report.put("totalNodes", nodeCache.size());
        report.put("onlineNodes", onlineCount);
        report.put("healthyNodes", healthyCount);
        report.put("averageHealthScore", String.format("%.2f", avgHealthScore));
        report.put("cacheSize", messageCache.size());
        report.put("metrics", metrics.getMetricsSnapshot());
        
        return report;
    }

    /**
     * 批量同步缓存
     */
    public SyncResult batchSyncCache(List<UUID> cacheIds) {
        SyncResult result = new SyncResult();
        
        for (UUID cacheId : cacheIds) {
            try {
                boolean success = syncCachedMessage(cacheId);
                if (success) {
                    result.incrementSuccess();
                } else {
                    result.incrementFailed();
                }
            } catch (Exception e) {
                result.incrementFailed();
            }
        }
        
        return result;
    }

    // 辅助方法
    private String generateCacheKey(String messageId, UUID nodeId) {
        return messageId + "::" + nodeId.toString();
    }

    private EdgeCacheMessageEntity findCacheById(UUID cacheId) {
        return messageCache.values().stream()
                .filter(msg -> cacheId.equals(msg.getId()))
                .findFirst()
                .orElse(null);
    }

    // 内部类：统计指标
    public static class EdgeComputingMetrics {
        private long nodeRegistrations = 0;
        private long heartbeats = 0;
        private long nodeSelections = 0;
        private long nodeSelectionFailures = 0;
        private long messagesCached = 0;
        private long messagesSynced = 0;
        private long syncFailures = 0;
        private long cacheHits = 0;
        private long cacheMisses = 0;
        private long cacheCleanups = 0;
        private long messagesMigrated = 0;
        private int onlineNodes = 0;
        private int cacheSize = 0;

        public void incrementNodeRegistrations() { nodeRegistrations++; }
        public void incrementHeartbeats() { heartbeats++; }
        public void incrementNodeSelections() { nodeSelections++; }
        public void incrementNodeSelectionFailures() { nodeSelectionFailures++; }
        public void incrementMessagesCached() { messagesCached++; }
        public void incrementMessagesSynced() { messagesSynced++; }
        public void incrementSyncFailures() { syncFailures++; }
        public void incrementCacheHits() { cacheHits++; }
        public void incrementCacheMisses() { cacheMisses++; }
        public void incrementCacheCleanups() { cacheCleanups++; }
        public void incrementMessagesMigrated(long count) { messagesMigrated += count; }
        
        public void updateOnlineNodes(int count) { onlineNodes = count; }
        public void updateCacheSize(int size) { cacheSize = size; }

        public Map<String, Object> getMetricsSnapshot() {
            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("nodeRegistrations", nodeRegistrations);
            snapshot.put("heartbeats", heartbeats);
            snapshot.put("nodeSelections", nodeSelections);
            snapshot.put("nodeSelectionFailures", nodeSelectionFailures);
            snapshot.put("messagesCached", messagesCached);
            snapshot.put("messagesSynced", messagesSynced);
            snapshot.put("syncFailures", syncFailures);
            snapshot.put("cacheHits", cacheHits);
            snapshot.put("cacheMisses", cacheMisses);
            snapshot.put("cacheHitRate", cacheHits + cacheMisses > 0 ? 
                String.format("%.2f%%", (cacheHits * 100.0) / (cacheHits + cacheMisses)) : "0%");
            snapshot.put("cacheCleanups", cacheCleanups);
            snapshot.put("messagesMigrated", messagesMigrated);
            snapshot.put("onlineNodes", onlineNodes);
            snapshot.put("cacheSize", cacheSize);
            return snapshot;
        }
    }

    // 内部类：同步结果
    public static class SyncResult {
        private int successCount = 0;
        private int failedCount = 0;

        public void incrementSuccess() { successCount++; }
        public void incrementFailed() { failedCount++; }

        public int getSuccessCount() { return successCount; }
        public int getFailedCount() { return failedCount; }
        public int getTotalCount() { return successCount + failedCount; }
        public double getSuccessRate() { 
            return getTotalCount() > 0 ? (successCount * 100.0) / getTotalCount() : 0.0; 
        }
    }

    /**
     * 获取服务统计信息
     */
    public EdgeComputingMetrics getMetrics() {
        return metrics;
    }

    /**
     * 获取所有在线节点
     */
    public List<EdgeNodeEntity> getOnlineNodes() {
        return nodeCache.values().stream()
                .filter(EdgeNodeEntity::isOnline)
                .collect(Collectors.toList());
    }

    /**
     * 获取节点缓存大小
     */
    public int getCacheSize() {
        return messageCache.size();
    }

    /**
     * 清除所有缓存（测试用）
     */
    public void clearAllCache() {
        nodeCache.clear();
        messageCache.clear();
        // 重置指标
        metrics.updateOnlineNodes(0);
        metrics.updateCacheSize(0);
    }
}