package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单履约消息响应
 */
@Data
public class FulfillmentMessageResponse {

    /**
     * 消息ID
     */
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
     * 消息类型
     */
    private Integer messageType;

    /**
     * 消息类型描述
     */
    private String messageTypeDesc;

    /**
     * 消息子类型
     */
    private Integer messageSubType;

    /**
     * 消息子类型描述
     */
    private String messageSubTypeDesc;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者类型
     */
    private Integer senderType;

    /**
     * 发送者类型描述
     */
    private String senderTypeDesc;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息扩展数据
     */
    private String extraData;

    /**
     * 是否已读
     */
    private Integer readStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
