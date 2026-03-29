package com.im.backend.modules.local.life.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.core.result.PageResult;
import com.im.backend.modules.local.life.dto.*;
import com.im.backend.modules.local.life.entity.ActivityRegistration;

import java.util.List;

/**
 * 活动报名服务接口
 */
public interface ActivityRegistrationService extends IService<ActivityRegistration> {

    /**
     * 报名活动
     */
    ActivityRegistrationResponse registerActivity(RegisterActivityRequest request, Long userId);

    /**
     * 取消报名
     */
    void cancelRegistration(Long activityId, Long userId, String reason);

    /**
     * 确认参加
     */
    void confirmParticipation(Long registrationId, Long userId);

    /**
     * 审核报名
     */
    void reviewRegistration(Long registrationId, Long reviewerId, boolean approved, String remark);

    /**
     * 获取报名详情
     */
    ActivityRegistrationResponse getRegistrationDetail(Long registrationId);

    /**
     * 获取活动的报名列表
     */
    PageResult<ActivityRegistrationResponse> getActivityRegistrations(Long activityId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户报名列表
     */
    List<ActivityRegistrationResponse> getUserRegistrations(Long userId, String status);

    /**
     * 活动签到
     */
    void checkIn(Long activityId, Long userId, Double longitude, Double latitude);

    /**
     * 评价活动
     */
    void rateActivity(Long activityId, Long userId, Integer rating, String content);

    /**
     * 检查用户是否已报名
     */
    boolean isRegistered(Long activityId, Long userId);

    /**
     * 获取用户的报名状态
     */
    String getRegistrationStatus(Long activityId, Long userId);
}
