package com.im.entity.geofence;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 围栏触发消息实体类
 * 记录围栏触发后发送的消息
 */
@Data
public class FenceTriggerMessage {
    
    /** 消息ID */
    private String messageId;
    
    /** 围栏ID */
    private String fenceId;
    
    /** 用户ID */
    private String userId;
    
    /** 触发类型: ENTER-进入, DWELL-停留, EXIT-离开 */
    private String triggerType;
    
    /** 消息类型: WELCOME-欢迎, COUPON-优惠券, SERVICE-服务, SURVEY-调查 */
    private String messageType;
    
    /** 消息标题 */
    private String title;
    
    /** 消息内容 */
    private String content;
    
    /** 消息卡片数据 */
    private Map<String, Object> cardData;
    
    /** 跳转链接 */
    private String actionUrl;
    
    /** 推送渠道: APP_PUSH-应用推送, SMS-短信, WECHAT-微信, IN_APP-应用内 */
    private String pushChannel;
    
    /** 发送状态: PENDING-待发送, SENT-已发送, FAILED-失败, READ-已读 */
    private String sendStatus;
    
    /** 触发时间 */
    private LocalDateTime triggerTime;
    
    /** 发送时间 */
    private LocalDateTime sendTime;
    
    /** 读取时间 */
    private LocalDateTime readTime;
    
    /** 是否去重(避免重复发送) */
    private Boolean deduplicated;
    
    /** 去重Key */
    private String dedupKey;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
