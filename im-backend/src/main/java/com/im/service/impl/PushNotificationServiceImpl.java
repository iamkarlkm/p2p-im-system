package com.im.service.impl;

import com.im.service.IPushNotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 推送通知服务实现类
 * 功能 #8: 消息推送通知系统
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class PushNotificationServiceImpl implements IPushNotificationService {
    
    // 用户设备令牌
    private final Map<String, Map<String, String>> userDeviceTokens = new ConcurrentHashMap<>();
    
    // 推送统计
    private final Map<String, PushStats> userPushStats = new ConcurrentHashMap<>();
    
    // 频率限制
    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();
    
    private static class PushStats {
        AtomicInteger totalPushed = new AtomicInteger(0);
        AtomicInteger totalDelivered = new AtomicInteger(0);
        AtomicInteger totalClicked = new AtomicInteger(0);
        LocalDateTime lastPushTime;
    }
    
    private static class RateLimiter {
        int maxPerMinute;
        Queue<Long> pushTimestamps = new LinkedList<>();
        
        RateLimiter(int maxPerMinute) {
            this.maxPerMinute = maxPerMinute;
        }
        
        synchronized boolean allow() {
            long now = System.currentTimeMillis();
            long oneMinuteAgo = now - 60000;
            
            // 清理过期时间戳
            while (!pushTimestamps.isEmpty() && pushTimestamps.peek() < oneMinuteAgo) {
                pushTimestamps.poll();
            }
            
            if (pushTimestamps.size() < maxPerMinute) {
                pushTimestamps.offer(now);
                return true;
            }
            return false;
        }
    }
    
    @Override
    public boolean pushToDevice(String userId, String deviceToken, String title, String content, Map<String, Object> extras) {
        // 检查频率限制
        if (!checkPushRateLimit(userId)) {
            return false;
        }
        
        // 模拟推送
        System.out.println("[PUSH] To: " + userId + ", Title: " + title + ", Content: " + content);
        
        // 更新统计
        PushStats stats = userPushStats.computeIfAbsent(userId, k -> new PushStats());
        stats.totalPushed.incrementAndGet();
        stats.lastPushTime = LocalDateTime.now();
        
        return true;
    }
    
    @Override
    public boolean pushToAllDevices(String userId, String title, String content, Map<String, Object> extras) {
        Map<String, String> devices = userDeviceTokens.get(userId);
        if (devices == null || devices.isEmpty()) {
            return false;
        }
        
        devices.forEach((deviceType, token) -> {
            pushToDevice(userId, token, title, content, extras);
        });
        
        return true;
    }
    
    @Override
    public boolean registerDeviceToken(String userId, String deviceType, String deviceToken) {
        userDeviceTokens.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
            .put(deviceType, deviceToken);
        return true;
    }
    
    @Override
    public boolean unregisterDeviceToken(String userId, String deviceToken) {
        Map<String, String> devices = userDeviceTokens.get(userId);
        if (devices != null) {
            devices.values().remove(deviceToken);
        }
        return true;
    }
    
    @Override
    public void setPushRateLimit(String userId, int maxPerMinute) {
        rateLimiters.put(userId, new RateLimiter(maxPerMinute));
    }
    
    @Override
    public boolean checkPushRateLimit(String userId) {
        RateLimiter limiter = rateLimiters.get(userId);
        if (limiter == null) {
            return true; // 无限制
        }
        return limiter.allow();
    }
    
    @Override
    public Map<String, Object> getPushStats(String userId) {
        PushStats stats = userPushStats.get(userId);
        if (stats == null) {
            return Map.of("totalPushed", 0);
        }
        
        return Map.of(
            "totalPushed", stats.totalPushed.get(),
            "totalDelivered", stats.totalDelivered.get(),
            "totalClicked", stats.totalClicked.get(),
            "lastPushTime", stats.lastPushTime
        );
    }
    
    @Override
    public int pushBatch(List<String> userIds, String title, String content) {
        int successCount = 0;
        for (String userId : userIds) {
            if (pushToAllDevices(userId, title, content, null)) {
                successCount++;
            }
        }
        return successCount;
    }
}
