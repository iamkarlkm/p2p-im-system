package com.im.backend.modules.merchant.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单履约消息记录实体
 * 记录订单履约过程中发送的IM消息
 */
@Data
@TableName("im_order_fulfillment_message")
public class OrderFulfillmentMessage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 消息类型: 1-系统消息, 2-文本消息, 3-图片消息, 4-位置消息, 5-语音消息
     */
    private Integer messageType;

    /**
     * 消息子类型(系统消息细分)
     */
    private Integer messageSubType;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者类型: 1-用户, 2-商户, 3-骑手, 4-系统
     */
    private Integer senderType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息扩展数据(JSON)
     */
    private String extraData;

    /**
     * 关联状态变更
     */
    private Integer relatedStatusChange;

    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer readStatus;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;
}
