package com.im.backend.modules.delivery.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建DTO
 */
@Data
public class OrderCreateDTO {

    @NotNull(message = "业务订单ID不能为空")
    private Long bizOrderId;

    @NotNull(message = "业务类型不能为空")
    private String bizType;

    @NotNull(message = "商家ID不能为空")
    private Long merchantId;

    private String merchantName;

    @NotNull(message = "商家纬度不能为空")
    private BigDecimal merchantLat;

    @NotNull(message = "商家经度不能为空")
    private BigDecimal merchantLng;

    private String merchantAddress;
    private String merchantPhone;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String userName;
    private String userPhone;

    @NotNull(message = "配送纬度不能为空")
    private BigDecimal deliveryLat;

    @NotNull(message = "配送经度不能为空")
    private BigDecimal deliveryLng;

    private String deliveryAddress;
    private String houseNumber;
    private String estimatedDeliveryTime;
    private BigDecimal deliveryFee;
    private BigDecimal orderAmount;
    private Integer itemCount;
    private Integer weight;
    private String remark;
}
