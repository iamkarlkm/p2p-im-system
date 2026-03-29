package com.im.local.delivery.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送订单实体
 * 本地物流配送订单全链路追踪
 */
@Data
public class DeliveryOrder {
    
    /** 配送订单ID */
    private Long id;
    
    /** 关联的商户订单ID */
    private Long merchantOrderId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 用户ID */
    private Long userId;
    
    /** 骑手ID */
    private Long riderId;
    
    /** 配送单号 */
    private String deliveryNo;
    
    /** 订单状态：0-待分配, 1-已分配, 2-取货中, 3-配送中, 4-已送达, 5-异常, 6-取消 */
    private Integer status;
    
    /** 取货地址 */
    private String pickupAddress;
    
    /** 取货纬度 */
    private BigDecimal pickupLat;
    
    /** 取货经度 */
    private BigDecimal pickupLng;
    
    /** 取货联系人 */
    private String pickupContact;
    
    /** 取货电话 */
    private String pickupPhone;
    
    /** 送货地址 */
    private String deliveryAddress;
    
    /** 送货纬度 */
    private BigDecimal deliveryLat;
    
    /** 送货经度 */
    private BigDecimal deliveryLng;
    
    /** 收货人姓名 */
    private String receiverName;
    
    /** 收货人电话 */
    private String receiverPhone;
    
    /** 配送距离(米) */
    private Integer distance;
    
    /** 预计配送时长(分钟) */
    private Integer estimatedDuration;
    
    /** 配送费 */
    private BigDecimal deliveryFee;
    
    /** 骑手小费 */
    private BigDecimal tipAmount;
    
    /** 物品类型 */
    private String itemType;
    
    /** 物品重量(kg) */
    private BigDecimal itemWeight;
    
    /** 备注 */
    private String remark;
    
    /** 分配时间 */
    private LocalDateTime assignedAt;
    
    /** 取货时间 */
    private LocalDateTime pickedUpAt;
    
    /** 送达时间 */
    private LocalDateTime deliveredAt;
    
    /** 签收码 */
    private String signCode;
    
    /** 签收图片URL */
    private String signImageUrl;
    
    /** 异常原因 */
    private String exceptionReason;
    
    /** 异常描述 */
    private String exceptionDesc;
    
    /** 取消原因 */
    private String cancelReason;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 期望送达时间 */
    private LocalDateTime expectDeliverTime;
    
    /** 超时时间(分钟) */
    private Integer timeoutMinutes;
    
    /** 配送类型：1-即时送, 2-预约送 */
    private Integer deliveryType;
    
    /**
     * 计算剩余配送时间(分钟)
     */
    public Integer getRemainingMinutes() {
        if (expectDeliverTime == null) {
            return null;
        }
        int remaining = (int) java.time.Duration.between(
            LocalDateTime.now(), expectDeliverTime).toMinutes();
        return Math.max(remaining, 0);
    }
    
    /**
     * 检查是否超时
     */
    public boolean isTimeout() {
        if (expectDeliverTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expectDeliverTime);
    }
    
    /**
     * 检查是否可取消
     */
    public boolean canCancel() {
        return status != null && status <= 1;
    }
    
    /**
     * 获取配送进度百分比
     */
    public Integer getProgressPercent() {
        if (status == null) return 0;
        switch (status) {
            case 0: return 0;
            case 1: return 20;
            case 2: return 40;
            case 3: return 70;
            case 4: return 100;
            default: return 0;
        }
    }
}
