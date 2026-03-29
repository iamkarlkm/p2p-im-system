package com.im.backend.modules.geofence.service;

import com.im.backend.modules.geofence.entity.ArrivalRecord;

/**
 * 个性化到店服务接口
 */
public interface IPersonalizedArrivalService {

    /**
     * 用户到店时触发个性化服务
     */
    void onUserArrival(ArrivalRecord arrivalRecord);

    /**
     * 推送欢迎消息
     */
    void pushWelcomeMessage(Long arrivalRecordId, Long userId, Long merchantId, String memberLevel);

    /**
     * 推送优惠券
     */
    void pushCoupon(Long arrivalRecordId, Long userId, Long merchantId, String customerTag);

    /**
     * 推送个性化推荐
     */
    void pushRecommendations(Long arrivalRecordId, Long userId, Long merchantId);

    /**
     * 发送优先接待提醒给商家
     */
    void sendPriorityAlertToMerchant(Long arrivalRecordId, Long merchantId, Long storeId, String memberLevel);

    /**
     * 获取欢迎消息模板
     */
    String getWelcomeMessageTemplate(Long merchantId, Long storeId, String customerTag);
}
