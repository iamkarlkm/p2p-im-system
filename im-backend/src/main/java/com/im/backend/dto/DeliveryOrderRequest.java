package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 配送订单请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryOrderRequest {

    /**
     * 商户订单ID
     */
    @NotNull(message = "商户订单ID不能为空")
    private Long merchantOrderId;

    /**
     * 商户ID
     */
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    /**
     * 取货地址
     */
    @NotBlank(message = "取货地址不能为空")
    @Size(max = 500, message = "取货地址长度不能超过500")
    private String pickupAddress;

    /**
     * 取货经度
     */
    @NotNull(message = "取货经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度范围错误")
    @DecimalMax(value = "180.0", message = "经度范围错误")
    private Double pickupLongitude;

    /**
     * 取货纬度
     */
    @NotNull(message = "取货纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度范围错误")
    @DecimalMax(value = "90.0", message = "纬度范围错误")
    private Double pickupLatitude;

    /**
     * 送货地址
     */
    @NotBlank(message = "送货地址不能为空")
    @Size(max = 500, message = "送货地址长度不能超过500")
    private String deliveryAddress;

    /**
     * 送货经度
     */
    @NotNull(message = "送货经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度范围错误")
    @DecimalMax(value = "180.0", message = "经度范围错误")
    private Double deliveryLongitude;

    /**
     * 送货纬度
     */
    @NotNull(message = "送货纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度范围错误")
    @DecimalMax(value = "90.0", message = "纬度范围错误")
    private Double deliveryLatitude;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50")
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String receiverPhone;

    /**
     * 配送物品描述
     */
    @Size(max = 200, message = "物品描述长度不能超过200")
    private String itemDescription;

    /**
     * 物品重量(kg)
     */
    @DecimalMin(value = "0.01", message = "重量必须大于0")
    @DecimalMax(value = "50.0", message = "重量不能超过50kg")
    private Double itemWeight;

    /**
     * 备注
     */
    @Size(max = 300, message = "备注长度不能超过300")
    private String remark;
}
