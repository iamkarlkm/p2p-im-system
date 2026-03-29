package com.im.backend.websocket.loadbalance;

import com.im.backend.websocket.session.WebSocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WebSocket连接管理器
 * 管理所有活跃的WebSocket连接，提供连接统计和负载信息
 */
@Component
public class WebSocketConnectionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnectionManager.class);
    
    // 用户ID -> 会话列表 (一个用户可能有多个设备连接)
    private final Map<String, Map<String, WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    
    // 会话ID -> 会话映射
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    
    // 服务器节点 -> 连接数
    private final Map<String, AtomicInteger> serverConnectionCounts = new ConcurrentHashMap<>();
    
    // 连接统计
    private final AtomicLong totalConnections = new AtomicLong(0);
    private final AtomicLong totalDisconnections = new AtomicLong(0);
    private final AtomicLong totalMessagesSent = new AtomicLong(0);
    private final AtomicLong totalMessagesReceived = new AtomicLong(0);
    
    // 最大连接数限制
    private volatile int maxConnectionsPerServer = 10000;
    private volatile int maxConnectionsPerUser = 5;
    private volatile boolean enableConnectionLimit = true;
    
    /**
     * 注册新连接
     */
    public boolean registerConnection(WebSocketSession session) {
        if (session == null || session.getSessionId() == null) {
            logger.warn("尝试注册无效的会话");
            return false;
        }
        
        String userId = session.getUserId();
        String sessionId = session.getSessionId();
        String serverNode = session.getServerNode();
        
        // 检查用户连接数限制
        if (enableConnectionLimit && !checkUserConnectionLimit(userId)) {
            logger.warn("用户 {} 已达到最大连接数限制", userId);
            return false;
        }
        
        // 检查服务器连接数限制
        if (enableConnectionLimit && !checkServerConnectionLimit(serverNode)) {
            logger.warn("服务器节点 {} 已达到最大连接数限制", serverNode);
            return false;
        }
        
        // 注册会话
        userSessions.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                    .put(sessionId, session);
        sessionMap.put(sessionId, session);
        
        // 更新服务器连接计数
        serverConnectionCounts.computeIfAbsent(serverNode, k -> new AtomicInteger(0))
                             .incrementAndGet();
        
        totalConnections.incrementAndGet();
        
        logger.info("WebSocket连接已注册 - 用户: {}, 会话: {}, 服务器: {}", 
                   userId, sessionId, serverNode);
        
        return true;
    }
    
    /**
     * 注销连接
     */
    public void unregisterConnection(String sessionId) {
        WebSocketSession session = sessionMap.remove(sessionId);
        if (session == null) {
            return;
        }
        
        String userId = session.getUserId();
        String serverNode = session.getServerNode();
        
        // 从用户会话列表中移除
        Map<String, WebSocketSession> userSessionMap = userSessions.get(userId);
        if (userSessionMap != null) {
            userSessionMap.remove(sessionId);
            if (userSessionMap.isEmpty()) {
                userSessions.remove(userId);
            }
        }
        
        // 更新服务器连接计数
        AtomicInteger count = serverConnectionCounts.get(serverNode);
        if (count != null) {
            count.decrementAndGet();
        }
        
        totalDisconnections.incrementAndGet();
        
        logger.info("WebSocket连接已注销 - 用户: {}, 会话: {}, 服务器: {}", 
                   userId, sessionId, serverNode);
    }
    
    /**
     * 检查用户连接数限制
     */
    private boolean checkUserConnectionLimit(String userId) {
        Map<String, WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null) {
            return true;
        }
        return sessions.size() < maxConnectionsPerUser;
    }
    
    /**
     * 检查服务器连接数限制
     */
    private boolean checkServerConnectionLimit(String serverNode) {
        AtomicInteger count = serverConnectionCounts.get(serverNode);
        if (count == null) {
            return true;
        }
        return count.get() < maxConnectionsPerServer;
    }
    
    /**
     * 获取会话
     */
    public WebSocketSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }
    
    /**
     * 获取用户的所有会话
     */
    public Collection<WebSocketSession> getUserSessions(String userId) {
        Map<String, WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null) {
            return java.util.Collections.emptyList();
        }
        return sessions.values();
    }
    
    /**
     * 获取用户会话数量
     */
    public int getUserSessionCount(String userId) {
        Map<String, WebSocketSession> sessions = userSessions.get(userId);
        return sessions == null ? 0 : sessions.size();
    }
    
    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(String userId) {
        return userSessions.containsKey(userId);
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }
    
    /**
     * 获取总连接数
     */
    public int getTotalConnectionCount() {
        return sessionMap.size();
    }
    
    /**
     * 获取服务器连接数
     */
    public int getServerConnectionCount(String serverNode) {
        AtomicInteger count = serverConnectionCounts.get(serverNode);
        return count == null ? 0 : count.get();
    }
    
    /**
     * 获取所有服务器连接分布
     */
    public Map<String, Integer> getServerConnectionDistribution() {
        Map<String, Integer> distribution = new ConcurrentHashMap<>();
        for (Map.Entry<String, AtomicInteger> entry : serverConnectionCounts.entrySet()) {
            distribution.put(entry.getKey(), entry.getValue().get());
        }
        return distribution;
    }
    
    /**
     * 更新消息发送计数
     */
    public void incrementMessagesSent() {
        totalMessagesSent.incrementAndGet();
    }
    
    /**
     * 更新消息接收计数
     */
    public void incrementMessagesReceived() {
        totalMessagesReceived.incrementAndGet();
    }
    
    /**
     * 获取连接统计信息
     */
    public ConnectionStats getConnectionStats() {
        return new ConnectionStats(
            totalConnections.get(),
            totalDisconnections.get(),
            sessionMap.size(),
            userSessions.size(),
            totalMessagesSent.get(),
            totalMessagesReceived.get()
        );
    }
    
    /**
     * 获取服务器负载信息
     */
    public ServerLoadInfo getServerLoadInfo(String serverNode) {
        int currentConnections = getServerConnectionCount(serverNode);
        double loadPercentage = (double) currentConnections / maxConnectionsPerServer * 100;
        
        return new ServerLoadInfo(
            serverNode,
            currentConnections,
            maxConnectionsPerServer,
            loadPercentage,
            loadPercentage < 70 ? LoadLevel.LOW : loadPercentage < 90 ? LoadLevel.MEDIUM : LoadLevel.HIGH
        );
    }
    
    /**
     * 设置最大连接数限制
     */
    public void setMaxConnectionsPerServer(int max) {
        this.maxConnectionsPerServer = max;
    }
    
    public void setMaxConnectionsPerUser(int max) {
        this.maxConnectionsPerUser = max;
    }
    
    public void setEnableConnectionLimit(boolean enable) {
        this.enableConnectionLimit = enable;
    }
    
    /**
     * 关闭所有连接
     */
    public void shutdownAllConnections() {
        logger.info("正在关闭所有WebSocket连接...");
        
        for (WebSocketSession session : sessionMap.values()) {
            try {
                session.close();
            } catch (Exception e) {
                logger.error("关闭会话失败: {}", session.getSessionId(), e);
            }
        }
        
        sessionMap.clear();
        userSessions.clear();
        serverConnectionCounts.clear();
        
        logger.info("所有WebSocket连接已关闭");
    }
    
    // ==================== 内部类 ====================
    
    /**
     * 连接统计信息
     */
    public static class ConnectionStats {
        private final long totalConnections;
        private final long totalDisconnections;
        private final long activeConnections;
        private final long onlineUsers;
        private final long totalMessagesSent;
        private final long totalMessagesReceived;
        private final long timestamp;
        
        public ConnectionStats(long totalConnections, long totalDisconnections,
                             long activeConnections, long onlineUsers,
                             long totalMessagesSent, long totalMessagesReceived) {
            this.totalConnections = totalConnections;
            this.totalDisconnections = totalDisconnections;
            this.activeConnections = activeConnections;
            this.onlineUsers = onlineUsers;
            this.totalMessagesSent = totalMessagesSent;
            this.totalMessagesReceived = totalMessagesReceived;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public long getTotalConnections() { return totalConnections; }
        public long getTotalDisconnections() { return totalDisconnections; }
        public long getActiveConnections() { return activeConnections; }
        public long getOnlineUsers() { return onlineUsers; }
        public long getTotalMessagesSent() { return totalMessagesSent; }
        public long getTotalMessagesReceived() { return totalMessagesReceived; }
        public long getTimestamp() { return timestamp; }
        
        public double getAverageMessagesPerConnection() {
            return activeConnections > 0 ? (double) totalMessagesSent / activeConnections : 0;
        }
    }
    
    /**
     * 服务器负载信息
     */
    public static class ServerLoadInfo {
        private final String serverNode;
        private final int currentConnections;
        private final int maxConnections;
        private final double loadPercentage;
        private final LoadLevel loadLevel;
        private final long timestamp;
        
        public ServerLoadInfo(String serverNode, int currentConnections, int maxConnections,
                            double loadPercentage, LoadLevel loadLevel) {
            this.serverNode = serverNode;
            this.currentConnections = currentConnections;
            this.maxConnections = maxConnections;
            this.loadPercentage = loadPercentage;
            this.loadLevel = loadLevel;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public String getServerNode() { return serverNode; }
        public int getCurrentConnections() { return currentConnections; }
        public int getMaxConnections() { return maxConnections; }
        public double getLoadPercentage() { return loadPercentage; }
        public LoadLevel getLoadLevel() { return loadLevel; }
        public long getTimestamp() { return timestamp; }
        
        public int getAvailableSlots() {
            return maxConnections - currentConnections;
        }
        
        public boolean isOverloaded() {
            return loadPercentage >= 90;
        }
    }
    
    /**
     * 负载级别
     */
    public enum LoadLevel {
        LOW,      // < 70%
        MEDIUM,   // 70% - 90%
        HIGH      // >= 90%
    }
}
