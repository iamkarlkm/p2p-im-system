package com.im.backend.modules.logistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建配送订单请求DTO
 */
@Data
@Schema(description = "创建配送订单请求")
public class CreateDeliveryOrderRequest {

    @Schema(description = "商户ID", required = true)
    private Long merchantId;

    @Schema(description = "商户名称", required = true)
    private String merchantName;

    @Schema(description = "商户地址", required = true)
    private String merchantAddress;

    @Schema(description = "商户经度", required = true)
    private BigDecimal merchantLongitude;

    @Schema(description = "商户纬度", required = true)
    private BigDecimal merchantLatitude;

    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "用户姓名", required = true)
    private String userName;

    @Schema(description = "用户电话", required = true)
    private String userPhone;

    @Schema(description = "配送地址", required = true)
    private String deliveryAddress;

    @Schema(description = "配送经度", required = true)
    private BigDecimal deliveryLongitude;

    @Schema(description = "配送纬度", required = true)
    private BigDecimal deliveryLatitude;

    @Schema(description = "订单金额", required = true)
    private BigDecimal orderAmount;

    @Schema(description = "配送费")
    private BigDecimal deliveryFee;

    @Schema(description = "要求送达时间")
    private LocalDateTime requiredDeliveryTime;

    @Schema(description = "订单备注")
    private String remark;
}
