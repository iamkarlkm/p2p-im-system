package com.im.modules.merchant.automation.service;

import com.im.modules.merchant.automation.dto.*;

import java.util.List;

/**
 * 商户智能提醒服务接口
 */
public interface IMerchantReminderService {
    
    /**
     * 获取商户提醒配置
     */
    MerchantReminderConfigResponse getConfig(String merchantId);
    
    /**
     * 更新商户提醒配置
     */
    MerchantReminderConfigResponse updateConfig(UpdateReminderConfigRequest request);
    
    /**
     * 触发异常订单提醒
     */
    void triggerAbnormalOrderReminder(String merchantId, String orderId, String reason);
    
    /**
     * 触发库存不足提醒
     */
    void triggerLowStockReminder(String merchantId, String productId, String productName, int currentStock);
    
    /**
     * 触发新订单提醒
     */
    void triggerNewOrderReminder(String merchantId, String orderId, String orderAmount);
    
    /**
     * 触发低分评价预警
     */
    void triggerLowRatingAlert(String merchantId, String reviewId, int rating);
    
    /**
     * 发送经营日报
     */
    void sendDailyReport(String merchantId);
    
    /**
     * 发送经营周报
     */
    void sendWeeklyReport(String merchantId);
    
    /**
     * 获取商户未读提醒数
     */
    int getUnreadCount(String merchantId);
    
    /**
     * 标记提醒为已读
     */
    void markAsRead(String recordId);
    
    /**
     * 获取商户提醒列表
     */
    List<MerchantReminderRecordResponse> getReminderList(String merchantId, int page, int size);
}
