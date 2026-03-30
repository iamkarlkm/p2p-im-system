package com.im.service;

import java.util.Map;

/**
 * 推送通知服务接口
 * 功能 #8: 消息推送通知系统
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface IPushNotificationService {
    
    /**
     * 推送消息到设备
     */
    boolean pushToDevice(String userId, String deviceToken, String title, String content, Map<String, Object> extras);
    
    /**
     * 推送消息到所有设备
     */
    boolean pushToAllDevices(String userId, String title, String content, Map<String, Object> extras);
    
    /**
     * 注册设备令牌
     */
    boolean registerDeviceToken(String userId, String deviceType, String deviceToken);
    
    /**
     * 注销设备令牌
     */
    boolean unregisterDeviceToken(String userId, String deviceToken);
    
    /**
     * 设置推送频率限制
     */
    void setPushRateLimit(String userId, int maxPerMinute);
    
    /**
     * 检查推送频率
     */
    boolean checkPushRateLimit(String userId);
    
    /**
     * 获取推送统计
     */
    Map<String, Object> getPushStats(String userId);
    
    /**
     * 发送批量推送
     */
    int pushBatch(java.util.List<String> userIds, String title, String content);
}
