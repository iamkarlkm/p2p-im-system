package com.im.service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 在线状态服务接口
 * 功能 #7: 实时在线状态服务
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface IOnlineStatusService {
    
    /**
     * 用户上线
     */
    void userOnline(String userId, String deviceId);
    
    /**
     * 用户下线
     */
    void userOffline(String userId, String deviceId);
    
    /**
     * 心跳更新
     */
    void heartbeat(String userId);
    
    /**
     * 检查用户是否在线
     */
    boolean isOnline(String userId);
    
    /**
     * 获取用户最后活跃时间
     */
    LocalDateTime getLastActiveTime(String userId);
    
    /**
     * 获取在线用户列表
     */
    List<String> getOnlineUsers();
    
    /**
     * 订阅用户状态
     */
    void subscribeStatus(String subscriberId, String targetUserId);
    
    /**
     * 取消订阅
     */
    void unsubscribeStatus(String subscriberId, String targetUserId);
    
    /**
     * 获取订阅者列表
     */
    List<String> getSubscribers(String userId);
    
    /**
     * 获取在线用户数量
     */
    int getOnlineCount();
    
    /**
     * 多端同步状态
     */
    void syncStatusAcrossDevices(String userId, String status);
}
