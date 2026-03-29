package com.im.backend.modules.geofence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 到店个性化服务推送记录
 */
@Data
@TableName("im_arrival_service_push")
public class ArrivalServicePush {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 到店记录ID */
    private Long arrivalRecordId;

    /** 用户ID */
    private Long userId;

    /** 商户ID */
    private Long merchantId;

    /** 门店ID */
    private Long storeId;

    /** 服务类型: WELCOME-欢迎消息, COUPON-优惠券, RECOMMEND-推荐, PRIORITY-优先服务 */
    private String serviceType;

    /** 推送内容 */
    private String content;

    /** 关联优惠券ID */
    private Long couponId;

    /** 优惠券金额 */
    private BigDecimal couponAmount;

    /** 推送渠道: APP_PUSH-应用推送, SMS-短信, WECHAT-微信, IN_APP-应用内 */
    private String pushChannel;

    /** 推送状态: PENDING-待推送, SENT-已发送, DELIVERED-已送达, READ-已读 */
    private String pushStatus;

    /** 发送时间 */
    private LocalDateTime sentTime;

    /** 送达时间 */
    private LocalDateTime deliveredTime;

    /** 读取时间 */
    private LocalDateTime readTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
