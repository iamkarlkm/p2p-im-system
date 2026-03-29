package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 营销规则响应
 */
@Data
public class MarketingRuleResponse {
    
    /**
     * 规则ID
     */
    private Long id;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 规则类型
     */
    private String ruleType;
    
    /**
     * 消息模板ID
     */
    private Long templateId;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 触发次数
     */
    private Integer triggerCount;
    
    /**
     * 转化次数
     */
    private Integer conversionCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
