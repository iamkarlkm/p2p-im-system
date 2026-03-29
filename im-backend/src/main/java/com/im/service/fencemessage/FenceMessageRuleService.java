package com.im.service.fencemessage;

import com.im.entity.fencemessage.FenceMessageRule;
import java.util.List;

/**
 * 场景化消息规则引擎服务接口
 */
public interface FenceMessageRuleService {
    
    /**
     * 创建规则
     */
    FenceMessageRule createRule(FenceMessageRule rule);
    
    /**
     * 更新规则
     */
    FenceMessageRule updateRule(String ruleId, FenceMessageRule rule);
    
    /**
     * 删除规则
     */
    void deleteRule(String ruleId);
    
    /**
     * 获取规则详情
     */
    FenceMessageRule getRule(String ruleId);
    
    /**
     * 获取围栏的所有规则
     */
    List<FenceMessageRule> getRulesByFence(String fenceId);
    
    /**
     * 匹配适用的规则
     */
    List<FenceMessageRule> matchRules(String userId, String fenceId, String scene);
    
    /**
     * 检查规则条件
     */
    boolean checkConditions(FenceMessageRule rule, String userId, String scene);
    
    /**
     * 启用规则
     */
    void enableRule(String ruleId);
    
    /**
     * 禁用规则
     */
    void disableRule(String ruleId);
}
