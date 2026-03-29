package com.im.backend.modules.local.life.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.core.result.PageResult;
import com.im.backend.modules.local.life.dto.*;
import com.im.backend.modules.local.life.entity.Activity;

import java.util.List;

/**
 * 活动服务接口
 */
public interface ActivityService extends IService<Activity> {

    /**
     * 创建活动
     */
    ActivityResponse createActivity(CreateActivityRequest request, Long userId);

    /**
     * 更新活动
     */
    ActivityResponse updateActivity(Long activityId, CreateActivityRequest request, Long userId);

    /**
     * 发布活动
     */
    void publishActivity(Long activityId, Long userId);

    /**
     * 取消活动
     */
    void cancelActivity(Long activityId, Long userId, String reason);

    /**
     * 获取活动详情
     */
    ActivityResponse getActivityDetail(Long activityId, Long currentUserId);

    /**
     * 查询活动列表
     */
    PageResult<ActivityResponse> queryActivities(ActivityQueryRequest request, Long currentUserId);

    /**
     * 获取附近活动
     */
    List<ActivityResponse> getNearbyActivities(Double longitude, Double latitude, Integer radius, Integer limit);

    /**
     * 获取热门活动
     */
    List<ActivityResponse> getHotActivities(Integer limit);

    /**
     * 获取推荐活动
     */
    List<ActivityResponse> getRecommendedActivities(Long userId, Integer limit);

    /**
     * 搜索活动
     */
    PageResult<ActivityResponse> searchActivities(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long activityId);

    /**
     * 获取活动统计
     */
    ActivityStatisticsResponse getActivityStatistics(Long activityId, Long userId);

    /**
     * 检查用户是否有权限管理活动
     */
    boolean hasManagePermission(Long activityId, Long userId);
}
