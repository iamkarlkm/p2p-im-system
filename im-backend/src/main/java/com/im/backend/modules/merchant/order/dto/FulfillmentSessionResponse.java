package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单履约会话响应
 */
@Data
public class FulfillmentSessionResponse {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * 骑手姓名
     */
    private String riderName;

    /**
     * 骑手电话
     */
    private String riderPhone;

    /**
     * IM群组ID
     */
    private Long groupId;

    /**
     * 会话状态: 0-活跃, 1-暂停, 2-已结束
     */
    private Integer status;

    /**
     * 会话状态描述
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedDeliveryTime;

    /**
     * 实际送达时间
     */
    private LocalDateTime actualDeliveryTime;
}
