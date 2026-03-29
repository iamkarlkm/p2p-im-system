package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 确认出餐请求
 */
@Data
public class ConfirmMealReadyRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 操作员工ID
     */
    private Long operatorId;

    /**
     * 出餐照片URL(多图逗号分隔)
     */
    private String mealPhotos;

    /**
     * 出餐备注
     */
    private String remark;

    /**
     * 预计等待时间(分钟)
     */
    private Integer estimatedWaitMinutes;
}
