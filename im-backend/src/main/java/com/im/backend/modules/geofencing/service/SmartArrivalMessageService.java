package com.im.backend.modules.geofencing.service;

import com.im.backend.modules.geofencing.dto.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能到店消息服务接口
 * 生成和发送个性化到店消息
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface SmartArrivalMessageService {
    
    /**
     * 生成欢迎消息
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     * @param merchantId 商户ID
     * @return 生成的消息
     */
    SmartArrivalMessageVO generateWelcomeMessage(Long userId, Long geofenceId, Long merchantId);
    
    /**
     * 生成感谢消息
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     * @param merchantId 商户ID
     * @param dwellMinutes 停留时长
     * @return 生成的消息
     */
    SmartArrivalMessageVO generateThankYouMessage(Long userId, Long geofenceId, 
                                                   Long merchantId, Integer dwellMinutes);
    
    /**
     * 生成个性化服务消息
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     * @param messageType 消息类型
     * @return 生成的消息
     */
    SmartArrivalMessageVO generatePersonalizedMessage(Long userId, Long geofenceId, 
                                                       String messageType);
    
    /**
     * 发送消息
     * @param messageId 消息ID
     * @param channel 发送渠道
     */
    void sendMessage(Long messageId, String channel);
    
    /**
     * 批量发送消息
     * @param messageIds 消息ID列表
     */
    void batchSendMessages(List<Long> messageIds);
    
    /**
     * 处理围栏触发事件并发送消息
     * @param eventDTO 触发事件DTO
     * @return 发送的消息
     */
    SmartArrivalMessageVO processTriggerEvent(GeofenceTriggerEventDTO eventDTO);
    
    /**
     * 获取用户消息列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    List<SmartArrivalMessageVO> getUserMessages(Long userId, Integer page, Integer size);
    
    /**
     * 标记消息已读
     * @param messageId 消息ID
     * @param userId 用户ID
     */
    void markMessageAsRead(Long messageId, Long userId);
    
    /**
     * 获取未读消息数量
     * @param userId 用户ID
     * @return 未读数量
     */
    Integer getUnreadMessageCount(Long userId);
    
    /**
     * 生成个性化推荐内容
     * @param userId 用户ID
     * @param merchantId 商户ID
     * @return 推荐内容
     */
    PersonalizedRecommendationVO generateRecommendations(Long userId, Long merchantId);
    
    /**
     * 匹配最佳优惠券
     * @param userId 用户ID
     * @param merchantId 商户ID
     * @return 匹配的优惠券
     */
    MatchedCouponVO matchBestCoupon(Long userId, Long merchantId);
    
    /**
     * 获取消息模板
     * @param merchantId 商户ID
     * @param messageType 消息类型
     * @return 消息模板
     */
    MessageTemplateVO getMessageTemplate(Long merchantId, String messageType);
    
    /**
     * 重试失败消息
     * @param messageId 消息ID
     */
    void retryFailedMessage(Long messageId);
    
    /**
     * 取消消息发送
     * @param messageId 消息ID
     */
    void cancelMessage(Long messageId);
    
    /**
     * 获取消息统计
     * @param merchantId 商户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消息统计
     */
    MessageStatisticsVO getMessageStatistics(Long merchantId, 
                                             LocalDateTime startTime, 
                                             LocalDateTime endTime);
}
