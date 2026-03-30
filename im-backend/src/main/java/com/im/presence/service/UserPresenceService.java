package com.im.presence.service;

import com.im.presence.dto.BatchSubscribeRequest;
import com.im.presence.dto.PresenceResponse;
import com.im.presence.dto.PresenceUpdateRequest;

import java.util.List;
import java.util.Map;

/**
 * 用户在线状态服务接口
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface UserPresenceService {
    
    /**
     * 更新用户在线状态
     * 
     * @param request 状态更新请求
     * @return 更新后的状态
     */
    PresenceResponse updatePresence(PresenceUpdateRequest request);
    
    /**
     * 获取用户在线状态
     * 
     * @param userId 用户ID
     * @return 状态响应
     */
    PresenceResponse getUserPresence(Long userId);
    
    /**
     * 批量获取用户状态
     * 
     * @param userIds 用户ID列表
     * @return 状态列表
     */
    List<PresenceResponse> batchGetPresence(List<Long> userIds);
    
    /**
     * 用户登录
     * 
     * @param userId 用户ID
     * @param deviceType 设备类型
     * @param deviceId 设备ID
     * @param ip IP地址
     * @param serverNode 服务器节点
     * @return 状态响应
     */
    PresenceResponse userLogin(Long userId, Integer deviceType, String deviceId, 
                                String ip, String serverNode);
    
    /**
     * 用户登出
     * 
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 状态响应
     */
    PresenceResponse userLogout(Long userId, String deviceId);
    
    /**
     * 订阅用户状态变更
     * 
     * @param subscriberId 订阅者ID
     * @param targetUserId 目标用户ID
     * @return 是否成功
     */
    boolean subscribePresence(Long subscriberId, Long targetUserId);
    
    /**
     * 批量订阅
     * 
     * @param request 订阅请求
     * @return 成功订阅的用户数
     */
    int batchSubscribe(BatchSubscribeRequest request);
    
    /**
     * 取消订阅
     * 
     * @param subscriberId 订阅者ID
     * @param targetUserId 目标用户ID
     * @return 是否成功
     */
    boolean unsubscribePresence(Long subscriberId, Long targetUserId);
    
    /**
     * 处理超时离线用户
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 处理的用户数
     */
    int handleTimeoutUsers(int timeoutMinutes);
    
    /**
     * 获取在线用户数量
     * 
     * @return 在线用户数
     */
    int getOnlineUserCount();
    
    /**
     * 获取状态统计
     * 
     * @return 各状态用户数量
     */
    Map<String, Integer> getPresenceStatistics();
}
