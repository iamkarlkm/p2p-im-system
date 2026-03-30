package com.im.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.*;

/**
 * WebSocket会话管理器
 * 功能 #2: WebSocket实时推送服务 - 会话管理
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class WebSocketSessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionManager.class);
    
    // 单例实例
    private static volatile WebSocketSessionManager instance;
    
    // 会话存储
    private final ConcurrentHashMap<String, SessionInfo> sessionStore = new ConcurrentHashMap<>();
    
    // 用户到会话映射
    private final ConcurrentHashMap<String, Set<String>> userSessionMap = new ConcurrentHashMap<>();
    
    // 设备类型统计
    private final ConcurrentHashMap<String, AtomicInteger> deviceStats = new ConcurrentHashMap<>();
    
    // 会话超时时间（毫秒）
    private static final long SESSION_TIMEOUT = 300000; // 5分钟
    
    // 清理定时器
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
    
    private WebSocketSessionManager() {
        // 启动清理任务
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredSessions, 60, 60, TimeUnit.SECONDS);
    }
    
    /**
     * 获取单例
     */
    public static WebSocketSessionManager getInstance() {
        if (instance == null) {
            synchronized (WebSocketSessionManager.class) {
                if (instance == null) {
                    instance = new WebSocketSessionManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * 注册会话
     */
    public void registerSession(String sessionId, String userId, String deviceType, WebSocketSession session) {
        SessionInfo info = new SessionInfo(sessionId, userId, deviceType, session);
        sessionStore.put(sessionId, info);
        
        if (userId != null) {
            userSessionMap.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        }
        
        deviceStats.computeIfAbsent(deviceType, k -> new AtomicInteger(0)).incrementAndGet();
        
        logger.info("Session registered: sessionId={}, userId={}, device={}", sessionId, userId, deviceType);
    }
    
    /**
     * 注销会话
     */
    public void unregisterSession(String sessionId) {
        SessionInfo info = sessionStore.remove(sessionId);
        if (info != null) {
            if (info.getUserId() != null) {
                Set<String> sessions = userSessionMap.get(info.getUserId());
                if (sessions != null) {
                    sessions.remove(sessionId);
                    if (sessions.isEmpty()) {
                        userSessionMap.remove(info.getUserId());
                    }
                }
            }
            
            AtomicInteger count = deviceStats.get(info.getDeviceType());
            if (count != null) {
                count.decrementAndGet();
            }
            
            logger.info("Session unregistered: sessionId={}, userId={}", sessionId, info.getUserId());
        }
    }
    
    /**
     * 获取会话信息
     */
    public SessionInfo getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }
    
    /**
     * 获取用户所有会话
     */
    public List<SessionInfo> getUserSessions(String userId) {
        Set<String> sessionIds = userSessionMap.get(userId);
        if (sessionIds == null) return Collections.emptyList();
        
        List<SessionInfo> sessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            SessionInfo info = sessionStore.get(sessionId);
            if (info != null) {
                sessions.add(info);
            }
        }
        return sessions;
    }
    
    /**
     * 获取用户在线状态
     */
    public boolean isUserOnline(String userId) {
        Set<String> sessions = userSessionMap.get(userId);
        return sessions != null && !sessions.isEmpty();
    }
    
    /**
     * 获取用户在线设备数
     */
    public int getUserDeviceCount(String userId) {
        Set<String> sessions = userSessionMap.get(userId);
        return sessions != null ? sessions.size() : 0;
    }
    
    /**
     * 更新心跳时间
     */
    public void updateHeartbeat(String sessionId) {
        SessionInfo info = sessionStore.get(sessionId);
        if (info != null) {
            info.setLastHeartbeat(System.currentTimeMillis());
        }
    }
    
    /**
     * 获取所有在线用户
     */
    public Set<String> getAllOnlineUsers() {
        return new HashSet<>(userSessionMap.keySet());
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessionMap.size();
    }
    
    /**
     * 获取总会话数
     */
    public int getTotalSessionCount() {
        return sessionStore.size();
    }
    
    /**
     * 清理过期会话
     */
    private void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        List<String> expiredSessions = new ArrayList<>();
        
        sessionStore.forEach((sessionId, info) -> {
            if (now - info.getLastHeartbeat() > SESSION_TIMEOUT) {
                expiredSessions.add(sessionId);
            }
        });
        
        for (String sessionId : expiredSessions) {
            unregisterSession(sessionId);
            logger.warn("Expired session cleaned up: {}", sessionId);
        }
    }
    
    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", sessionStore.size());
        stats.put("onlineUsers", userSessionMap.size());
        stats.put("deviceStats", new HashMap<>(deviceStats));
        return stats;
    }
    
    /**
     * 会话信息内部类
     */
    public static class SessionInfo {
        private final String sessionId;
        private final String userId;
        private final String deviceType;
        private final WebSocketSession session;
        private final long createTime;
        private volatile long lastHeartbeat;
        
        public SessionInfo(String sessionId, String userId, String deviceType, WebSocketSession session) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.deviceType = deviceType != null ? deviceType : "unknown";
            this.session = session;
            this.createTime = System.currentTimeMillis();
            this.lastHeartbeat = this.createTime;
        }
        
        public String getSessionId() { return sessionId; }
        public String getUserId() { return userId; }
        public String getDeviceType() { return deviceType; }
        public WebSocketSession getSession() { return session; }
        public long getCreateTime() { return createTime; }
        public long getLastHeartbeat() { return lastHeartbeat; }
        public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
        
        /**
         * 获取在线时长（毫秒）
         */
        public long getOnlineDuration() {
            return System.currentTimeMillis() - createTime;
        }
    }
}
