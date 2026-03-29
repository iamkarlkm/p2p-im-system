package com.im.backend.modules.poi.customer_service.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送消息请求
 */
@Data
public class SendMessageRequest {

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 消息类型
     */
    @NotBlank(message = "消息类型不能为空")
    private String messageType;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 额外内容(JSON)
     */
    private String contentExtra;

    /**
     * 引用消息ID
     */
    private String quoteMessageId;

    /**
     * 客户端消息ID(幂等)
     */
    private String clientMessageId;
}
