package com.im.backend.modules.merchant.assistant.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.merchant.assistant.dto.CreateMarketingRuleRequest;
import com.im.backend.modules.merchant.assistant.dto.MarketingRuleResponse;
import com.im.backend.modules.merchant.assistant.service.IMarketingAutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 营销自动化控制器
 */
@RestController
@RequestMapping("/api/v1/assistant/marketing")
@RequiredArgsConstructor
public class MarketingAutomationController {
    
    private final IMarketingAutomationService marketingService;
    
    /**
     * 创建营销规则
     */
    @PostMapping("/rule/create")
    public Result<MarketingRuleResponse> createRule(@RequestBody CreateMarketingRuleRequest request) {
        MarketingRuleResponse response = marketingService.createRule(request);
        return Result.success(response);
    }
    
    /**
     * 更新营销规则
     */
    @PutMapping("/rule/{ruleId}")
    public Result<Void> updateRule(@PathVariable Long ruleId, @RequestBody CreateMarketingRuleRequest request) {
        marketingService.updateRule(ruleId, request);
        return Result.success();
    }
    
    /**
     * 启用/禁用规则
     */
    @PostMapping("/rule/{ruleId}/toggle")
    public Result<Void> toggleRuleStatus(@PathVariable Long ruleId, @RequestParam Boolean enabled) {
        marketingService.toggleRuleStatus(ruleId, enabled);
        return Result.success();
    }
    
    /**
     * 删除营销规则
     */
    @DeleteMapping("/rule/{ruleId}")
    public Result<Void> deleteRule(@PathVariable Long ruleId) {
        marketingService.deleteRule(ruleId);
        return Result.success();
    }
    
    /**
     * 获取商户的营销规则列表
     */
    @GetMapping("/merchant/{merchantId}/rules")
    public Result<List<MarketingRuleResponse>> getMerchantRules(@PathVariable Long merchantId) {
        List<MarketingRuleResponse> rules = marketingService.getMerchantRules(merchantId);
        return Result.success(rules);
    }
    
    /**
     * 触发营销规则
     */
    @PostMapping("/rule/{ruleId}/trigger")
    public Result<Void> triggerRule(@PathVariable Long ruleId, 
                                     @RequestParam Long userId,
                                     @RequestParam(required = false) String triggerData) {
        marketingService.triggerRule(ruleId, userId, triggerData);
        return Result.success();
    }
    
    /**
     * 处理地理围栏触发
     */
    @PostMapping("/trigger/geofence")
    public Result<Void> handleGeofenceTrigger(@RequestParam Long merchantId,
                                               @RequestParam Long userId,
                                               @RequestParam String geofenceId,
                                               @RequestParam String eventType) {
        marketingService.handleGeofenceTrigger(merchantId, userId, geofenceId, eventType);
        return Result.success();
    }
}
