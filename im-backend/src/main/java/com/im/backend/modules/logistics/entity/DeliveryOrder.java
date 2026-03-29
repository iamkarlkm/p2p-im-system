package com.im.backend.modules.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送订单实体类
 * 用于存储即时配送订单信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("delivery_order")
public class DeliveryOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 商户ID */
    private Long merchantId;

    /** 商户名称 */
    private String merchantName;

    /** 商户地址 */
    private String merchantAddress;

    /** 商户经度 */
    private BigDecimal merchantLongitude;

    /** 商户纬度 */
    private BigDecimal merchantLatitude;

    /** 用户ID */
    private Long userId;

    /** 用户姓名 */
    private String userName;

    /** 用户电话 */
    private String userPhone;

    /** 配送地址 */
    private String deliveryAddress;

    /** 配送经度 */
    private BigDecimal deliveryLongitude;

    /** 配送纬度 */
    private BigDecimal deliveryLatitude;

    /** 订单金额 */
    private BigDecimal orderAmount;

    /** 配送费 */
    private BigDecimal deliveryFee;

    /** 预计送达时间 */
    private LocalDateTime estimatedDeliveryTime;

    /** 要求送达时间 */
    private LocalDateTime requiredDeliveryTime;

    /** 骑手ID */
    private Long riderId;

    /** 骑手姓名 */
    private String riderName;

    /** 骑手电话 */
    private String riderPhone;

    /** 订单状态: 1-待分配 2-已分配 3-已取货 4-配送中 5-已送达 6-已完成 7-已取消 */
    private Integer status;

    /** 配送距离(米) */
    private Integer deliveryDistance;

    /** 配送时长(分钟) */
    private Integer deliveryDuration;

    /** 订单备注 */
    private String remark;

    /** 取消原因 */
    private String cancelReason;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 分配时间 */
    private LocalDateTime assignTime;

    /** 取货时间 */
    private LocalDateTime pickupTime;

    /** 送达时间 */
    private LocalDateTime deliverTime;

    /** 完成时间 */
    private LocalDateTime completeTime;

    /** 逻辑删除: 0-正常 1-已删除 */
    @TableLogic
    private Integer deleted;
}
