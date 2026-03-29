package com.im.location.service;

import com.im.location.dto.*;

import java.util.List;

/**
 * 位置共享服务接口
 */
public interface ILocationSharingService {
    
    /**
     * 创建位置共享会话
     */
    LocationSharingSessionResponse createSession(Long userId, CreateLocationSharingRequest request);
    
    /**
     * 获取会话详情
     */
    LocationSharingSessionResponse getSessionDetail(String sessionId);
    
    /**
     * 加入位置共享
     */
    void joinSession(Long userId, JoinLocationSharingRequest request);
    
    /**
     * 离开位置共享
     */
    void leaveSession(Long userId, String sessionId);
    
    /**
     * 更新位置
     */
    void updateLocation(Long userId, UpdateLocationRequest request);
    
    /**
     * 更新会话状态
     */
    void updateSessionStatus(Long userId, String sessionId, Integer status);
    
    /**
     * 获取用户参与的活跃会话列表
     */
    List<LocationSharingSessionResponse> getUserActiveSessions(Long userId);
    
    /**
     * 获取会话成员列表
     */
    List<LocationSharingMemberResponse> getSessionMembers(String sessionId);
    
    /**
     * 计算ETA（预计到达时间）
     */
    Integer calculateETA(String sessionId, Double longitude, Double latitude);
    
    /**
     * 检查围栏触发
     */
    void checkGeofenceTrigger(String sessionId, Long userId, Double longitude, Double latitude);
}
