package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 发送履约消息请求
 */
@Data
public class SendFulfillmentMessageRequest {

    /**
     * 会话ID
     */
    @NotNull(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 消息类型: 1-系统消息, 2-文本消息, 3-图片消息, 4-位置消息, 5-语音消息
     */
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息扩展数据(JSON)
     */
    private String extraData;
}
