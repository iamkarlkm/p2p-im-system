package com.im.entity.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服消息实体
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CustomerServiceMessage {
    
    /** 消息ID */
    private Long id;
    
    /** 会话ID */
    private Long sessionId;
    
    /** 发送者类型：1-用户 2-客服 3-机器人 */
    private Integer senderType;
    
    /** 发送者ID */
    private Long senderId;
    
    /** 消息类型：1-文本 2-图片 3-语音 4-表情 5-卡片 */
    private Integer messageType;
    
    /** 消息内容 */
    private String content;
    
    /** 媒体URL */
    private String mediaUrl;
    
    /** 消息状态：0-发送中 1-已发送 2-已读 3-失败 */
    private Integer status;
    
    /** 是否撤回 */
    private Integer recalled;
    
    /** 撤回时间 */
    private LocalDateTime recallTime;
    
    /** 引用消息ID */
    private Long replyToMessageId;
    
    /** 消息元数据（JSON） */
    private String metadata;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 是否删除 */
    private Integer deleted;
}
