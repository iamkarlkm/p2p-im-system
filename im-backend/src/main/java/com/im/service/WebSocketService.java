package com.im.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务 - 管理在线用户会话
 * 功能ID: #3
 * @author developer-agent
 * @since 2026-03-30
 */
@Service
public class WebSocketService {

    // 用户ID -> SessionID 映射 (支持多设备登录)
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> userSessions = new ConcurrentHashMap<>();
    
    // 用户ID -> 最后心跳时间
    private final ConcurrentHashMap<String, Long> userHeartbeats = new ConcurrentHashMap<>();
    
    // 在线用户集合
    private final CopyOnWriteArraySet<String> onlineUsers = new CopyOnWriteArraySet<>();

    /**
     * 用户上线
     */
    public void userOnline(String userId) {
        onlineUsers.add(userId);
        userSessions.putIfAbsent(userId, new CopyOnWriteArraySet<>());
        userHeartbeats.put(userId, System.currentTimeMillis());
    }

    /**
     * 用户下线
     */
    public void userOffline(String userId) {
        onlineUsers.remove(userId);
        userSessions.remove(userId);
        userHeartbeats.remove(userId);
    }

    /**
     * 注册用户会话
     */
    public void registerSession(String userId, String sessionId) {
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
        onlineUsers.add(userId);
    }

    /**
     * 移除用户会话
     */
    public void removeSession(String userId, String sessionId) {
        CopyOnWriteArraySet<String> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                userOffline(userId);
            }
        }
    }

    /**
     * 更新心跳
     */
    public void updateHeartbeat(String userId) {
        userHeartbeats.put(userId, System.currentTimeMillis());
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(String userId) {
        return onlineUsers.contains(userId);
    }

    /**
     * 获取在线用户列表
     */
    public CopyOnWriteArraySet<String> getOnlineUsers() {
        return onlineUsers;
    }

    /**
     * 获取用户会话数
     */
    public int getUserSessionCount(String userId) {
        CopyOnWriteArraySet<String> sessions = userSessions.get(userId);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * 清理超时用户
     */
    public void cleanExpiredUsers(long timeoutMs) {
        long now = System.currentTimeMillis();
        userHeartbeats.forEach((userId, lastHeartbeat) -> {
            if (now - lastHeartbeat > timeoutMs) {
                userOffline(userId);
            }
        });
    }
}
