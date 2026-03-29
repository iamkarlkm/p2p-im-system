package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 创建订单履约会话请求
 */
@Data
public class CreateFulfillmentSessionRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 商户ID
     */
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 骑手ID(可选,分配骑手后更新)
     */
    private Long riderId;

    /**
     * 预计送达时间(分钟)
     */
    private Integer estimatedDeliveryMinutes;
}
