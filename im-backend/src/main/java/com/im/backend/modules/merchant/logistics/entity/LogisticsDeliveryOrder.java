package com.im.backend.modules.merchant.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 物流配送订单实体 - 功能#311: 本地物流配送调度
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("logistics_delivery_order")
public class LogisticsDeliveryOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 商户ID */
    private Long merchantId;

    /** 用户ID */
    private Long userId;

    /** 骑手ID */
    private Long riderId;

    /** 配送状态: 1-待分配, 2-待取货, 3-配送中, 4-已送达, 5-已完成, 6-异常 */
    private Integer status;

    /** 取货地址 */
    private String pickupAddress;

    /** 取货经度 */
    private BigDecimal pickupLng;

    /** 取货纬度 */
    private BigDecimal pickupLat;

    /** 送货地址 */
    private String deliveryAddress;

    /** 送货经度 */
    private BigDecimal deliveryLng;

    /** 送货纬度 */
    private BigDecimal deliveryLat;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人电话 */
    private String receiverPhone;

    /** 配送距离(米) */
    private Integer distance;

    /** 配送费 */
    private BigDecimal deliveryFee;

    /** 预计送达时间 */
    private LocalDateTime estimatedArrivalTime;

    /** 实际送达时间 */
    private LocalDateTime actualArrivalTime;

    /** 备注 */
    private String remark;

    /** 取消原因 */
    private String cancelReason;

    @TableLogic
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
