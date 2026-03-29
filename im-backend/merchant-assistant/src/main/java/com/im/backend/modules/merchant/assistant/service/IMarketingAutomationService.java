package com.im.backend.modules.merchant.assistant.service;

import com.im.backend.modules.merchant.assistant.dto.*;

import java.util.List;

/**
 * 营销自动化服务接口
 */
public interface IMarketingAutomationService {
    
    /**
     * 创建营销规则
     */
    MarketingRuleResponse createRule(CreateMarketingRuleRequest request);
    
    /**
     * 更新营销规则
     */
    void updateRule(Long ruleId, CreateMarketingRuleRequest request);
    
    /**
     * 启用/禁用规则
     */
    void toggleRuleStatus(Long ruleId, Boolean enabled);
    
    /**
     * 删除营销规则
     */
    void deleteRule(Long ruleId);
    
    /**
     * 获取商户的营销规则列表
     */
    List<MarketingRuleResponse> getMerchantRules(Long merchantId);
    
    /**
     * 触发营销规则
     */
    void triggerRule(Long ruleId, Long userId, String triggerData);
    
    /**
     * 处理地理围栏触发
     */
    void handleGeofenceTrigger(Long merchantId, Long userId, String geofenceId, String eventType);
    
    /**
     * 处理用户行为触发
     */
    void handleBehaviorTrigger(Long merchantId, Long userId, String behaviorType, Object behaviorData);
}
