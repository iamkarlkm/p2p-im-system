package com.im.live.service;

import com.im.live.dto.*;
import com.im.live.entity.LiveRoom;

import java.util.List;

/**
 * 直播服务接口
 */
public interface LiveRoomService {

    /**
     * 创建直播间
     */
    LiveRoomDTO createRoom(CreateLiveRoomRequestDTO request, Long anchorId);

    /**
     * 开始直播
     */
    LiveRoomDTO startLive(Long roomId, Long anchorId);

    /**
     * 结束直播
     */
    LiveRoomDTO endLive(Long roomId, Long anchorId);

    /**
     * 获取直播间信息
     */
    LiveRoomDTO getRoomInfo(Long roomId, Long userId);

    /**
     * 获取直播间列表
     */
    List<LiveRoomDTO> getRoomList(Integer status, String category, int page, int size);

    /**
     * 搜索直播间
     */
    List<LiveRoomDTO> searchRooms(String keyword, int page, int size);

    /**
     * 获取主播的直播间
     */
    List<LiveRoomDTO> getAnchorRooms(Long anchorId, int page, int size);

    /**
     * 更新直播间信息
     */
    LiveRoomDTO updateRoom(Long roomId, CreateLiveRoomRequestDTO request, Long anchorId);

    /**
     * 删除直播间
     */
    void deleteRoom(Long roomId, Long anchorId);

    /**
     * 预约直播
     */
    void subscribeLive(Long roomId, Long userId);

    /**
     * 取消预约
     */
    void unsubscribeLive(Long roomId, Long userId);

    /**
     * 获取推流地址
     */
    String getPushUrl(Long roomId, Long anchorId);

    /**
     * 心跳保活
     */
    void heartbeat(Long roomId, Long anchorId);

    /**
     * 获取推荐直播间
     */
    List<LiveRoomDTO> getRecommendedRooms(Long userId, int page, int size);

    /**
     * 获取附近直播间
     */
    List<LiveRoomDTO> getNearbyRooms(Double longitude, Double latitude, Integer radius, int page, int size);
}
