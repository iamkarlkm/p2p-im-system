package com.im.server.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 在线状态服务 - 跟踪用户在线状态
 */
@Service
public class PresenceService {
    
    // 存储在线用户ID
    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();
    
    // 用户最后一次活跃时间
    private final java.util.Map<Long, Long> lastActiveTime = new ConcurrentHashMap<>();
    
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    public PresenceService() {
        // 定期清理不活跃的用户（超过5分钟无活动）
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            long threshold = now - (5 * 60 * 1000); // 5分钟
            
            lastActiveTime.entrySet().removeIf(entry -> entry.getValue() < threshold);
            onlineUsers.removeIf(userId -> !lastActiveTime.containsKey(userId));
        }, 5, 5, TimeUnit.MINUTES);
    }
    
    /**
     * 用户上线
     */
    public void userOnline(Long userId) {
        onlineUsers.add(userId);
        lastActiveTime.put(userId, System.currentTimeMillis());
    }
    
    /**
     * 用户离线
     */
    public void userOffline(Long userId) {
        onlineUsers.remove(userId);
        lastActiveTime.remove(userId);
    }
    
    /**
     * 更新用户活跃时间
     */
    public void updateActivity(Long userId) {
        if (onlineUsers.contains(userId)) {
            lastActiveTime.put(userId, System.currentTimeMillis());
        }
    }
    
    /**
     * 检查用户是否在线
     */
    public boolean isOnline(Long userId) {
        return onlineUsers.contains(userId);
    }
    
    /**
     * 获取所有在线用户
     */
    public Set<Long> getOnlineUsers() {
        return new java.util.HashSet<>(onlineUsers);
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineCount() {
        return onlineUsers.size();
    }
    
    /**
     * 批量检查用户是否在线
     */
    public java.util.Map<Long, Boolean> checkOnlineStatus(java.util.List<Long> userIds) {
        return userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        this::isOnline
                ));
    }
    
    /**
     * 获取用户最后活跃时间
     */
    public Long getLastActiveTime(Long userId) {
        return lastActiveTime.get(userId);
    }
    
    /**
     * 清理资源
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
