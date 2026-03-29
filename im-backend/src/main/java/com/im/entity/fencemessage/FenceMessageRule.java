package com.im.entity.fencemessage;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 场景化消息规则引擎配置实体
 * 定义复杂的消息触发规则组合
 */
@Data
public class FenceMessageRule {
    
    /** 规则ID */
    private String ruleId;
    
    /** 规则名称 */
    private String ruleName;
    
    /** 关联围栏ID */
    private String fenceId;
    
    /** 规则优先级(数字越小优先级越高) */
    private Integer priority;
    
    /** 触发条件组合 */
    private List<Map<String, Object>> triggerConditions;
    
    /** 条件逻辑: AND-全部满足, OR-任一满足 */
    private String conditionLogic;
    
    /** 触发场景列表: ["ENTER", "DWELL", "EXIT"] */
    private List<String> scenes;
    
    /** 用户标签条件 */
    private List<String> userTagConditions;
    
    /** 时间条件: 生效时段 */
    private Map<String, Object> timeConditions;
    
    /** 频次限制: 每日最多触发次数 */
    private Integer dailyLimit;
    
    /** 频次限制: 每周最多触发次数 */
    private Integer weeklyLimit;
    
    /** 频次限制: 每月最多触发次数 */
    private Integer monthlyLimit;
    
    /** 冷却时间(分钟) */
    private Integer cooldownMinutes;
    
    /** 关联的消息模板ID */
    private String templateId;
    
    /** 消息变量填充规则 */
    private Map<String, String> variableRules;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
