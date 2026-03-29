package com.im.backend.modules.location.service;

import com.im.backend.modules.location.model.dto.*;
import com.im.backend.modules.location.model.entity.LocationSharingSession;

import java.util.List;

/**
 * 位置共享服务接口
 */
public interface ILocationSharingService {

    /**
     * 创建位置共享会话
     */
    LocationShareResponse createSession(Long creatorId, CreateLocationShareRequest request);

    /**
     * 获取会话详情
     */
    LocationShareResponse getSessionDetail(String sessionId, Long userId);

    /**
     * 加入位置共享
     */
    void joinSession(Long userId, JoinLocationShareRequest request);

    /**
     * 离开位置共享
     */
    void leaveSession(String sessionId, Long userId);

    /**
     * 暂停位置共享
     */
    void pauseSession(String sessionId, Long userId);

    /**
     * 恢复位置共享
     */
    void resumeSession(String sessionId, Long userId);

    /**
     * 结束位置共享
     */
    void endSession(String sessionId, Long userId);

    /**
     * 更新位置
     */
    void updateLocation(Long userId, LocationUpdateRequest request);

    /**
     * 获取会话成员位置
     */
    List<SharedLocationDTO> getMemberLocations(String sessionId, Long userId);

    /**
     * 获取用户的活跃会话列表
     */
    List<LocationShareResponse> getUserActiveSessions(Long userId);

    /**
     * 获取会话围栏事件
     */
    List<GeofenceEventDTO> getSessionEvents(String sessionId, Long userId);
}
