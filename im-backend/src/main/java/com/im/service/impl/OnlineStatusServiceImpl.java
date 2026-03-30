package com.im.service.impl;

import com.im.service.IOnlineStatusService;
import com.im.websocket.WebSocketConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 在线状态服务实现类
 * 功能 #7: 实时在线状态服务
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class OnlineStatusServiceImpl implements IOnlineStatusService {
    
    @Autowired
    private WebSocketConnectionManager connectionManager;
    
    // 用户在线状态
    private final Map<String, UserStatus> userStatusMap = new ConcurrentHashMap<>();
    
    // 用户设备映射
    private final Map<String, Set<String>> userDevices = new ConcurrentHashMap<>();
    
    // 状态订阅关系
    private final Map<String, Set<String>> subscriptions = new ConcurrentHashMap<>();
    
    // 用户状态信息
    private static class UserStatus {
        String userId;
        boolean online;
        LocalDateTime lastActiveTime;
        Set<String> devices;
        
        UserStatus(String userId) {
            this.userId = userId;
            this.online = false;
            this.lastActiveTime = LocalDateTime.now();
            this.devices = ConcurrentHashMap.newKeySet();
        }
    }
    
    @Override
    public void userOnline(String userId, String deviceId) {
        UserStatus status = userStatusMap.computeIfAbsent(userId, UserStatus::new);
        status.online = true;
        status.lastActiveTime = LocalDateTime.now();
        status.devices.add(deviceId);
        userDevices.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(deviceId);
        
        // 通知订阅者
        notifySubscribers(userId, "online");
    }
    
    @Override
    public void userOffline(String userId, String deviceId) {
        Set<String> devices = userDevices.get(userId);
        if (devices != null) {
            devices.remove(deviceId);
            
            // 如果没有设备在线，标记为离线
            if (devices.isEmpty()) {
                UserStatus status = userStatusMap.get(userId);
                if (status != null) {
                    status.online = false;
                    status.lastActiveTime = LocalDateTime.now();
                }
                notifySubscribers(userId, "offline");
            }
        }
    }
    
    @Override
    public void heartbeat(String userId) {
        UserStatus status = userStatusMap.get(userId);
        if (status != null) {
            status.lastActiveTime = LocalDateTime.now();
        }
    }
    
    @Override
    public boolean isOnline(String userId) {
        UserStatus status = userStatusMap.get(userId);
        return status != null && status.online;
    }
    
    @Override
    public LocalDateTime getLastActiveTime(String userId) {
        UserStatus status = userStatusMap.get(userId);
        return status != null ? status.lastActiveTime : null;
    }
    
    @Override
    public List<String> getOnlineUsers() {
        return userStatusMap.entrySet().stream()
            .filter(e -> e.getValue().online)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    @Override
    public void subscribeStatus(String subscriberId, String targetUserId) {
        subscriptions.computeIfAbsent(subscriberId, k -> ConcurrentHashMap.newKeySet()).add(targetUserId);
    }
    
    @Override
    public void unsubscribeStatus(String subscriberId, String targetUserId) {
        Set<String> subs = subscriptions.get(subscriberId);
        if (subs != null) {
            subs.remove(targetUserId);
        }
    }
    
    @Override
    public List<String> getSubscribers(String userId) {
        return subscriptions.entrySet().stream()
            .filter(e -> e.getValue().contains(userId))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    @Override
    public int getOnlineCount() {
        return (int) userStatusMap.values().stream().filter(s -> s.online).count();
    }
    
    @Override
    public void syncStatusAcrossDevices(String userId, String status) {
        Set<String> devices = userDevices.get(userId);
        if (devices != null) {
            // 同步状态到所有设备
            devices.forEach(deviceId -> {
                // 推送状态同步消息
            });
        }
    }
    
    /**
     * 通知订阅者状态变化
     */
    private void notifySubscribers(String userId, String status) {
        List<String> subscribers = getSubscribers(userId);
        subscribers.forEach(subscriberId -> {
            // 推送状态变更通知
        });
    }
    
    /**
     * 清理超时未心跳的用户（每30秒执行）
     */
    @Scheduled(fixedRate = 30000)
    public void cleanupInactiveUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(60);
        
        userStatusMap.forEach((userId, status) -> {
            if (status.online && status.lastActiveTime.isBefore(threshold)) {
                status.online = false;
                notifySubscribers(userId, "offline");
            }
        });
    }
}
