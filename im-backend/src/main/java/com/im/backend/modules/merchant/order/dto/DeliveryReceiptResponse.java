package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 配送签收响应
 */
@Data
public class DeliveryReceiptResponse {

    /**
     * 签收ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 签收码
     */
    private String receiptCode;

    /**
     * 签收方式
     */
    private Integer receiptType;

    /**
     * 签收方式描述
     */
    private String receiptTypeDesc;

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

    /**
     * 签收状态
     */
    private Integer status;

    /**
     * 签收状态描述
     */
    private String statusDesc;

    /**
     * 签收时间
     */
    private LocalDateTime receiptTime;

    /**
     * 送达地址
     */
    private String deliveryAddress;
}
