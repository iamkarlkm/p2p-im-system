package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 骑手位置更新请求
 */
@Data
public class RiderLocationUpdateRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 骑手ID
     */
    @NotNull(message = "骑手ID不能为空")
    private Long riderId;

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    /**
     * 精度(米)
     */
    private Double accuracy;

    /**
     * 速度(m/s)
     */
    private Double speed;

    /**
     * 方向(0-360)
     */
    private Double heading;

    /**
     * 电量(%)
     */
    private Integer batteryLevel;

    /**
     * 配送状态
     */
    private Integer deliveryStatus;
}
