package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建订单结果DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "创建订单结果")
public class CreateOrderResultDTO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "实付金额（元）")
    private BigDecimal payAmount;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "支付参数")
    private PayParamsDTO payParams;

    @Schema(description = "剩余支付时间（秒）")
    private Long remainingPaySeconds;

    @Schema(description = "是否需要支付")
    private Boolean needPay;
}
