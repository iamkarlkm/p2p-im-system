package com.im.controller.fencemessage;

import com.im.common.Result;
import com.im.entity.fencemessage.FenceMessageTemplate;
import com.im.entity.fencemessage.FenceMessageTrigger;
import com.im.entity.fencemessage.FenceMessageRule;
import com.im.service.fencemessage.FenceMessageTemplateService;
import com.im.service.fencemessage.FenceMessageTriggerService;
import com.im.service.fencemessage.FenceMessageRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 场景化消息触发引擎控制器
 */
@RestController
@RequestMapping("/api/v1/fence-message")
public class FenceMessageController {
    
    @Autowired
    private FenceMessageTemplateService templateService;
    
    @Autowired
    private FenceMessageTriggerService triggerService;
    
    @Autowired
    private FenceMessageRuleService ruleService;
    
    // ========== 模板管理 ==========
    
    @PostMapping("/templates")
    public Result<FenceMessageTemplate> createTemplate(@RequestBody FenceMessageTemplate template) {
        return Result.success(templateService.createTemplate(template));
    }
    
    @PutMapping("/templates/{templateId}")
    public Result<FenceMessageTemplate> updateTemplate(
            @PathVariable String templateId,
            @RequestBody FenceMessageTemplate template) {
        return Result.success(templateService.updateTemplate(templateId, template));
    }
    
    @DeleteMapping("/templates/{templateId}")
    public Result<Void> deleteTemplate(@PathVariable String templateId) {
        templateService.deleteTemplate(templateId);
        return Result.success();
    }
    
    @GetMapping("/templates/{templateId}")
    public Result<FenceMessageTemplate> getTemplate(@PathVariable String templateId) {
        return Result.success(templateService.getTemplate(templateId));
    }
    
    @GetMapping("/templates")
    public Result<List<FenceMessageTemplate>> getAllTemplates() {
        return Result.success(templateService.getAllTemplates());
    }
    
    @PostMapping("/templates/{templateId}/enable")
    public Result<Void> enableTemplate(@PathVariable String templateId) {
        templateService.enableTemplate(templateId);
        return Result.success();
    }
    
    @PostMapping("/templates/{templateId}/disable")
    public Result<Void> disableTemplate(@PathVariable String templateId) {
        templateService.disableTemplate(templateId);
        return Result.success();
    }
    
    // ========== 规则管理 ==========
    
    @PostMapping("/rules")
    public Result<FenceMessageRule> createRule(@RequestBody FenceMessageRule rule) {
        return Result.success(ruleService.createRule(rule));
    }
    
    @PutMapping("/rules/{ruleId}")
    public Result<FenceMessageRule> updateRule(
            @PathVariable String ruleId,
            @RequestBody FenceMessageRule rule) {
        return Result.success(ruleService.updateRule(ruleId, rule));
    }
    
    @DeleteMapping("/rules/{ruleId}")
    public Result<Void> deleteRule(@PathVariable String ruleId) {
        ruleService.deleteRule(ruleId);
        return Result.success();
    }
    
    @GetMapping("/rules/{ruleId}")
    public Result<FenceMessageRule> getRule(@PathVariable String ruleId) {
        return Result.success(ruleService.getRule(ruleId));
    }
    
    @GetMapping("/fences/{fenceId}/rules")
    public Result<List<FenceMessageRule>> getFenceRules(@PathVariable String fenceId) {
        return Result.success(ruleService.getRulesByFence(fenceId));
    }
    
    // ========== 触发记录 ==========
    
    @GetMapping("/users/{userId}/triggers")
    public Result<List<FenceMessageTrigger>> getUserTriggers(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return Result.success(triggerService.getUserTriggers(userId, limit));
    }
    
    @GetMapping("/fences/{fenceId}/stats")
    public Result<Map<String, Object>> getFenceStats(
            @PathVariable String fenceId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(triggerService.getFenceTriggerStats(fenceId, startDate, endDate));
    }
    
    @PostMapping("/triggers/{triggerId}/retry")
    public Result<Void> retryFailedMessage(@PathVariable String triggerId) {
        triggerService.retryFailedMessage(triggerId);
        return Result.success();
    }
    
    // ========== 手动触发(测试用) ==========
    
    @PostMapping("/trigger/enter")
    public Result<Void> triggerEnter(
            @RequestParam String userId,
            @RequestParam String fenceId,
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        triggerService.triggerEnterMessage(userId, fenceId, longitude, latitude);
        return Result.success();
    }
    
    @PostMapping("/trigger/dwell")
    public Result<Void> triggerDwell(
            @RequestParam String userId,
            @RequestParam String fenceId,
            @RequestParam Integer dwellMinutes) {
        triggerService.triggerDwellMessage(userId, fenceId, dwellMinutes);
        return Result.success();
    }
    
    @PostMapping("/trigger/exit")
    public Result<Void> triggerExit(
            @RequestParam String userId,
            @RequestParam String fenceId) {
        triggerService.triggerExitMessage(userId, fenceId);
        return Result.success();
    }
}
