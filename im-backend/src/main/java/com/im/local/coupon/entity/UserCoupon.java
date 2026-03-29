package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券领取记录实体
 * 记录用户领取的优惠券实例
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {

    /** 用户优惠券ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 优惠券ID */
    private Long couponId;

    /** 优惠券模板ID */
    private Long templateId;

    /** 商户ID */
    private Long merchantId;

    /** 优惠券码 */
    private String couponCode;

    /** 优惠券名称(快照) */
    private String couponName;

    /** 优惠券类型(快照) */
    private Integer couponType;

    /** 优惠面值(快照) */
    private BigDecimal couponValue;

    /** 使用门槛(快照) */
    private BigDecimal minSpend;

    /** 有效期开始时间 */
    private LocalDateTime validStartTime;

    /** 有效期结束时间 */
    private LocalDateTime validEndTime;

    /** 状态: 0-未使用 1-已使用 2-已过期 3-已作废 */
    private Integer status;

    /** 领取时间 */
    private LocalDateTime receiveTime;

    /** 使用时间 */
    private LocalDateTime useTime;

    /** 使用订单ID */
    private Long orderId;

    /** 订单金额 */
    private BigDecimal orderAmount;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 领取渠道: 1-主动领取 2-系统发放 3-活动赠送 4-分享获得 */
    private Integer receiveChannel;

    /** 领取场景: 1-附近推荐 2-商户主页 3-活动页面 4-分享链接 */
    private Integer receiveScene;

    /** 领取时的经度 */
    private Double receiveLongitude;

    /** 领取时的纬度 */
    private Double receiveLatitude;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
