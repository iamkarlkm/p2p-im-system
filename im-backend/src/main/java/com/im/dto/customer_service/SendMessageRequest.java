package com.im.dto.customer_service;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送消息请求DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class SendMessageRequest {
    
    /** 会话ID */
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;
    
    /** 发送者类型：1-用户 2-客服 3-机器人 */
    @NotNull(message = "发送者类型不能为空")
    private Integer senderType;
    
    /** 发送者ID */
    @NotNull(message = "发送者ID不能为空")
    private Long senderId;
    
    /** 消息类型：1-文本 2-图片 3-语音 4-表情 5-卡片 */
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;
    
    /** 消息内容 */
    @NotBlank(message = "消息内容不能为空")
    private String content;
    
    /** 媒体URL */
    private String mediaUrl;
    
    /** 引用消息ID */
    private Long replyToMessageId;
    
    /** 消息元数据 */
    private String metadata;
}
