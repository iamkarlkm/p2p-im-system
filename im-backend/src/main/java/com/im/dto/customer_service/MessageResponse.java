package com.im.dto.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息响应DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class MessageResponse {
    
    /** 消息ID */
    private Long id;
    
    /** 会话ID */
    private Long sessionId;
    
    /** 发送者类型 */
    private Integer senderType;
    
    /** 发送者类型名称 */
    private String senderTypeName;
    
    /** 发送者ID */
    private Long senderId;
    
    /** 发送者昵称 */
    private String senderName;
    
    /** 发送者头像 */
    private String senderAvatar;
    
    /** 消息类型 */
    private Integer messageType;
    
    /** 消息类型名称 */
    private String messageTypeName;
    
    /** 消息内容 */
    private String content;
    
    /** 媒体URL */
    private String mediaUrl;
    
    /** 消息状态 */
    private Integer status;
    
    /** 是否撤回 */
    private Integer recalled;
    
    /** 引用消息ID */
    private Long replyToMessageId;
    
    /** 引用消息内容 */
    private String replyToContent;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
