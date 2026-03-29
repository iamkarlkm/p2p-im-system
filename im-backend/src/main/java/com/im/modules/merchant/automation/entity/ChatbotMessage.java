package com.im.modules.merchant.automation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 智能客服消息实体
 * 存储会话中的每条消息
 */
@Data
@TableName("chatbot_message")
public class ChatbotMessage {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String messageId;
    
    private String sessionId;
    
    private String merchantId;
    
    private String userId;
    
    private String senderType;
    
    private String senderId;
    
    private String content;
    
    private String messageType;
    
    private String mediaUrl;
    
    private String intent;
    
    private Double confidence;
    
    private String replyToMessageId;
    
    private Boolean isRead;
    
    private LocalDateTime readTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Boolean deleted;
}
