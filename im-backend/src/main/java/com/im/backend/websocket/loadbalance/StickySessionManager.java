package com.im.backend.websocket.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 粘性会话管理器
 * 管理用户会话与服务器节点的绑定关系，确保会话在集群中的一致性
 */
@Component
public class StickySessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(StickySessionManager.class);
    
    // 会话ID -> 会话绑定信息
    private final Map<String, SessionBinding> sessionBindings = new ConcurrentHashMap<>();
    
    // 用户ID -> 会话ID集合
    private final Map<String, Set<String>> userSessionIndex = new ConcurrentHashMap<>();
    
    // 服务器节点 -> 会话ID集合
    private final Map<String, Set<String>> nodeSessionIndex = new ConcurrentHashMap<>();
    
    // 会话超时时间（毫秒）
    private volatile long sessionTimeoutMs = 30 * 60 * 1000; // 30分钟
    
    // 清理任务调度器
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor(
        r -> {
            Thread t = new Thread(r, "sticky-session-cleanup");
            t.setDaemon(true);
            return t;
        }
    );
    
    public StickySessionManager() {
        // 启动定期清理任务，每5分钟执行一次
        cleanupScheduler.scheduleWithFixedDelay(
            this::cleanupExpiredSessions,
            5, 5, TimeUnit.MINUTES
        );
    }
    
    /**
     * 创建会话绑定
     */
    public SessionBinding createBinding(String userId, String sessionId, String serverNode) {
        SessionBinding binding = new SessionBinding(
            sessionId,
            userId,
            serverNode,
            System.currentTimeMillis()
        );
        
        sessionBindings.put(sessionId, binding);
        
        // 更新索引
        userSessionIndex.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                       .add(sessionId);
        nodeSessionIndex.computeIfAbsent(serverNode, k -> ConcurrentHashMap.newKeySet())
                       .add(sessionId);
        
        logger.debug("创建会话绑定 - 用户: {}, 会话: {}, 节点: {}", 
                    userId, sessionId, serverNode);
        
        return binding;
    }
    
    /**
     * 获取会话绑定
     */
    public SessionBinding getBinding(String sessionId) {
        SessionBinding binding = sessionBindings.get(sessionId);
        if (binding != null) {
            binding.updateLastAccessTime();
        }
        return binding;
    }
    
    /**
     * 获取会话绑定的服务器节点
     */
    public String getBoundNode(String sessionId) {
        SessionBinding binding = sessionBindings.get(sessionId);
        return binding != null ? binding.getServerNode() : null;
    }
    
    /**
     * 检查会话是否存在绑定
     */
    public boolean hasBinding(String sessionId) {
        return sessionBindings.containsKey(sessionId);
    }
    
    /**
     * 移除会话绑定
     */
    public void removeBinding(String sessionId) {
        SessionBinding binding = sessionBindings.remove(sessionId);
        if (binding == null) {
            return;
        }
        
        // 更新索引
        String userId = binding.getUserId();
        String serverNode = binding.getServerNode();
        
        Set<String> userSessions = userSessionIndex.get(userId);
        if (userSessions != null) {
            userSessions.remove(sessionId);
            if (userSessions.isEmpty()) {
                userSessionIndex.remove(userId);
            }
        }
        
        Set<String> nodeSessions = nodeSessionIndex.get(serverNode);
        if (nodeSessions != null) {
            nodeSessions.remove(sessionId);
            if (nodeSessions.isEmpty()) {
                nodeSessionIndex.remove(serverNode);
            }
        }
        
        logger.debug("移除会话绑定 - 会话: {}, 用户: {}, 节点: {}", 
                    sessionId, userId, serverNode);
    }
    
    /**
     * 迁移会话到新的服务器节点
     */
    public boolean migrateSession(String sessionId, String newServerNode) {
        SessionBinding binding = sessionBindings.get(sessionId);
        if (binding == null) {
            logger.warn("无法迁移不存在的会话: {}", sessionId);
            return false;
        }
        
        String oldNode = binding.getServerNode();
        
        // 从旧节点索引中移除
        Set<String> oldNodeSessions = nodeSessionIndex.get(oldNode);
        if (oldNodeSessions != null) {
            oldNodeSessions.remove(sessionId);
        }
        
        // 更新绑定
        binding.setServerNode(newServerNode);
        binding.setMigrationCount(binding.getMigrationCount() + 1);
        binding.setLastMigrationTime(System.currentTimeMillis());
        binding.updateLastAccessTime();
        
        // 添加到新节点索引
        nodeSessionIndex.computeIfAbsent(newServerNode, k -> ConcurrentHashMap.newKeySet())
                       .add(sessionId);
        
        logger.info("会话已迁移 - 会话: {}, 从节点 {} 到节点 {}", 
                   sessionId, oldNode, newServerNode);
        
        return true;
    }
    
    /**
     * 获取用户的所有会话绑定
     */
    public Set<SessionBinding> getUserBindings(String userId) {
        Set<String> sessionIds = userSessionIndex.get(userId);
        if (sessionIds == null) {
            return java.util.Collections.emptySet();
        }
        
        Set<SessionBinding> bindings = ConcurrentHashMap.newKeySet();
        for (String sessionId : sessionIds) {
            SessionBinding binding = sessionBindings.get(sessionId);
            if (binding != null) {
                bindings.add(binding);
            }
        }
        return bindings;
    }
    
    /**
     * 获取服务器节点上的所有会话
     */
    public Set<SessionBinding> getNodeBindings(String serverNode) {
        Set<String> sessionIds = nodeSessionIndex.get(serverNode);
        if (sessionIds == null) {
            return java.util.Collections.emptySet();
        }
        
        Set<SessionBinding> bindings = ConcurrentHashMap.newKeySet();
        for (String sessionId : sessionIds) {
            SessionBinding binding = sessionBindings.get(sessionId);
            if (binding != null) {
                bindings.add(binding);
            }
        }
        return bindings;
    }
    
    /**
     * 获取服务器节点上的会话数量
     */
    public int getNodeSessionCount(String serverNode) {
        Set<String> sessionIds = nodeSessionIndex.get(serverNode);
        return sessionIds == null ? 0 : sessionIds.size();
    }
    
    /**
     * 获取用户的会话数量
     */
    public int getUserSessionCount(String userId) {
        Set<String> sessionIds = userSessionIndex.get(userId);
        return sessionIds == null ? 0 : sessionIds.size();
    }
    
    /**
     * 获取所有绑定的会话数量
     */
    public int getTotalBindingCount() {
        return sessionBindings.size();
    }
    
    /**
     * 获取会话统计信息
     */
    public SessionStats getSessionStats() {
        return new SessionStats(
            sessionBindings.size(),
            userSessionIndex.size(),
            nodeSessionIndex.size(),
            calculateAverageSessionAge(),
            calculateMigrationRate()
        );
    }
    
    /**
     * 计算平均会话年龄
     */
    private double calculateAverageSessionAge() {
        if (sessionBindings.isEmpty()) {
            return 0;
        }
        
        long now = System.currentTimeMillis();
        long totalAge = 0;
        for (SessionBinding binding : sessionBindings.values()) {
            totalAge += (now - binding.getCreatedAt());
        }
        
        return (double) totalAge / sessionBindings.size() / 1000; // 返回秒
    }
    
    /**
     * 计算迁移率
     */
    private double calculateMigrationRate() {
        if (sessionBindings.isEmpty()) {
            return 0;
        }
        
        int migratedCount = 0;
        for (SessionBinding binding : sessionBindings.values()) {
            if (binding.getMigrationCount() > 0) {
                migratedCount++;
            }
        }
        
        return (double) migratedCount / sessionBindings.size() * 100;
    }
    
    /**
     * 清理过期会话
     */
    private void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        int cleanedCount = 0;
        
        for (Map.Entry<String, SessionBinding> entry : sessionBindings.entrySet()) {
            SessionBinding binding = entry.getValue();
            if (now - binding.getLastAccessTime() > sessionTimeoutMs) {
                removeBinding(entry.getKey());
                cleanedCount++;
            }
        }
        
        if (cleanedCount > 0) {
            logger.info("清理过期会话完成 - 清理数量: {}", cleanedCount);
        }
    }
    
    /**
     * 设置会话超时时间
     */
    public void setSessionTimeout(long timeoutMs) {
        this.sessionTimeoutMs = timeoutMs;
        logger.info("会话超时时间已设置为 {} 毫秒", timeoutMs);
    }
    
    /**
     * 停止清理调度器
     */
    public void shutdown() {
        cleanupScheduler.shutdown();
        try {
            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // ==================== 内部类 ====================
    
    /**
     * 会话绑定信息
     */
    public static class SessionBinding {
        private final String sessionId;
        private final String userId;
        private volatile String serverNode;
        private final long createdAt;
        private volatile long lastAccessTime;
        private volatile int migrationCount;
        private volatile long lastMigrationTime;
        private volatile Map<String, Object> attributes;
        
        public SessionBinding(String sessionId, String userId, String serverNode, long createdAt) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.serverNode = serverNode;
            this.createdAt = createdAt;
            this.lastAccessTime = createdAt;
            this.migrationCount = 0;
            this.lastMigrationTime = 0;
            this.attributes = new ConcurrentHashMap<>();
        }
        
        // Getters
        public String getSessionId() { return sessionId; }
        public String getUserId() { return userId; }
        public String getServerNode() { return serverNode; }
        public long getCreatedAt() { return createdAt; }
        public long getLastAccessTime() { return lastAccessTime; }
        public int getMigrationCount() { return migrationCount; }
        public long getLastMigrationTime() { return lastMigrationTime; }
        
        // Setters
        public void setServerNode(String serverNode) { this.serverNode = serverNode; }
        public void setMigrationCount(int count) { this.migrationCount = count; }
        public void setLastMigrationTime(long time) { this.lastMigrationTime = time; }
        
        public void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        public long getSessionAgeMs() {
            return System.currentTimeMillis() - createdAt;
        }
        
        public long getIdleTimeMs() {
            return System.currentTimeMillis() - lastAccessTime;
        }
        
        public void setAttribute(String key, Object value) {
            attributes.put(key, value);
        }
        
        public Object getAttribute(String key) {
            return attributes.get(key);
        }
        
        public void removeAttribute(String key) {
            attributes.remove(key);
        }
        
        public boolean isMigrated() {
            return migrationCount > 0;
        }
    }
    
    /**
     * 会话统计信息
     */
    public static class SessionStats {
        private final int totalBindings;
        private final int uniqueUsers;
        private final int activeNodes;
        private final double averageSessionAgeSeconds;
        private final double migrationRate;
        private final long timestamp;
        
        public SessionStats(int totalBindings, int uniqueUsers, int activeNodes,
                          double averageSessionAgeSeconds, double migrationRate) {
            this.totalBindings = totalBindings;
            this.uniqueUsers = uniqueUsers;
            this.activeNodes = activeNodes;
            this.averageSessionAgeSeconds = averageSessionAgeSeconds;
            this.migrationRate = migrationRate;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public int getTotalBindings() { return totalBindings; }
        public int getUniqueUsers() { return uniqueUsers; }
        public int getActiveNodes() { return activeNodes; }
        public double getAverageSessionAgeSeconds() { return averageSessionAgeSeconds; }
        public double getMigrationRate() { return migrationRate; }
        public long getTimestamp() { return timestamp; }
        
        public double getAverageSessionsPerUser() {
            return uniqueUsers > 0 ? (double) totalBindings / uniqueUsers : 0;
        }
        
        public double getAverageSessionsPerNode() {
            return activeNodes > 0 ? (double) totalBindings / activeNodes : 0;
        }
    }
}
