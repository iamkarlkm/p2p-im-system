package com.im.modules.merchant.automation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 智能客服会话实体
 * 存储客服会话的基本信息
 */
@Data
@TableName("chatbot_session")
public class ChatbotSession {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String sessionId;
    
    private String merchantId;
    
    private String userId;
    
    private String agentId;
    
    private Integer status;
    
    private String intent;
    
    private Double confidence;
    
    private Boolean needTransfer;
    
    private String transferReason;
    
    private LocalDateTime transferTime;
    
    private Integer messageCount;
    
    private String context;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Boolean deleted;
}
