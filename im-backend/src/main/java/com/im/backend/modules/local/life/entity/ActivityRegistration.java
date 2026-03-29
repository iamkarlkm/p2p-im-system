package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动报名记录实体
 * 存储用户报名参加活动的信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_activity_registration")
public class ActivityRegistration {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 报名唯一标识 */
    private String registrationCode;

    /** 活动ID */
    private Long activityId;

    /** 报名用户ID */
    private Long userId;

    /** 用户昵称 */
    private String userNickname;

    /** 用户头像 */
    private String userAvatar;

    /** 报名状态: PENDING-待确认, CONFIRMED-已确认, CANCELLED-已取消, REJECTED-已拒绝 */
    private String status;

    /** 报名人数(含本人) */
    private Integer participantCount;

    /** 报名人数(含本人) */
    private String participantNames;

    /** 联系电话 */
    private String contactPhone;

    /** 备注信息 */
    private String remark;

    /** 支付状态: UNPAID-未支付, PAID-已支付, REFUNDED-已退款 */
    private String paymentStatus;

    /** 支付金额 */
    private BigDecimal paymentAmount;

    /** 支付时间 */
    private LocalDateTime paymentTime;

    /** 支付订单号 */
    private String paymentOrderNo;

    /** 是否签到 */
    private Boolean checkedIn;

    /** 签到时间 */
    private LocalDateTime checkInTime;

    /** 签到地点经纬度 */
    private String checkInLocation;

    /** 评价状态: UNRATED-未评价, RATED-已评价 */
    private String ratingStatus;

    /** 评分 */
    private Integer rating;

    /** 评价内容 */
    private String ratingContent;

    /** 评价时间 */
    private LocalDateTime ratingTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 是否删除 */
    @TableLogic
    private Boolean deleted;
}
