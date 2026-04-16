package com.im.service.websocket.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 在线状态管理服务
 * 
 * 核心功能:
 * 1. 用户在线/离线状态维护
 * 2. 好友在线状态查询
 * 3. 最后在线时间记录
 * 4. 多端状态同步
 * 5. 在线状态变更通知
 * 
 * 存储策略:
 * - Redis: 持久化存储用户在线状态和最后在线时间
 * - 本地缓存: 缓存用户设备在线状态，减少Redis访问
 * 
 * Redis Key 设计:
 * - im:user:online:{userId} -> 在线状态信息 (Hash)
 * - im:user:last_seen:{userId} -> 最后在线时间 (String)
 * - im:user:friends_online:{userId} -> 好友在线状态 (Set)
 * 
 * @author im-modular
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineStatusService {

    private final RedisTemplate<String, Object> redisTemplate;

    // ==================== 配置常量 ====================
    
    /** Redis Key 前缀 */
    private static final String KEY_PREFIX = "im:user:online:";
    private static final String LAST_SEEN_PREFIX = "im:user:last_seen:";
    private static final String FRIENDS_ONLINE_PREFIX = "im:user:friends_online:";
    
    /** 在线状态过期时间 (分钟) */
    private static final long ONLINE_STATUS_EXPIRE_MINUTES = 10;
    
    /** 最后在线时间缓存时间 (天) */
    private static final long LAST_SEEN_EXPIRE_DAYS = 30;
    
    /** 心跳超时时间 (毫秒) */
    private static final long HEARTBEAT_TIMEOUT_MS = 120000; // 2分钟

    // ==================== 本地缓存 ====================
    
    /**
     * 用户设备在线状态缓存
     * userId -> (deviceId -> DeviceStatus)
     */
    private final Map<Long, Map<String, DeviceStatus>> deviceStatusCache = new ConcurrentHashMap<>();

    // ==================== 在线状态管理 ====================

    /**
     * 用户上线
     * 
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    public void userOnline(Long userId, String deviceId) {
        if (userId == null || deviceId == null) {
            return;
        }
        
        String key = KEY_PREFIX + userId;
        String lastSeenKey = LAST_SEEN_PREFIX + userId;
        long currentTime = System.currentTimeMillis();
        
        // 更新 Redis 在线状态
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("userId", userId.toString());
        statusMap.put("deviceId", deviceId);
        statusMap.put("status", "online");
        statusMap.put("lastHeartbeat", currentTime);
        statusMap.put("onlineTime", currentTime);
        
        redisTemplate.opsForHash().putAll(key, statusMap);
        redisTemplate.expire(key, Duration.ofMinutes(ONLINE_STATUS_EXPIRE_MINUTES));
        
        // 更新最后在线时间
        redisTemplate.opsForValue().set(lastSeenKey, currentTime, 
                Duration.ofDays(LAST_SEEN_EXPIRE_DAYS));
        
        // 更新本地缓存
        DeviceStatus deviceStatus = new DeviceStatus();
        deviceStatus.setDeviceId(deviceId);
        deviceStatus.setStatus("online");
        deviceStatus.setLastHeartbeat(currentTime);
        deviceStatus.setOnlineTime(currentTime);
        
        deviceStatusCache.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                        .put(deviceId, deviceStatus);
        
        log.info("用户上线: userId={}, deviceId={}", userId, deviceId);
        
        // 通知好友状态变更 (异步)
        notifyFriendsStatusChange(userId, "online");
    }

    /**
     * 用户下线
     * 
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    public void userOffline(Long userId, String deviceId) {
        if (userId == null || deviceId == null) {
            return;
        }
        
        // 更新本地缓存
        Map<String, DeviceStatus> devices = deviceStatusCache.get(userId);
        if (devices != null) {
            devices.remove(deviceId);
            
            // 如果该用户没有活跃设备，清除Redis状态
            if (devices.isEmpty()) {
                deviceStatusCache.remove(userId);
                
                String key = KEY_PREFIX + userId;
                String lastSeenKey = LAST_SEEN_PREFIX + userId;
                long currentTime = System.currentTimeMillis();
                
                // 更新最后在线时间
                redisTemplate.opsForValue().set(lastSeenKey, currentTime, 
                        Duration.ofDays(LAST_SEEN_EXPIRE_DAYS));
                
                // 删除在线状态
                redisTemplate.delete(key);
                
                log.info("用户完全离线: userId={}, deviceId={}", userId, deviceId);
                
                // 通知好友状态变更
                notifyFriendsStatusChange(userId, "offline");
            } else {
                log.info("用户部分离线: userId={}, deviceId={}, remainingDevices={}", 
                        userId, deviceId, devices.size());
            }
        }
    }

    /**
     * 更新心跳时间
     * 
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    public void updateHeartbeat(Long userId, String deviceId) {
        if (userId == null || deviceId == null) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        String key = KEY_PREFIX + userId;
        
        // 更新 Redis
        redisTemplate.opsForHash().put(key, "lastHeartbeat", currentTime);
        
        // 更新本地缓存
        Map<String, DeviceStatus> devices = deviceStatusCache.get(userId);
        if (devices != null) {
            DeviceStatus device = devices.get(deviceId);
            if (device != null) {
                device.setLastHeartbeat(currentTime);
            }
        }
    }

    /**
     * 更新在线状态 (用户主动设置)
     * 
     * @param userId 用户ID
     * @param status 状态: online, away, busy, invisible
     */
    public void updatePresence(Long userId, String status) {
        if (userId == null || status == null) {
            return;
        }
        
        String key = KEY_PREFIX + userId;
        long currentTime = System.currentTimeMillis();
        
        // 更新 Redis
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("userId", userId.toString());
        statusMap.put("status", status);
        statusMap.put("lastPresenceUpdate", currentTime);
        
        redisTemplate.opsForHash().putAll(key, statusMap);
        redisTemplate.expire(key, Duration.ofMinutes(ONLINE_STATUS_EXPIRE_MINUTES));
        
        // 更新本地缓存
        Map<String, DeviceStatus> devices = deviceStatusCache.get(userId);
        if (devices != null) {
            for (DeviceStatus device : devices.values()) {
                device.setStatus(status);
                device.setLastPresenceUpdate(currentTime);
            }
        }
        
        log.debug("用户更新在线状态: userId={}, status={}", userId, status);
        
        // 通知好友状态变更
        notifyFriendsStatusChange(userId, status);
    }

    // ==================== 查询方法 ====================

    /**
     * 检查用户是否在线
     * 
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        if (userId == null) {
            return false;
        }
        
        // 先检查本地缓存
        Map<String, DeviceStatus> devices = deviceStatusCache.get(userId);
        if (devices != null && !devices.isEmpty()) {
            // 检查是否有活跃设备
            long currentTime = System.currentTimeMillis();
            for (DeviceStatus device : devices.values()) {
                if (currentTime - device.getLastHeartbeat() < HEARTBEAT_TIMEOUT_MS) {
                    return true;
                }
            }
        }
        
        // 检查 Redis
        String key = KEY_PREFIX + userId;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 获取用户在线状态
     * 
     * @param userId 用户ID
     * @return 在线状态信息
     */
    public UserOnlineStatus getUserOnlineStatus(Long userId) {
        if (userId == null) {
            return null;
        }
        
        boolean online = isUserOnline(userId);
        UserOnlineStatus status = new UserOnlineStatus();
        status.setUserId(userId);
        status.setOnline(online);
        
        if (online) {
            // 从 Redis 获取详细信息
            String key = KEY_PREFIX + userId;
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            
            if (entries != null) {
                status.setStatus((String) entries.get("status"));
                
                Object lastHeartbeat = entries.get("lastHeartbeat");
                if (lastHeartbeat != null) {
                    status.setLastHeartbeat(parseLong(lastHeartbeat));
                }
                
                Object onlineTime = entries.get("onlineTime");
                if (onlineTime != null) {
                    status.setOnlineTime(parseLong(onlineTime));
                }
            }
            
            // 从本地缓存获取设备信息
            Map<String, DeviceStatus> devices = deviceStatusCache.get(userId);
            if (devices != null) {
                status.setDeviceCount(devices.size());
                status.setDevices(new ArrayList<>(devices.values()));
            }
        } else {
            // 获取最后在线时间
            status.setStatus("offline");
            status.setLastSeen(getLastSeenTime(userId));
        }
        
        return status;
    }

    /**
     * 批量获取用户在线状态
     * 
     * @param userIds 用户ID列表
     * @return 用户在线状态列表
     */
    public List<UserOnlineStatus> getBatchOnlineStatus(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        return userIds.stream()
                .map(this::getUserOnlineStatus)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 获取最后在线时间
     * 
     * @param userId 用户ID
     * @return 最后在线时间
     */
    public LocalDateTime getLastSeenTime(Long userId) {
        if (userId == null) {
            return null;
        }
        
        String lastSeenKey = LAST_SEEN_PREFIX + userId;
        Object value = redisTemplate.opsForValue().get(lastSeenKey);
        
        if (value != null) {
            long timestamp = parseLong(value);
            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), 
                    ZoneId.systemDefault());
        }
        
        return null;
    }

    /**
     * 获取所有在线用户ID
     * 
     * @return 在线用户ID集合
     */
    public Set<Long> getAllOnlineUsers() {
        // 从本地缓存获取
        return deviceStatusCache.keySet();
    }

    /**
     * 获取在线用户数量
     * 
     * @return 在线用户数量
     */
    public long getOnlineUserCount() {
        return deviceStatusCache.size();
    }

    // ==================== 好友在线状态 ====================

    /**
     * 获取好友在线状态
     * 
     * @param userId    用户ID
     * @param friendIds 好友ID列表
     * @return 好友在线状态映射
     */
    public Map<Long, UserOnlineStatus> getFriendsOnlineStatus(Long userId, List<Long> friendIds) {
        if (userId == null || friendIds == null || friendIds.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<Long, UserOnlineStatus> result = new HashMap<>();
        for (Long friendId : friendIds) {
            UserOnlineStatus status = getUserOnlineStatus(friendId);
            if (status != null) {
                result.put(friendId, status);
            }
        }
        
        return result;
    }

    /**
     * 订阅好友在线状态变更
     * 
     * @param userId   用户ID
     * @param friendId 好友ID
     */
    public void subscribeFriendStatus(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            return;
        }
        
        String key = FRIENDS_ONLINE_PREFIX + userId;
        redisTemplate.opsForSet().add(key, friendId.toString());
        redisTemplate.expire(key, Duration.ofDays(LAST_SEEN_EXPIRE_DAYS));
    }

    /**
     * 取消订阅好友在线状态
     * 
     * @param userId   用户ID
     * @param friendId 好友ID
     */
    public void unsubscribeFriendStatus(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            return;
        }
        
        String key = FRIENDS_ONLINE_PREFIX + userId;
        redisTemplate.opsForSet().remove(key, friendId.toString());
    }

    // ==================== 状态变更通知 ====================

    /**
     * 通知好友状态变更
     * 
     * @param userId 用户ID
     * @param status 新状态
     */
    private void notifyFriendsStatusChange(Long userId, String status) {
        // 这里应该调用消息推送服务，通知该用户的好友
        // 简化实现：记录日志
        log.debug("通知好友状态变更: userId={}, status={}", userId, status);
    }

    // ==================== 工具方法 ====================

    private long parseLong(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ==================== 内部类 ====================

    /**
     * 设备在线状态
     */
    @Data
    public static class DeviceStatus {
        private String deviceId;
        private String status;  // online, away, busy
        private long lastHeartbeat;
        private long onlineTime;
        private long lastPresenceUpdate;
    }

    /**
     * 用户在线状态
     */
    @Data
    public static class UserOnlineStatus {
        private Long userId;
        private boolean online;
        private String status;  // online, away, busy, offline
        private Long lastHeartbeat;
        private Long onlineTime;
        private LocalDateTime lastSeen;
        private int deviceCount;
        private List<DeviceStatus> devices;
    }
}
