package com.im.backend.modules.merchant.order.service;

import com.im.backend.modules.merchant.order.dto.*;

/**
 * 订单配送签收服务接口
 */
public interface IOrderDeliveryReceiptService {

    /**
     * 生成签收码
     */
    String generateReceiptCode(Long orderId);

    /**
     * 验证签收码
     */
    boolean verifyReceiptCode(Long orderId, String receiptCode);

    /**
     * 确认签收
     */
    DeliveryReceiptResponse confirmReceipt(ConfirmDeliveryReceiptRequest request, Long userId);

    /**
     * 获取签收信息
     */
    DeliveryReceiptResponse getReceiptInfo(Long orderId);

    /**
     * 拍照签收
     */
    DeliveryReceiptResponse photoReceipt(Long orderId, String photoUrl, String remark);

    /**
     * 直接签收(无码)
     */
    DeliveryReceiptResponse directReceipt(Long orderId, Long userId);
}
