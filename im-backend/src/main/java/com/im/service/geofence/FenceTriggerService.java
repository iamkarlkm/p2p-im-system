package com.im.service.geofence;

import com.im.entity.geofence.UserFenceStatus;
import com.im.entity.geofence.FenceTriggerMessage;
import java.util.List;

/**
 * 围栏触发消息服务接口
 */
public interface FenceTriggerService {
    
    /**
     * 处理用户位置更新
     */
    void processLocationUpdate(String userId, Double longitude, Double latitude);
    
    /**
     * 处理进入围栏事件
     */
    void handleEnterFence(String userId, String fenceId);
    
    /**
     * 处理停留超时事件
     */
    void handleDwellTimeout(String userId, String fenceId);
    
    /**
     * 处理离开围栏事件
     */
    void handleExitFence(String userId, String fenceId);
    
    /**
     * 发送触发消息
     */
    void sendTriggerMessage(FenceTriggerMessage message);
    
    /**
     * 获取用户的围栏状态
     */
    List<UserFenceStatus> getUserFenceStatuses(String userId);
    
    /**
     * 获取围栏中的用户列表
     */
    List<UserFenceStatus> getUsersInFence(String fenceId);
    
    /**
     * 获取用户的消息历史
     */
    List<FenceTriggerMessage> getUserMessageHistory(String userId, Integer limit);
}
