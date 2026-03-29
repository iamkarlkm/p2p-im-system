package com.im.service.fencemessage.impl;

import com.im.entity.fencemessage.FenceMessageRule;
import com.im.service.fencemessage.FenceMessageRuleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 场景化消息规则引擎服务实现
 */
@Service
public class FenceMessageRuleServiceImpl implements FenceMessageRuleService {
    
    private final Map<String, FenceMessageRule> ruleStore = new ConcurrentHashMap<>();
    
    @Override
    public FenceMessageRule createRule(FenceMessageRule rule) {
        rule.setRuleId(UUID.randomUUID().toString());
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        rule.setEnabled(true);
        ruleStore.put(rule.getRuleId(), rule);
        return rule;
    }
    
    @Override
    public FenceMessageRule updateRule(String ruleId, FenceMessageRule rule) {
        rule.setRuleId(ruleId);
        rule.setUpdateTime(LocalDateTime.now());
        ruleStore.put(ruleId, rule);
        return rule;
    }
    
    @Override
    public void deleteRule(String ruleId) {
        ruleStore.remove(ruleId);
    }
    
    @Override
    public FenceMessageRule getRule(String ruleId) {
        return ruleStore.get(ruleId);
    }
    
    @Override
    public List<FenceMessageRule> getRulesByFence(String fenceId) {
        return ruleStore.values().stream()
                .filter(r -> fenceId.equals(r.getFenceId()))
                .filter(r -> Boolean.TRUE.equals(r.getEnabled()))
                .sorted(Comparator.comparing(FenceMessageRule::getPriority))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FenceMessageRule> matchRules(String userId, String fenceId, String scene) {
        return getRulesByFence(fenceId).stream()
                .filter(r -> r.getScenes() != null && r.getScenes().contains(scene))
                .filter(r -> checkConditions(r, userId, scene))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean checkConditions(FenceMessageRule rule, String userId, String scene) {
        if (rule.getTriggerConditions() == null || rule.getTriggerConditions().isEmpty()) {
            return true;
        }
        
        // 简化的条件检查逻辑
        boolean allMatch = true;
        boolean anyMatch = false;
        
        for (Map<String, Object> condition : rule.getTriggerConditions()) {
            boolean matched = checkSingleCondition(condition, userId, scene);
            allMatch = allMatch && matched;
            anyMatch = anyMatch || matched;
        }
        
        return "AND".equals(rule.getConditionLogic()) ? allMatch : anyMatch;
    }
    
    /**
     * 检查单个条件
     */
    private boolean checkSingleCondition(Map<String, Object> condition, String userId, String scene) {
        String type = (String) condition.get("type");
        
        if ("TIME".equals(type)) {
            // 时间条件检查
            return checkTimeCondition(condition);
        } else if ("SCENE".equals(type)) {
            // 场景条件检查
            return scene.equals(condition.get("value"));
        }
        
        return true;
    }
    
    /**
     * 检查时间条件
     */
    private boolean checkTimeCondition(Map<String, Object> condition) {
        LocalDateTime now = LocalDateTime.now();
        String timeRange = (String) condition.get("timeRange");
        
        if (timeRange != null) {
            String[] parts = timeRange.split("-");
            if (parts.length == 2) {
                int currentHour = now.getHour();
                int startHour = Integer.parseInt(parts[0]);
                int endHour = Integer.parseInt(parts[1]);
                return currentHour >= startHour && currentHour <= endHour;
            }
        }
        return true;
    }
    
    @Override
    public void enableRule(String ruleId) {
        FenceMessageRule rule = ruleStore.get(ruleId);
        if (rule != null) {
            rule.setEnabled(true);
            rule.setUpdateTime(LocalDateTime.now());
        }
    }
    
    @Override
    public void disableRule(String ruleId) {
        FenceMessageRule rule = ruleStore.get(ruleId);
        if (rule != null) {
            rule.setEnabled(false);
            rule.setUpdateTime(LocalDateTime.now());
        }
    }
}
