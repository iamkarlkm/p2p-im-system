package com.im.local.coupon.service;

import com.im.local.coupon.entity.*;

import java.util.List;

/**
 * 营销活动服务接口
 */
public interface IMarketingActivityService {

    /**
     * 创建营销活动
     */
    Long createActivity(MarketingActivity activity, Long createBy);

    /**
     * 获取活动详情
     */
    MarketingActivity getActivityDetail(Long activityId);

    /**
     * 参与活动
     */
    ActivityParticipation participateActivity(Long activityId, Long userId, Integer participateType);

    /**
     * 获取进行中的活动列表
     */
    List<MarketingActivity> getActiveActivities();

    /**
     * 获取商户活动列表
     */
    List<MarketingActivity> getMerchantActivities(Long merchantId);

    /**
     * 创建拼团
     */
    GroupBuying createGroupBuying(Long activityId, Long leaderId);

    /**
     * 加入拼团
     */
    boolean joinGroupBuying(Long groupId, Long userId);

    /**
     * 发起砍价
     */
    BargainActivity startBargain(Long activityId, Long userId);

    /**
     * 帮助砍价
     */
    BargainHelpRecord helpBargain(Long bargainId, Long helperId);

    /**
     * 获取拼团详情
     */
    GroupBuying getGroupBuyingDetail(Long groupId);

    /**
     * 获取砍价详情
     */
    BargainActivity getBargainDetail(Long bargainId);
}
