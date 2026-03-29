package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服知识库条目
 */
@Data
@TableName("chatbot_knowledge")
public class ChatbotKnowledge {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 问题类型: FAQ-常见问题, INTENT-意图识别, ENTITY-实体提取
     */
    private String knowledgeType;
    
    /**
     * 标准问题
     */
    private String question;
    
    /**
     * 相似问题JSON数组
     */
    private String similarQuestions;
    
    /**
     * 标准答案
     */
    private String answer;
    
    /**
     * 关键词JSON数组
     */
    private String keywords;
    
    /**
     * 意图标签
     */
    private String intentTag;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 命中次数
     */
    private Integer hitCount;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
