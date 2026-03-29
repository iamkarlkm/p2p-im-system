package com.im.backend.modules.merchant.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单配送追踪记录实体
 * 记录骑手配送过程中的位置和状态
 */
@Data
@TableName("im_order_delivery_tracking")
public class OrderDeliveryTracking {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * 配送状态: 0-待接单, 1-已接单, 2-已到店, 3-已取餐, 4-配送中, 5-已送达, 6-已完成
     */
    private Integer deliveryStatus;

    /**
     * 当前经度
     */
    private BigDecimal longitude;

    /**
     * 当前纬度
     */
    private BigDecimal latitude;

    /**
     * 位置精度(米)
     */
    private Double accuracy;

    /**
     * 移动速度(m/s)
     */
    private Double speed;

    /**
     * 移动方向(0-360度)
     */
    private Double heading;

    /**
     * 骑手手机电量(%)
     */
    private Integer batteryLevel;

    /**
     * 距离商家距离(米)
     */
    private Double distanceToMerchant;

    /**
     * 距离用户距离(米)
     */
    private Double distanceToUser;

    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedArrivalTime;

    /**
     * 位置上报时间
     */
    private LocalDateTime locationTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
