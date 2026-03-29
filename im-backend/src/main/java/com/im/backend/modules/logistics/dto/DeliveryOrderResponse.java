package com.im.backend.modules.logistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送订单响应DTO
 */
@Data
@Schema(description = "配送订单响应")
public class DeliveryOrderResponse {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "商户ID")
    private Long merchantId;

    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "商户地址")
    private String merchantAddress;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "用户电话")
    private String userPhone;

    @Schema(description = "配送地址")
    private String deliveryAddress;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "配送费")
    private BigDecimal deliveryFee;

    @Schema(description = "订单状态: 1-待分配 2-已分配 3-已取货 4-配送中 5-已送达 6-已完成 7-已取消")
    private Integer status;

    @Schema(description = "订单状态描述")
    private String statusDesc;

    @Schema(description = "骑手ID")
    private Long riderId;

    @Schema(description = "骑手姓名")
    private String riderName;

    @Schema(description = "骑手电话")
    private String riderPhone;

    @Schema(description = "配送距离(米)")
    private Integer deliveryDistance;

    @Schema(description = "预计送达时间")
    private LocalDateTime estimatedDeliveryTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "骑手当前经度")
    private BigDecimal riderCurrentLongitude;

    @Schema(description = "骑手当前纬度")
    private BigDecimal riderCurrentLatitude;
}
