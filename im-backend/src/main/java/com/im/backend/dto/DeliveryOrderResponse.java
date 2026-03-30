package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送订单响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryOrderResponse {

    /**
     * 配送订单ID
     */
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
     * 订单状态
     */
    private String status;

    /**
     * 状态显示文本
     */
    private String statusText;

    /**
     * 取货地址
     */
    private String pickupAddress;

    /**
     * 送货地址
     */
    private String deliveryAddress;

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
     * 剩余送达时间(分钟)
     */
    private Integer remainingMinutes;

    /**
     * 骑手信息
     */
    private RiderInfo rider;

    /**
     * 取货时间
     */
    private LocalDateTime pickupTime;

    /**
     * 实际送达时间
     */
    private LocalDateTime actualArrivalTime;

    /**
     * 配送物品描述
     */
    private String itemDescription;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否超时
     */
    private Boolean timeout;

    /**
     * 骑手信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RiderInfo {

        /**
         * 骑手ID
         */
        private Long riderId;

        /**
         * 骑手姓名
         */
        private String riderName;

        /**
         * 骑手电话
         */
        private String riderPhone;

        /**
         * 骑手头像
         */
        private String avatarUrl;

        /**
         * 评分
         */
        private BigDecimal rating;

        /**
         * 当前经度
         */
        private Double currentLongitude;

        /**
         * 当前纬度
         */
        private Double currentLatitude;

        /**
         * 距离取货地(米)
         */
        private Integer distanceToPickup;

        /**
         * 距离送货地(米)
         */
        private Integer distanceToDelivery;

        /**
         * 预计到达时间(分钟)
         */
        private Integer estimatedArrivalMinutes;
    }
}
