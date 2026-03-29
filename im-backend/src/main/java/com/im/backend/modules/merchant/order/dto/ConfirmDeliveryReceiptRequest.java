package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 * 确认签收请求
 */
@Data
public class ConfirmDeliveryReceiptRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 签收码
     */
    private String receiptCode;

    /**
     * 签收方式: 1-签收码验证, 2-拍照确认, 3-直接签收, 4-寄存/放门口
     */
    @NotNull(message = "签收方式不能为空")
    private Integer receiptType;

    /**
     * 签收人姓名
     */
    private String recipientName;

    /**
     * 签收人电话
     */
    private String recipientPhone;

    /**
     * 签收照片URL
     */
    private String receiptPhotoUrl;

    /**
     * 签收备注
     */
    private String remark;
}
