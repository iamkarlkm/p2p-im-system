package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 退款申请结果DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "退款申请结果")
public class RefundApplyResultDTO {

    @Schema(description = "退款申请ID")
    private Long refundId;

    @Schema(description = "退款编号")
    private String refundNo;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "退款金额（元）")
    private BigDecimal refundAmount;

    @Schema(description = "退款状态：0-待审核 1-审核通过 2-审核拒绝 3-退款中 4-退款成功 5-退款失败")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "申请时间")
    private String applyTime;

    @Schema(description = "预计到账时间")
    private String estimatedTime;

    @Schema(description = "提示信息")
    private String message;
}
