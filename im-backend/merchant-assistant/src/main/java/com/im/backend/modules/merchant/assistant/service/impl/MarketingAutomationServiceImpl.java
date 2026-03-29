package com.im.backend.modules.merchant.assistant.service.impl;

import com.im.backend.modules.merchant.assistant.dto.CreateMarketingRuleRequest;
import com.im.backend.modules.merchant.assistant.dto.MarketingRuleResponse;
import com.im.backend.modules.merchant.assistant.entity.MarketingAutomationRule;
import com.im.backend.modules.merchant.assistant.repository.MarketingAutomationRuleMapper;
import com.im.backend.modules.merchant.assistant.service.IMarketingAutomationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 营销自动化服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingAutomationServiceImpl implements IMarketingAutomationService {
    
    private final MarketingAutomationRuleMapper ruleMapper;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public MarketingRuleResponse createRule(CreateMarketingRuleRequest request) {
        MarketingAutomationRule rule = new MarketingAutomationRule();
        rule.setMerchantId(request.getMerchantId());
        rule.setRuleName(request.getRuleName());
        rule.setRuleType(request.getRuleType());
        
        try {
            rule.setTriggerCondition(objectMapper.writeValueAsString(request.getTriggerCondition()));
            rule.setTargetAudience(objectMapper.writeValueAsString(request.getTargetAudience()));
        } catch (Exception e) {
            log.error("序列化规则配置失败", e);
        }
        
        rule.setTemplateId(request.getTemplateId());
        rule.setPriority(0);
        rule.setCooldownMinutes(request.getCooldownMinutes());
        rule.setDailyLimit(request.getDailyLimit());
        rule.setStartTime(request.getStartTime());
        rule.setEndTime(request.getEndTime());
        rule.setEnabled(true);
        rule.setTriggerCount(0);
        rule.setConversionCount(0);
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        
        ruleMapper.insert(rule);
        
        return convertToResponse(rule);
    }
    
    @Override
    @Transactional
    public void updateRule(Long ruleId, CreateMarketingRuleRequest request) {
        MarketingAutomationRule rule = ruleMapper.selectById(ruleId);
        if (rule == null) {
            return;
        }
        
        rule.setRuleName(request.getRuleName());
        rule.setRuleType(request.getRuleType());
        
        try {
            rule.setTriggerCondition(objectMapper.writeValueAsString(request.getTriggerCondition()));
            rule.setTargetAudience(objectMapper.writeValueAsString(request.getTargetAudience()));
        } catch (Exception e) {
            log.error("序列化规则配置失败", e);
        }
        
        rule.setTemplateId(request.getTemplateId());
        rule.setCooldownMinutes(request.getCooldownMinutes());
        rule.setDailyLimit(request.getDailyLimit());
        rule.setStartTime(request.getStartTime());
        rule.setEndTime(request.getEndTime());
        rule.setUpdateTime(LocalDateTime.now());
        
        ruleMapper.updateById(rule);
    }
    
    @Override
    @Transactional
    public void toggleRuleStatus(Long ruleId, Boolean enabled) {
        MarketingAutomationRule rule = ruleMapper.selectById(ruleId);
        if (rule != null) {
            rule.setEnabled(enabled);
            rule.setUpdateTime(LocalDateTime.now());
            ruleMapper.updateById(rule);
        }
    }
    
    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        ruleMapper.deleteById(ruleId);
    }
    
    @Override
    public List<MarketingRuleResponse> getMerchantRules(Long merchantId) {
        List<MarketingAutomationRule> rules = ruleMapper.selectEnabledRules(merchantId);
        return rules.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void triggerRule(Long ruleId, Long userId, String triggerData) {
        MarketingAutomationRule rule = ruleMapper.selectById(ruleId);
        if (rule == null || !rule.getEnabled()) {
            return;
        }
        
        // 检查时间范围
        LocalDateTime now = LocalDateTime.now();
        if (rule.getStartTime() != null && now.isBefore(rule.getStartTime())) {
            return;
        }
        if (rule.getEndTime() != null && now.isAfter(rule.getEndTime())) {
            return;
        }
        
        // 增加触发次数
        ruleMapper.incrementTriggerCount(ruleId);
        
        log.info("营销规则触发: ruleId={}, userId={}, triggerData={}", ruleId, userId, triggerData);
        
        // TODO: 发送营销消息
    }
    
    @Override
    public void handleGeofenceTrigger(Long merchantId, Long userId, String geofenceId, String eventType) {
        // 查询地理围栏类型的规则
        List<MarketingAutomationRule> rules = ruleMapper.selectByRuleType(merchantId, "GEO_FENCE");
        
        for (MarketingAutomationRule rule : rules) {
            // 检查触发条件
            if (isGeofenceConditionMatch(rule, geofenceId, eventType)) {
                triggerRule(rule.getId(), userId, "geofence:" + geofenceId + ":" + eventType);
            }
        }
    }
    
    @Override
    public void handleBehaviorTrigger(Long merchantId, Long userId, String behaviorType, Object behaviorData) {
        // 查询行为触发类型的规则
        List<MarketingAutomationRule> rules = ruleMapper.selectByRuleType(merchantId, "BEHAVIOR");
        
        for (MarketingAutomationRule rule : rules) {
            if (isBehaviorConditionMatch(rule, behaviorType, behaviorData)) {
                triggerRule(rule.getId(), userId, "behavior:" + behaviorType);
            }
        }
    }
    
    // ============ 私有方法 ============
    
    private boolean isGeofenceConditionMatch(MarketingAutomationRule rule, String geofenceId, String eventType) {
        try {
            String triggerCondition = rule.getTriggerCondition();
            if (triggerCondition == null) {
                return false;
            }
            
            // 简单解析触发条件
            return triggerCondition.contains(geofenceId) && triggerCondition.contains(eventType);
        } catch (Exception e) {
            log.error("解析地理围栏触发条件失败", e);
            return false;
        }
    }
    
    private boolean isBehaviorConditionMatch(MarketingAutomationRule rule, String behaviorType, Object behaviorData) {
        try {
            String triggerCondition = rule.getTriggerCondition();
            if (triggerCondition == null) {
                return false;
            }
            
            return triggerCondition.contains(behaviorType);
        } catch (Exception e) {
            log.error("解析行为触发条件失败", e);
            return false;
        }
    }
    
    private MarketingRuleResponse convertToResponse(MarketingAutomationRule rule) {
        MarketingRuleResponse response = new MarketingRuleResponse();
        response.setId(rule.getId());
        response.setRuleName(rule.getRuleName());
        response.setRuleType(rule.getRuleType());
        response.setTemplateId(rule.getTemplateId());
        response.setPriority(rule.getPriority());
        response.setEnabled(rule.getEnabled());
        response.setTriggerCount(rule.getTriggerCount());
        response.setConversionCount(rule.getConversionCount());
        response.setCreateTime(rule.getCreateTime());
        return response;
    }
}
