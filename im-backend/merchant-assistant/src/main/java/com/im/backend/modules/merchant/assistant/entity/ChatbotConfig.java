package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 智能客服机器人配置
 */
@Data
@TableName("chatbot_config")
public class ChatbotConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 机器人名称
     */
    private String botName;
    
    /**
     * 欢迎语
     */
    private String welcomeMessage;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 自动回复模式: AUTO-全自动, SEMI-半自动(辅助人工)
     */
    private String replyMode;
    
    /**
     * 转人工阈值(置信度低于此值转人工)
     */
    private Double transferThreshold;
    
    /**
     * 工作时间JSON配置
     */
    private String workHoursConfig;
    
    /**
     * 非工作时间回复语
     */
    private String offlineReply;
    
    /**
     * 知识库版本号
     */
    private Integer knowledgeVersion;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
