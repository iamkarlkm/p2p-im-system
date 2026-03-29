package com.im.local.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送订单响应
 */
@Data
public class DeliveryOrderResponse {
    
    /** 配送订单ID */
    private Long id;
    
    /** 配送单号 */
    private String deliveryNo;
    
    /** 订单状态 */
    private Integer status;
    
    /** 状态名称 */
    private String statusName;
    
    /** 骑手ID */
    private Long riderId;
    
    /** 骑手姓名 */
    private String riderName;
    
    /** 骑手电话 */
    private String riderPhone;
    
    /** 骑手头像 */
    private String riderAvatar;
    
    /** 骑手评分 */
    private BigDecimal riderRating;
    
    /** 取货地址 */
    private String pickupAddress;
    
    /** 送货地址 */
    private String deliveryAddress;
    
    /** 配送距离 */
    private Integer distance;
    
    /** 预计配送时长 */
    private Integer estimatedDuration;
    
    /** 剩余时间(分钟) */
    private Integer remainingMinutes;
    
    /** 配送进度(%) */
    private Integer progressPercent;
    
    /** 配送费 */
    private BigDecimal deliveryFee;
    
    /** 骑手当前纬度 */
    private BigDecimal riderLat;
    
    /** 骑手当前经度 */
    private BigDecimal riderLng;
    
    /** 是否超时 */
    private Boolean isTimeout;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 分配时间 */
    private LocalDateTime assignedAt;
    
    /** 取货时间 */
    private LocalDateTime pickedUpAt;
    
    /** 送达时间 */
    private LocalDateTime deliveredAt;
    
    /** 期望送达时间 */
    private LocalDateTime expectDeliverTime;
}
