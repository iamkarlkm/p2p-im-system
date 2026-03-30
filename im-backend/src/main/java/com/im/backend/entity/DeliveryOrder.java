package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送订单实体
 * 即时配送订单全流程追踪
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("delivery_order")
public class DeliveryOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商户订单ID
     */
    private Long merchantOrderId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * 订单状态: PENDING-待分配, ASSIGNED-已分配, PICKING-待取货, DELIVERING-配送中, ARRIVED-已送达, COMPLETED-已完成, CANCELLED-已取消
     */
    private String status;

    /**
     * 取货地址
     */
    private String pickupAddress;

    /**
     * 取货经度
     */
    private Double pickupLongitude;

    /**
     * 取货纬度
     */
    private Double pickupLatitude;

    /**
     * 送货地址
     */
    private String deliveryAddress;

    /**
     * 送货经度
     */
    private Double deliveryLongitude;

    /**
     * 送货纬度
     */
    private Double deliveryLatitude;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 配送距离(米)
     */
    private Integer deliveryDistance;

    /**
     * 配送费
     */
    private BigDecimal deliveryFee;

    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedArrivalTime;

    /**
     * 实际送达时间
     */
    private LocalDateTime actualArrivalTime;

    /**
     * 取货时间
     */
    private LocalDateTime pickupTime;

    /**
     * 分配给骑手时间
     */
    private LocalDateTime assignedTime;

    /**
     * 配送超时时间(分钟)
     */
    private Integer timeoutMinutes;

    /**
     * 配送物品描述
     */
    private String itemDescription;

    /**
     * 物品重量(kg)
     */
    private Double itemWeight;

    /**
     * 备注
     */
    private String remark;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 取消时间
     */
    private LocalDateTime cancelledTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;

    /**
     * 获取配送状态显示文本
     */
    public String getStatusText() {
        switch (status) {
            case "PENDING": return "待分配";
            case "ASSIGNED": return "已分配";
            case "PICKING": return "待取货";
            case "DELIVERING": return "配送中";
            case "ARRIVED": return "已送达";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return "未知";
        }
    }

    /**
     * 检查是否超时
     */
    public boolean isTimeout() {
        if (estimatedArrivalTime == null) return false;
        return LocalDateTime.now().isAfter(estimatedArrivalTime);
    }
}
