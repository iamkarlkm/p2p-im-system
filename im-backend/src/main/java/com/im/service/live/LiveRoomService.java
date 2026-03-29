package com.im.service.live;

import com.im.common.PageResult;
import com.im.dto.live.*;

/**
 * 直播服务接口
 * 小程序直播与本地电商核心服务
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface LiveRoomService {

    /**
     * 创建直播间
     *
     * @param userId  主播用户ID
     * @param request 创建请求
     * @return 直播间详情
     */
    LiveRoomDetailDTO createLiveRoom(Long userId, CreateLiveRoomRequestDTO request);

    /**
     * 获取直播间详情
     *
     * @param roomId 直播间ID
     * @param userId 当前用户ID
     * @return 直播间详情
     */
    LiveRoomDetailDTO getLiveRoomDetail(Long roomId, Long userId);

    /**
     * 开始直播
     *
     * @param roomId 直播间ID
     * @param userId 主播用户ID
     * @return 推流地址等信息
     */
    LiveRoomDetailDTO startLive(Long roomId, Long userId);

    /**
     * 结束直播
     *
     * @param roomId 直播间ID
     * @param userId 主播用户ID
     */
    void endLive(Long roomId, Long userId);

    /**
     * 暂停直播
     *
     * @param roomId 直播间ID
     * @param userId 主播用户ID
     */
    void pauseLive(Long roomId, Long userId);

    /**
     * 恢复直播
     *
     * @param roomId 直播间ID
     * @param userId 主播用户ID
     */
    void resumeLive(Long roomId, Long userId);

    /**
     * 获取直播间列表
     *
     * @param page     页码
     * @param size     每页数量
     * @param status   直播状态
     * @param liveType 直播类型
     * @param keyword  搜索关键词
     * @param latitude 纬度
     * @param longitude 经度
     * @return 直播间列表
     */
    PageResult<LiveRoomListDTO> listLiveRooms(Integer page, Integer size, Integer status, 
                                               Integer liveType, String keyword, 
                                               Double latitude, Double longitude);

    /**
     * 获取推荐的直播间
     *
     * @param limit 数量限制
     * @return 直播间列表
     */
    java.util.List<LiveRoomListDTO> getRecommendedRooms(Integer limit);

    /**
     * 获取附近直播间
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param radius    半径（米）
     * @param limit     数量限制
     * @return 直播间列表
     */
    java.util.List<LiveRoomListDTO> getNearbyRooms(Double latitude, Double longitude, 
                                                    Integer radius, Integer limit);

    /**
     * 用户进入直播间
     *
     * @param roomId 直播间ID
     * @param userId 用户ID
     */
    void enterRoom(Long roomId, Long userId);

    /**
     * 用户离开直播间
     *
     * @param roomId 直播间ID
     * @param userId 用户ID
     */
    void leaveRoom(Long roomId, Long userId);

    /**
     * 发送弹幕/评论
     *
     * @param roomId  直播间ID
     * @param userId  用户ID
     * @param content 内容
     * @return 评论信息
     */
    LiveCommentDTO sendComment(Long roomId, Long userId, String content);

    /**
     * 点赞
     *
     * @param roomId 直播间ID
     * @param userId 用户ID
     * @param count  点赞数量
     */
    void likeLive(Long roomId, Long userId, Integer count);

    /**
     * 分享直播间
     *
     * @param roomId 直播间ID
     * @param userId 用户ID
     */
    void shareLive(Long roomId, Long userId);

    /**
     * 获取直播间在线观众列表
     *
     * @param roomId 直播间ID
     * @param page   页码
     * @param size   每页数量
     * @return 观众列表
     */
    PageResult<LiveViewerDTO> getOnlineViewers(Long roomId, Integer page, Integer size);

    /**
     * 获取直播回放列表
     *
     * @param anchorId 主播ID
     * @param page     页码
     * @param size     每页数量
     * @return 回放列表
     */
    PageResult<LiveReplayDTO> getReplayList(Long anchorId, Integer page, Integer size);
}
