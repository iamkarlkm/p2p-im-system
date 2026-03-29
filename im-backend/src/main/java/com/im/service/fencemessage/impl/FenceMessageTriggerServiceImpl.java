package com.im.service.fencemessage.impl;

import com.im.entity.fencemessage.FenceMessageTrigger;
import com.im.entity.fencemessage.FenceMessageRule;
import com.im.entity.fencemessage.FenceMessageTemplate;
import com.im.service.fencemessage.FenceMessageTriggerService;
import com.im.service.fencemessage.FenceMessageRuleService;
import com.im.service.fencemessage.FenceMessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 场景化消息触发引擎服务实现
 */
@Service
public class FenceMessageTriggerServiceImpl implements FenceMessageTriggerService {
    
    @Autowired
    private FenceMessageRuleService ruleService;
    
    @Autowired
    private FenceMessageTemplateService templateService;
    
    private final Map<String, FenceMessageTrigger> triggerStore = new ConcurrentHashMap<>();
    private final Map<String, List<LocalDateTime>> userTriggerHistory = new ConcurrentHashMap<>();
    
    @Override
    public void triggerEnterMessage(String userId, String fenceId, Double longitude, Double latitude) {
        // 1. 获取适用的规则
        List<FenceMessageRule> rules = ruleService.matchRules(userId, fenceId, "ENTER");
        
        // 2. 按优先级排序并执行
        rules.stream()
                .sorted(Comparator.comparing(FenceMessageRule::getPriority))
                .forEach(rule -> executeRuleTrigger(userId, fenceId, rule, "ENTER", 
                        buildVariables(userId, fenceId, longitude, latitude)));
    }
    
    @Override
    public void triggerDwellMessage(String userId, String fenceId, Integer dwellMinutes) {
        List<FenceMessageRule> rules = ruleService.matchRules(userId, fenceId, "DWELL");
        
        rules.stream()
                .sorted(Comparator.comparing(FenceMessageRule::getPriority))
                .forEach(rule -> {
                    Map<String, String> variables = new HashMap<>();
                    variables.put("dwellMinutes", String.valueOf(dwellMinutes));
                    executeRuleTrigger(userId, fenceId, rule, "DWELL", variables);
                });
    }
    
    @Override
    public void triggerExitMessage(String userId, String fenceId) {
        List<FenceMessageRule> rules = ruleService.matchRules(userId, fenceId, "EXIT");
        
        rules.stream()
                .sorted(Comparator.comparing(FenceMessageRule::getPriority))
                .forEach(rule -> executeRuleTrigger(userId, fenceId, rule, "EXIT", new HashMap<>()));
    }
    
    /**
     * 执行规则触发
     */
    private void executeRuleTrigger(String userId, String fenceId, FenceMessageRule rule, 
                                     String scene, Map<String, String> variables) {
        // 检查频次限制
        if (!checkFrequencyLimit(userId, rule.getRuleId(), rule)) {
            return;
        }
        
        // 获取模板
        FenceMessageTemplate template = templateService.getTemplate(rule.getTemplateId());
        if (template == null || !Boolean.TRUE.equals(template.getEnabled())) {
            return;
        }
        
        // 检查去重
        if (checkDuplicate(userId, fenceId, template.getTemplateId(), template.getDedupWindowMinutes())) {
            return;
        }
        
        // 执行触发
        executeTrigger(userId, fenceId, template.getTemplateId(), scene, variables);
    }
    
    /**
     * 构建消息变量
     */
    private Map<String, String> buildVariables(String userId, String fenceId, 
                                                Double longitude, Double latitude) {
        Map<String, String> variables = new HashMap<>();
        variables.put("userId", userId);
        variables.put("fenceId", fenceId);
        variables.put("longitude", String.valueOf(longitude));
        variables.put("latitude", String.valueOf(latitude));
        variables.put("triggerTime", LocalDateTime.now().toString());
        return variables;
    }
    
    @Override
    public FenceMessageTrigger executeTrigger(String userId, String fenceId, String templateId, 
                                               String scene, Map<String, String> variables) {
        FenceMessageTemplate template = templateService.getTemplate(templateId);
        if (template == null) return null;
        
        // 渲染消息内容
        String title = templateService.renderMessage(template.getTitleTemplate(), variables);
        String content = templateService.renderMessage(template.getContentTemplate(), variables);
        
        // 创建触发记录
        FenceMessageTrigger trigger = new FenceMessageTrigger();
        trigger.setTriggerId(UUID.randomUUID().toString());
        trigger.setFenceId(fenceId);
        trigger.setTemplateId(templateId);
        trigger.setUserId(userId);
        trigger.setTriggerScene(scene);
        trigger.setMessageTitle(title);
        trigger.setMessageContent(content);
        trigger.setPushChannel(template.getPushChannel());
        trigger.setTriggerTime(LocalDateTime.now());
        trigger.setSendStatus("PENDING");
        trigger.setDedupKey(buildDedupKey(userId, fenceId, templateId));
        trigger.setCreateTime(LocalDateTime.now());
        
        // 保存记录
        triggerStore.put(trigger.getTriggerId(), trigger);
        
        // 记录触发历史
        recordTriggerHistory(userId, ruleService.getRulesByFence(fenceId).stream()
                .filter(r -> templateId.equals(r.getTemplateId()))
                .findFirst().map(FenceMessageRule::getRuleId).orElse(""));
        
        // 发送消息
        sendMessage(trigger);
        
        return trigger;
    }
    
    /**
     * 发送消息
     */
    private void sendMessage(FenceMessageTrigger trigger) {
        // 模拟发送
        trigger.setSendStatus("SENT");
        trigger.setSendTime(LocalDateTime.now());
        triggerStore.put(trigger.getTriggerId(), trigger);
        
        System.out.println("[场景消息] 发送给 " + trigger.getUserId() + ": " + trigger.getMessageContent());
    }
    
    /**
     * 构建去重Key
     */
    private String buildDedupKey(String userId, String fenceId, String templateId) {
        return userId + ":" + fenceId + ":" + templateId;
    }
    
    @Override
    public boolean checkDuplicate(String userId, String fenceId, String templateId, Integer windowMinutes) {
        if (windowMinutes == null || windowMinutes <= 0) return false;
        
        String dedupKey = buildDedupKey(userId, fenceId, templateId);
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(windowMinutes);
        
        return triggerStore.values().stream()
                .anyMatch(t -> dedupKey.equals(t.getDedupKey()) 
                        && !windowStart.isAfter(t.getTriggerTime())
                        && !"FAILED".equals(t.getSendStatus()));
    }
    
    @Override
    public boolean checkFrequencyLimit(String userId, String ruleId, FenceMessageRule rule) {
        List<LocalDateTime> history = userTriggerHistory.getOrDefault(userId + ":" + ruleId, new ArrayList<>());
        LocalDateTime now = LocalDateTime.now();
        
        // 清理过期记录
        history.removeIf(t -> t.isBefore(now.minusDays(30)));
        
        // 检查日限制
        if (rule.getDailyLimit() != null && rule.getDailyLimit() > 0) {
            long todayCount = history.stream()
                    .filter(t -> t.isAfter(now.minusDays(1)))
                    .count();
            if (todayCount >= rule.getDailyLimit()) return false;
        }
        
        // 检查冷却时间
        if (rule.getCooldownMinutes() != null && rule.getCooldownMinutes() > 0) {
            Optional<LocalDateTime> lastTrigger = history.stream().max(Comparator.naturalOrder());
            if (lastTrigger.isPresent()) {
                long minutesSince = ChronoUnit.MINUTES.between(lastTrigger.get(), now);
                if (minutesSince < rule.getCooldownMinutes()) return false;
            }
        }
        
        return true;
    }
    
    /**
     * 记录触发历史
     */
    private void recordTriggerHistory(String userId, String ruleId) {
        String key = userId + ":" + ruleId;
        userTriggerHistory.computeIfAbsent(key, k -> new ArrayList<>()).add(LocalDateTime.now());
    }
    
    @Override
    public List<FenceMessageTrigger> getUserTriggers(String userId, Integer limit) {
        return triggerStore.values().stream()
                .filter(t -> userId.equals(t.getUserId()))
                .sorted((a, b) -> b.getTriggerTime().compareTo(a.getTriggerTime()))
                .limit(limit != null ? limit : 50)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getFenceTriggerStats(String fenceId, String startDate, String endDate) {
        List<FenceMessageTrigger> triggers = triggerStore.values().stream()
                .filter(t -> fenceId.equals(t.getFenceId()))
                .collect(Collectors.toList());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTriggers", triggers.size());
        stats.put("sentCount", triggers.stream().filter(t -> "SENT".equals(t.getSendStatus())).count());
        stats.put("readCount", triggers.stream().filter(t -> "READ".equals(t.getSendStatus())).count());
        stats.put("failedCount", triggers.stream().filter(t -> "FAILED".equals(t.getSendStatus())).count());
        stats.put("fenceId", fenceId);
        
        return stats;
    }
    
    @Override
    public void retryFailedMessage(String triggerId) {
        FenceMessageTrigger trigger = triggerStore.get(triggerId);
        if (trigger != null && "FAILED".equals(trigger.getSendStatus())) {
            trigger.setSendStatus("PENDING");
            trigger.setFailReason(null);
            sendMessage(trigger);
        }
    }
}
