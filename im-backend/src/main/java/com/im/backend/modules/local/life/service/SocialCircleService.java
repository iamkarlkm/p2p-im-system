package com.im.backend.modules.local.life.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.core.result.PageResult;
import com.im.backend.modules.local.life.dto.*;
import com.im.backend.modules.local.life.entity.SocialCircle;

import java.util.List;

/**
 * 社交圈子服务接口
 */
public interface SocialCircleService extends IService<SocialCircle> {

    /**
     * 创建圈子
     */
    CircleResponse createCircle(CreateCircleRequest request, Long userId);

    /**
     * 更新圈子
     */
    CircleResponse updateCircle(Long circleId, CreateCircleRequest request, Long userId);

    /**
     * 获取圈子详情
     */
    CircleResponse getCircleDetail(Long circleId, Long currentUserId);

    /**
     * 查询圈子列表
     */
    PageResult<CircleResponse> queryCircles(String keyword, String category, Integer pageNum, Integer pageSize);

    /**
     * 获取附近圈子
     */
    List<CircleResponse> getNearbyCircles(Double longitude, Double latitude, Integer radius, Integer limit);

    /**
     * 获取热门圈子
     */
    List<CircleResponse> getHotCircles(Integer limit);

    /**
     * 加入圈子
     */
    void joinCircle(JoinCircleRequest request, Long userId);

    /**
     * 退出圈子
     */
    void leaveCircle(Long circleId, Long userId);

    /**
     * 审核入圈申请
     */
    void reviewJoinRequest(Long circleId, Long memberId, Long reviewerId, boolean approved, String remark);

    /**
     * 设置成员角色
     */
    void setMemberRole(Long circleId, Long memberId, String role, Long operatorId);

    /**
     * 移除成员
     */
    void removeMember(Long circleId, Long memberId, Long operatorId);

    /**
     * 禁言成员
     */
    void muteMember(Long circleId, Long memberId, Integer minutes, Long operatorId);

    /**
     * 获取用户加入的圈子
     */
    List<CircleResponse> getUserCircles(Long userId);

    /**
     * 检查用户是否是圈子成员
     */
    boolean isCircleMember(Long circleId, Long userId);

    /**
     * 获取用户的成员角色
     */
    String getMemberRole(Long circleId, Long userId);

    /**
     * 发布圈子公告
     */
    void publishAnnouncement(Long circleId, String announcement, Long operatorId);

    /**
     * 解散圈子
     */
    void disbandCircle(Long circleId, Long operatorId);
}
