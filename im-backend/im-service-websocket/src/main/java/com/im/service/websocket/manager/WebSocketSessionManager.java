package com.im.service.websocket.manager;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * WebSocket 会话管理器
 * 
 * 核心功能:
 * 1. 会话注册与注销 (支持多设备登录)
 * 2. 会话查询 (按用户ID、设备ID、会话ID)
3. 心跳检测与超时处理
 * 4. 断线重连支持 (会话缓存与恢复)
 * 5. 会话统计与监控
 * 
 * 会话结构:
 * - userId -> {deviceId -> WebSocketSession}
 * - sessionId -> UserSessionInfo
 * 
 * 多设备登录支持:
 * - 同一用户可在多个设备同时在线
 * - 每个设备有独立的会话
 * - 消息推送到所有在线设备
 * 
 * @author im-modular
 * @since 1.0.0
 */
@Slf4j
@Component
public class WebSocketSessionManager {

    // ==================== 配置常量 ====================
    
    /** 心跳超时时间 (毫秒) - 2分钟 */
    private static final long HEARTBEAT_TIMEOUT_MS = 120000;
    
    /** 会话清理间隔 (毫秒) - 5分钟 */
    private static final long CLEANUP_INTERVAL_MS = 300000;
    
    /** 最大会话缓存时间 (毫秒) - 10分钟 */
    private static final long MAX_SESSION_CACHE_MS = 600000;

    // ==================== 会话存储 ====================
    
    /**
     * 用户会话映射
     * userId -> {deviceId -> WebSocketSession}
     */
    private final Map<Long, Map<String, WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    
    /**
     * 会话信息映射
     * sessionId -> UserSessionInfo
     */
    private final Map<String, UserSessionInfo> sessionInfoMap = new ConcurrentHashMap<>();
    
    /**
     * 断线会话缓存 (用于断线重连)
     * sessionId -> CachedSessionInfo
     */
    private final Map<String, CachedSessionInfo> cachedSessions = new ConcurrentHashMap<>();

    // ==================== 会话注册与注销 ====================

    /**
     * 注册会话
     * 
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param session  WebSocket会话
     */
    public void registerSession(Long userId, String deviceId, WebSocketSession session) {
        if (userId == null || deviceId == null || session == null) {
            log.warn("注册会话失败: 参数不能为空");
            return;
        }
        
        String sessionId = session.getId();
        
        // 检查是否是断线重连
        CachedSessionInfo cached = cachedSessions.remove(sessionId);
        if (cached != null) {
            log.info("检测到断线重连: userId={}, deviceId={}, sessionId={}", 
                    userId, deviceId, sessionId);
        }
        
        // 检查是否已存在该设备的会话 (同一设备重复登录)
        Map<String, WebSocketSession> devices = userSessions.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        WebSocketSession oldSession = devices.get(deviceId);
        
        if (oldSession != null && !oldSession.getId().equals(sessionId)) {
            // 关闭旧会话 (踢下线)
            try {
                log.info("同一设备重复登录，关闭旧会话: userId={}, deviceId={}", userId, deviceId);
                oldSession.close();
                sessionInfoMap.remove(oldSession.getId());
            } catch (IOException e) {
                log.error("关闭旧会话失败", e);
            }
        }
        
        // 注册新会话
        devices.put(deviceId, session);
        
        // 创建会话信息
        UserSessionInfo sessionInfo = new UserSessionInfo();
        sessionInfo.setSessionId(sessionId);
        sessionInfo.setUserId(userId);
        sessionInfo.setDeviceId(deviceId);
        sessionInfo.setSession(session);
        sessionInfo.setConnectTime(System.currentTimeMillis());
        sessionInfo.setLastActivityTime(System.currentTimeMillis());
        sessionInfo.setOnline(true);
        
        sessionInfoMap.put(sessionId, sessionInfo);
        
        log.info("会话注册成功: userId={}, deviceId={}, sessionId={}, totalDevices={}", 
                userId, deviceId, sessionId, devices.size());
    }

    /**
     * 注销会话
     * 
     * @param userId    用户ID
     * @param deviceId  设备ID
     * @param sessionId 会话ID
     */
    public void unregisterSession(Long userId, String deviceId, String sessionId) {
        if (userId == null || sessionId == null) {
            return;
        }
        
        // 获取会话信息
        UserSessionInfo sessionInfo = sessionInfoMap.get(sessionId);
        
        // 缓存会话信息 (用于断线重连)
        if (sessionInfo != null) {
            cacheSession(sessionInfo);
        }
        
        // 从用户会话中移除
        Map<String, WebSocketSession> devices = userSessions.get(userId);
        if (devices != null) {
            WebSocketSession session = devices.get(deviceId);
            if (session != null && session.getId().equals(sessionId)) {
                devices.remove(deviceId);
                
                // 如果该用户没有活跃设备，移除用户条目
                if (devices.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }
        
        // 从会话信息映射中移除
        sessionInfoMap.remove(sessionId);
        
        log.info("会话注销: userId={}, deviceId={}, sessionId={}", userId, deviceId, sessionId);
    }

    /**
     * 缓存会话信息 (用于断线重连)
     */
    private void cacheSession(UserSessionInfo sessionInfo) {
        CachedSessionInfo cached = new CachedSessionInfo();
        cached.setUserId(sessionInfo.getUserId());
        cached.setDeviceId(sessionInfo.getDeviceId());
        cached.setSessionId(sessionInfo.getSessionId());
        cached.setDisconnectTime(System.currentTimeMillis());
        cached.setLastActivityTime(sessionInfo.getLastActivityTime());
        
        cachedSessions.put(sessionInfo.getSessionId(), cached);
        
        log.debug("会话已缓存: sessionId={}", sessionInfo.getSessionId());
    }

    // ==================== 会话查询 ====================

    /**
     * 获取用户的所有会话
     * 
     * @param userId 用户ID
     * @return 会话集合
     */
    public Set<WebSocketSession> getUserSessions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        
        Map<String, WebSocketSession> devices = userSessions.get(userId);
        if (devices == null || devices.isEmpty()) {
            return Collections.emptySet();
        }
        
        return new CopyOnWriteArraySet<>(devices.values());
    }

    /**
     * 获取用户的特定设备会话
     * 
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return WebSocket会话
     */
    public WebSocketSession getDeviceSession(Long userId, String deviceId) {
        if (userId == null || deviceId == null) {
            return null;
        }
        
        Map<String, WebSocketSession> devices = userSessions.get(userId);
        if (devices == null) {
            return null;
        }
        
        return devices.get(deviceId);
    }

    /**
     * 获取会话信息
     * 
     * @param sessionId 会话ID
     * @return 会话信息
     */
    public UserSessionInfo getSessionInfo(String sessionId) {
        return sessionInfoMap.get(sessionId);
    }

    /**
     * 获取用户的会话数量
     * 
     * @param userId 用户ID
     * @return 会话数量
     */
    public int getUserSessionCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        Map<String, WebSocketSession> devices = userSessions.get(userId);
        return devices != null ? devices.size() : 0;
    }

    /**
     * 检查用户是否有活跃会话
     * 
     * @param userId 用户ID
     * @return 是否有活跃会话
     */
    public boolean hasActiveSession(Long userId) {
        return getUserSessionCount(userId) > 0;
    }

    /**
     * 获取所有在线用户ID
     * 
     * @return 在线用户ID集合
     */
    public Set<Long> getAllOnlineUsers() {
        return new HashSet<>(userSessions.keySet());
    }

    /**
     * 获取在线用户数量
     * 
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }

    /**
     * 获取总会话数量
     * 
     * @return 会话数量
     */
    public int getTotalSessionCount() {
        return sessionInfoMap.size();
    }

    // ==================== 会话状态更新 ====================

    /**
     * 更新最后活动时间
     * 
     * @param userId    用户ID
     * @param sessionId 会话ID
     */
    public void updateLastActivityTime(Long userId, String sessionId) {
        UserSessionInfo sessionInfo = sessionInfoMap.get(sessionId);
        if (sessionInfo != null) {
            sessionInfo.setLastActivityTime(System.currentTimeMillis());
        }
    }

    /**
     * 检查会话是否有效
     * 
     * @param sessionId 会话ID
     * @return 是否有效
     */
    public boolean isSessionValid(String sessionId) {
        UserSessionInfo sessionInfo = sessionInfoMap.get(sessionId);
        if (sessionInfo == null) {
            return false;
        }
        
        WebSocketSession session = sessionInfo.getSession();
        return session != null && session.isOpen();
    }

    /**
     * 检查断线会话是否可以重连
     * 
     * @param sessionId 会话ID
     * @return 是否可以重连
     */
    public boolean canReconnect(String sessionId) {
        CachedSessionInfo cached = cachedSessions.get(sessionId);
        if (cached == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - cached.getDisconnectTime()) < MAX_SESSION_CACHE_MS;
    }

    /**
     * 获取断线会话信息
     * 
     * @param sessionId 会话ID
     * @return 断线会话信息
     */
    public CachedSessionInfo getCachedSession(String sessionId) {
        return cachedSessions.get(sessionId);
    }

    // ==================== 心跳检测 ====================

    /**
     * 检查会话心跳超时
     * 
     * @param sessionId 会话ID
     * @return 是否超时
     */
    public boolean isHeartbeatTimeout(String sessionId) {
        UserSessionInfo sessionInfo = sessionInfoMap.get(sessionId);
        if (sessionInfo == null) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - sessionInfo.getLastActivityTime()) > HEARTBEAT_TIMEOUT_MS;
    }

    /**
     * 获取超时会话列表
     * 
     * @return 超时会话ID列表
     */
    public List<String> getTimeoutSessions() {
        long currentTime = System.currentTimeMillis();
        
        return sessionInfoMap.entrySet().stream()
                .filter(entry -> {
                    UserSessionInfo info = entry.getValue();
                    return (currentTime - info.getLastActivityTime()) > HEARTBEAT_TIMEOUT_MS;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // ==================== 定时清理任务 ====================

    /**
     * 定时清理过期会话
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = CLEANUP_INTERVAL_MS)
    public void cleanupExpiredSessions() {
        log.debug("开始清理过期会话...");
        
        long currentTime = System.currentTimeMillis();
        int closedCount = 0;
        int removedCount = 0;
        
        // 1. 关闭超时会话
        List<String> timeoutSessions = getTimeoutSessions();
        for (String sessionId : timeoutSessions) {
            UserSessionInfo sessionInfo = sessionInfoMap.get(sessionId);
            if (sessionInfo != null) {
                WebSocketSession session = sessionInfo.getSession();
                if (session != null && session.isOpen()) {
                    try {
                        session.close();
                        closedCount++;
                    } catch (IOException e) {
                        log.error("关闭超时会话失败: sessionId={}", sessionId, e);
                    }
                }
                
                // 注销会话
                unregisterSession(sessionInfo.getUserId(), sessionInfo.getDeviceId(), sessionId);
                removedCount++;
            }
        }
        
        // 2. 清理过期缓存
        Iterator<Map.Entry<String, CachedSessionInfo>> cacheIterator = cachedSessions.entrySet().iterator();
        while (cacheIterator.hasNext()) {
            Map.Entry<String, CachedSessionInfo> entry = cacheIterator.next();
            CachedSessionInfo cached = entry.getValue();
            if ((currentTime - cached.getDisconnectTime()) > MAX_SESSION_CACHE_MS) {
                cacheIterator.remove();
            }
        }
        
        log.info("会话清理完成: closed={}, removed={}, remainingSessions={}, remainingCached={}", 
                closedCount, removedCount, sessionInfoMap.size(), cachedSessions.size());
    }

    // ==================== 会话统计 ====================

    /**
     * 获取会话统计信息
     * 
     * @return 统计信息
     */
    public SessionStats getSessionStats() {
        SessionStats stats = new SessionStats();
        stats.setOnlineUsers(getOnlineUserCount());
        stats.setTotalSessions(getTotalSessionCount());
        stats.setCachedSessions(cachedSessions.size());
        
        // 计算平均会话数
        if (stats.getOnlineUsers() > 0) {
            stats.setAvgSessionsPerUser((double) stats.getTotalSessions() / stats.getOnlineUsers());
        }
        
        // 统计设备类型分布 (简化实现)
        Map<String, Integer> deviceTypeStats = new HashMap<>();
        for (UserSessionInfo info : sessionInfoMap.values()) {
            String deviceType = detectDeviceType(info.getDeviceId());
            deviceTypeStats.merge(deviceType, 1, Integer::sum);
        }
        stats.setDeviceTypeDistribution(deviceTypeStats);
        
        return stats;
    }

    /**
     * 检测设备类型 (简化实现)
     */
    private String detectDeviceType(String deviceId) {
        if (deviceId == null) {
            return "unknown";
        }
        
        String lower = deviceId.toLowerCase();
        if (lower.contains("web")) {
            return "web";
        } else if (lower.contains("ios") || lower.contains("iphone") || lower.contains("ipad")) {
            return "ios";
        } else if (lower.contains("android")) {
            return "android";
        } else if (lower.contains("desktop") || lower.contains("pc")) {
            return "desktop";
        }
        return "other";
    }

    // ==================== 内部类 ====================

    /**
     * 用户会话信息
     */
    @Data
    public static class UserSessionInfo {
        private String sessionId;
        private Long userId;
        private String deviceId;
        private WebSocketSession session;
        private long connectTime;
        private long lastActivityTime;
        private boolean online;
    }

    /**
     * 缓存的会话信息 (用于断线重连)
     */
    @Data
    public static class CachedSessionInfo {
        private String sessionId;
        private Long userId;
        private String deviceId;
        private long disconnectTime;
        private long lastActivityTime;
    }

    /**
     * 会话统计信息
     */
    @Data
    public static class SessionStats {
        private int onlineUsers;
        private int totalSessions;
        private int cachedSessions;
        private double avgSessionsPerUser;
        private Map<String, Integer> deviceTypeDistribution;
    }
}
