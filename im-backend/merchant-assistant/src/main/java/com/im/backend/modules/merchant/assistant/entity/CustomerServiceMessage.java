package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服消息记录
 */
@Data
@TableName("customer_service_message")
public class CustomerServiceMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 发送者类型: USER-用户, BOT-机器人, AGENT-人工客服, SYSTEM-系统
     */
    private String senderType;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 消息类型: TEXT-文本, IMAGE-图片, VOICE-语音, TEMPLATE-模板消息
     */
    private String messageType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 富媒体内容JSON
     */
    private String mediaContent;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 意图识别结果
     */
    private String intentResult;
    
    /**
     * 置信度
     */
    private Double confidence;
    
    /**
     * 引用的知识库ID
     */
    private Long knowledgeId;
    
    private LocalDateTime createTime;
}
