package com.im.backend.modules.geofencing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 智能到店服务消息实体类
 * 记录发送给用户的个性化到店消息
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("smart_arrival_message")
public class SmartArrivalMessage {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 消息唯一标识 */
    private String messageId;
    
    /** 用户ID */
    private Long userId;
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 触发事件ID */
    private Long triggerEventId;
    
    /** 消息类型: WELCOME-欢迎, THANKS-感谢, OFFER-优惠, SERVICE-服务, SURVEY-问卷 */
    private String messageType;
    
    /** 消息标题 */
    private String title;
    
    /** 消息内容 */
    private String content;
    
    /** 消息副标题/摘要 */
    private String subtitle;
    
    /** 封面图片URL */
    private String coverImage;
    
    /** 跳转链接 */
    private String actionUrl;
    
    /** 跳转类型: URL-网页, MINIAPP-小程序, NATIVE-原生页面, COUPON-优惠券 */
    private String actionType;
    
    /** 按钮文字 */
    private String actionButtonText;
    
    /** 优惠券ID */
    private Long couponId;
    
    /** 优惠券名称 */
    private String couponName;
    
    /** 优惠券金额 */
    private BigDecimal couponAmount;
    
    /** 活动ID */
    private Long activityId;
    
    /** 个性化推荐商品ID列表 */
    private String recommendedProductIds;
    
    /** 会员等级识别 */
    private Integer userMemberLevel;
    
    /** 会员专属标识 */
    private Boolean memberExclusive;
    
    /** 发送状态: PENDING-待发送, SENT-已发送, DELIVERED-已送达, READ-已读, FAILED-失败 */
    private String status;
    
    /** 发送时间 */
    private LocalDateTime sendTime;
    
    /** 送达时间 */
    private LocalDateTime deliveredTime;
    
    /** 已读时间 */
    private LocalDateTime readTime;
    
    /** 推送渠道: PUSH-推送, SMS-短信, WECHAT-微信模板, INAPP-应用内 */
    private String channel;
    
    /** 推送令牌 */
    private String pushToken;
    
    /** 失败原因 */
    private String failReason;
    
    /** 重试次数 */
    private Integer retryCount;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 删除标记 */
    @TableLogic
    private Boolean deleted;
}
