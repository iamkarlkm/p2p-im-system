package com.im.backend.modules.merchant.logistics.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 创建配送订单请求DTO - 功能#311: 本地物流配送调度
 */
@Data
public class DeliveryOrderCreateRequest {

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @NotBlank(message = "取货地址不能为空")
    private String pickupAddress;

    @NotNull(message = "取货坐标不能为空")
    private BigDecimal pickupLng;

    @NotNull(message = "取货坐标不能为空")
    private BigDecimal pickupLat;

    @NotBlank(message = "送货地址不能为空")
    private String deliveryAddress;

    @NotNull(message = "送货坐标不能为空")
    private BigDecimal deliveryLng;

    @NotNull(message = "送货坐标不能为空")
    private BigDecimal deliveryLat;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    private String remark;
}
