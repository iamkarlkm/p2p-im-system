package com.im.backend.modules.location.service;

import com.im.backend.modules.location.model.entity.LocationSharingSession;
import com.im.backend.modules.location.model.entity.LocationSharingMember;

import java.util.List;

/**
 * 位置共享WebSocket服务接口
 */
public interface ILocationSharingWebSocketService {

    /**
     * 广播位置更新给会话内所有成员
     */
    void broadcastLocationUpdate(String sessionId, Long senderId, Double lat, Double lng);

    /**
     * 广播成员加入事件
     */
    void broadcastMemberJoined(String sessionId, LocationSharingMember member);

    /**
     * 广播成员离开事件
     */
    void broadcastMemberLeft(String sessionId, Long userId);

    /**
     * 广播成员到达事件
     */
    void broadcastMemberArrived(String sessionId, Long userId, String destinationName);

    /**
     * 广播围栏触发事件
     */
    void broadcastGeofenceEvent(String sessionId, Long userId, String eventType, String geofenceName);

    /**
     * 发送会话结束通知
     */
    void notifySessionEnded(String sessionId, String reason);

    /**
     * 获取会话成员的WebSocket会话ID列表
     */
    List<String> getMemberSessionIds(String sessionId);
}
