package com.im.backend.modules.merchant.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单履约IM会话实体
 * 管理用户-商户-骑手三方IM会话
 */
@Data
@TableName("im_order_fulfillment_session")
public class OrderFulfillmentSession {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话ID (order_{merchantId}_{orderId}_{timestamp})
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
     * 用户ID
     */
    private Long userId;

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * IM群组ID
     */
    private Long groupId;

    /**
     * 会话状态: 0-活跃, 1-暂停, 2-已结束
     */
    private Integer status;

    /**
     * 会话创建时间
     */
    private LocalDateTime createTime;

    /**
     * 会话结束时间
     */
    private LocalDateTime endTime;

    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedDeliveryTime;

    /**
     * 实际送达时间
     */
    private LocalDateTime actualDeliveryTime;

    /**
     * 创建者ID
     */
    private Long createBy;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
