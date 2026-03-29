package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建营销规则请求
 */
@Data
public class CreateMarketingRuleRequest {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 规则类型
     */
    private String ruleType;
    
    /**
     * 触发条件
     */
    private TriggerCondition triggerCondition;
    
    /**
     * 消息模板ID
     */
    private Long templateId;
    
    /**
     * 目标人群
     */
    private TargetAudience targetAudience;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 冷却时间(分钟)
     */
    private Integer cooldownMinutes;
    
    /**
     * 每日上限
     */
    private Integer dailyLimit;
    
    @Data
    public static class TriggerCondition {
        private String type;
        private String config;
    }
    
    @Data
    public static class TargetAudience {
        private List<String> tags;
        private String membershipLevel;
        private Integer minOrderCount;
        private Integer maxDaysSinceLastOrder;
    }
}
