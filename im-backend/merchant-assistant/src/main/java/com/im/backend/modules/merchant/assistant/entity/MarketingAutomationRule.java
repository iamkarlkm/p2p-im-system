package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 自动化营销规则
 */
@Data
@TableName("marketing_automation_rule")
public class MarketingAutomationRule {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 规则类型: GEO_FENCE-地理围栏, BEHAVIOR-行为触发, TIME-定时触发, EVENT-事件触发
     */
    private String ruleType;
    
    /**
     * 触发条件JSON配置
     */
    private String triggerCondition;
    
    /**
     * 触发动作JSON配置
     */
    private String actionConfig;
    
    /**
     * 消息模板ID
     */
    private Long templateId;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 冷却时间(分钟,同一用户触发间隔)
     */
    private Integer cooldownMinutes;
    
    /**
     * 每日上限
     */
    private Integer dailyLimit;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 目标人群JSON配置
     */
    private String targetAudience;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 触发次数统计
     */
    private Integer triggerCount;
    
    /**
     * 转化次数统计
     */
    private Integer conversionCount;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
