package com.im.entity.delivery;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 配送订单实体类 - 即时配送运力调度系统
 * 存储订单信息、配送状态、路径规划
 */
@Data
public class DeliveryOrder {
    
    /** 配送订单ID */
    private Long id;
    
    /** 业务订单ID */
    private Long businessOrderId;
    
    /** 订单编号 */
    private String orderNo;
    
    /** 商家ID */
    private Long merchantId;
    
    /** 商家名称 */
    private String merchantName;
    
    /** 商家地址 */
    private String merchantAddress;
    
    /** 商家经度 */
    private BigDecimal merchantLongitude;
    
    /** 商家纬度 */
    private BigDecimal merchantLatitude;
    
    /** 商家电话 */
    private String merchantPhone;
    
    /** 顾客ID */
    private Long customerId;
    
    /** 顾客姓名 */
    private String customerName;
    
    /** 顾客电话 */
    private String customerPhone;
    
    /** 配送地址 */
    private String deliveryAddress;
    
    /** 配送地址经度 */
    private BigDecimal deliveryLongitude;
    
    /** 配送地址纬度 */
    private BigDecimal deliveryLatitude;
    
    /** 门牌号/详细地址 */
    private String addressDetail;
    
    /** 订单金额 */
    private BigDecimal orderAmount;
    
    /** 配送费 */
    private BigDecimal deliveryFee;
    
    /** 订单商品摘要 */
    private String goodsSummary;
    
    /** 商品数量 */
    private Integer goodsCount;
    
    /** 订单重量(kg) */
    private BigDecimal weight;
    
    /** 订单备注 */
    private String remark;
    
    /** 配送状态: WAITING-待分配, ASSIGNED-已分配, PICKING-取货中, DELIVERING-配送中, ARRIVED-已送达, COMPLETED-已完成, CANCELLED-已取消 */
    private String status;
    
    /** 分配骑手ID */
    private Long riderId;
    
    /** 骑手姓名 */
    private String riderName;
    
    /** 骑手电话 */
    private String riderPhone;
    
    /** 分配时间 */
    private LocalDateTime assignedAt;
    
    /** 接单时间 */
    private LocalDateTime acceptedAt;
    
    /** 到店时间 */
    private LocalDateTime arrivedAt;
    
    /** 取货时间 */
    private LocalDateTime pickedAt;
    
    /** 送达时间 */
    private LocalDateTime deliveredAt;
    
    /** 完成时间 */
    private LocalDateTime completedAt;
    
    /** 预计送达时间 */
    private LocalDateTime estimatedDeliveryTime;
    
    /** 要求送达时间 */
    private LocalDateTime requiredDeliveryTime;
    
    /** 配送距离(米) */
    private Integer deliveryDistance;
    
    /** 实际配送时长(分钟) */
    private Integer actualDeliveryMinutes;
    
    /** 配送区域ID */
    private Long zoneId;
    
    /** 配送路径JSON */
    private String deliveryPath;
    
    /** 异常标记: NORMAL-正常, DELAY-延误, COMPLAINT-投诉, RETURN-退回 */
    private String exceptionFlag;
    
    /** 异常原因 */
    private String exceptionReason;
    
    /** 取消原因 */
    private String cancelReason;
    
    /** 取消人: CUSTOMER-顾客, MERCHANT-商家, RIDER-骑手, SYSTEM-系统 */
    private String cancelledBy;
    
    /** 取消时间 */
    private LocalDateTime cancelledAt;
    
    /** 顾客评分 */
    private Integer customerRating;
    
    /** 顾客评价 */
    private String customerComment;
    
    /** 骑手评价顾客 */
    private String riderComment;
    
    /** 配送类型: IMMEDIATE-即时送, SCHEDULED-预约单 */
    private String deliveryType;
    
    /** 配送优先级: NORMAL-普通, HIGH-高, URGENT-紧急 */
    private String priority;
    
    /** 配送标签: RAIN-雨天, HOT-高温, NIGHT-夜间, HEAVY-重物 */
    private List<String> tags;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 是否删除 */
    private Boolean deleted;
    
    // ========== 业务方法 ==========
    
    /**
     * 状态流转: 等待分配 -> 已分配
     */
    public void assignToRider(Long riderId, String riderName, String riderPhone) {
        this.riderId = riderId;
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.status = "ASSIGNED";
        this.assignedAt = LocalDateTime.now();
    }
    
    /**
     * 状态流转: 已分配 -> 取货中
     */
    public void accept() {
        this.status = "PICKING";
        this.acceptedAt = LocalDateTime.now();
    }
    
    /**
     * 状态流转: 取货中 -> 配送中
     */
    public void pickUp() {
        this.status = "DELIVERING";
        this.pickedAt = LocalDateTime.now();
    }
    
    /**
     * 状态流转: 配送中 -> 已送达
     */
    public void arrive() {
        this.status = "ARRIVED";
        this.deliveredAt = LocalDateTime.now();
        if (pickedAt != null) {
            this.actualDeliveryMinutes = (int) java.time.Duration.between(pickedAt, deliveredAt).toMinutes();
        }
    }
    
    /**
     * 状态流转: 已送达 -> 已完成
     */
    public void complete() {
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * 取消订单
     */
    public void cancel(String reason, String cancelledBy) {
        this.status = "CANCELLED";
        this.cancelReason = reason;
        this.cancelledBy = cancelledBy;
        this.cancelledAt = LocalDateTime.now();
    }
    
    /**
     * 标记异常
     */
    public void markException(String flag, String reason) {
        this.exceptionFlag = flag;
        this.exceptionReason = reason;
    }
    
    /**
     * 更新预计送达时间
     */
    public void updateEstimatedTime(LocalDateTime estimatedTime) {
        this.estimatedDeliveryTime = estimatedTime;
    }
    
    /**
     * 检查是否延误
     */
    public boolean isDelayed() {
        if (estimatedDeliveryTime == null) return false;
        return LocalDateTime.now().isAfter(estimatedDeliveryTime) && 
               !"COMPLETED".equals(status) && !"CANCELLED".equals(status);
    }
    
    /**
     * 计算配送进度百分比
     */
    public int getProgressPercentage() {
        return switch (status) {
            case "WAITING" -> 0;
            case "ASSIGNED" -> 20;
            case "PICKING" -> 40;
            case "DELIVERING" -> 70;
            case "ARRIVED" -> 90;
            case "COMPLETED" -> 100;
            default -> 0;
        };
    }
}
