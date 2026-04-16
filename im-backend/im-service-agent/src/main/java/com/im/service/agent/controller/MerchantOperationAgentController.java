package com.im.service.agent.controller;

import com.im.service.agent.dto.*;
import com.im.service.agent.service.MerchantOperationAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 智能商家运营Agent控制器
 * 功能#348: 多角色AI Agent协同完成商家运营任务
 */
@Slf4j
@RestController
@RequestMapping("/api/agent/merchant")
@RequiredArgsConstructor
public class MerchantOperationAgentController {

    private final MerchantOperationAgentService agentService;

    /**
     * 客服Agent: 处理用户咨询
     */
    @PostMapping("/customer-service/chat")
    public ResponseEntity<AgentChatResponse> customerServiceChat(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody AgentChatRequest request) {
        log.info("商家 {} 客服Agent处理咨询", merchantId);
        AgentChatResponse response = agentService.handleCustomerService(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 营销Agent: 生成营销方案
     */
    @PostMapping("/marketing/generate")
    public ResponseEntity<MarketingPlanResponse> generateMarketingPlan(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody MarketingPlanRequest request) {
        log.info("商家 {} 营销Agent生成方案", merchantId);
        MarketingPlanResponse response = agentService.generateMarketingPlan(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 营销Agent: 执行A/B测试
     */
    @PostMapping("/marketing/ab-test")
    public ResponseEntity<ABTestResponse> executeABTest(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody ABTestRequest request) {
        log.info("商家 {} 营销Agent执行A/B测试", merchantId);
        ABTestResponse response = agentService.executeABTest(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 库存Agent: 智能补货预测
     */
    @GetMapping("/inventory/forecast")
    public ResponseEntity<InventoryForecastResponse> forecastInventory(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam(defaultValue = "7") Integer daysAhead) {
        log.info("商家 {} 库存Agent预测未来{}天需求", merchantId, daysAhead);
        InventoryForecastResponse response = agentService.forecastInventory(merchantId, daysAhead);
        return ResponseEntity.ok(response);
    }

    /**
     * 库存Agent: 触发自动采购
     */
    @PostMapping("/inventory/auto-purchase")
    public ResponseEntity<AutoPurchaseResponse> triggerAutoPurchase(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody AutoPurchaseRequest request) {
        log.info("商家 {} 库存Agent触发自动采购", merchantId);
        AutoPurchaseResponse response = agentService.triggerAutoPurchase(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 财务Agent: 自动对账
     */
    @PostMapping("/finance/reconcile")
    public ResponseEntity<ReconciliationResponse> autoReconcile(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("商家 {} 财务Agent自动对账 {} 至 {}", merchantId, startDate, endDate);
        ReconciliationResponse response = agentService.autoReconcile(merchantId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * 财务Agent: 生成报表
     */
    @GetMapping("/finance/report")
    public ResponseEntity<FinanceReportResponse> generateFinanceReport(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam String reportType,
            @RequestParam String period) {
        log.info("商家 {} 财务Agent生成{}报表", merchantId, reportType);
        FinanceReportResponse response = agentService.generateFinanceReport(merchantId, reportType, period);
        return ResponseEntity.ok(response);
    }

    /**
     * 排班Agent: 智能排班优化
     */
    @PostMapping("/schedule/optimize")
    public ResponseEntity<ScheduleOptimizeResponse> optimizeSchedule(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody ScheduleOptimizeRequest request) {
        log.info("商家 {} 排班Agent优化排班", merchantId);
        ScheduleOptimizeResponse response = agentService.optimizeSchedule(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有Agent状态
     */
    @GetMapping("/agents/status")
    public ResponseEntity<Map<String, AgentStatusResponse>> getAllAgentStatus(
            @RequestHeader("X-Merchant-Id") Long merchantId) {
        Map<String, AgentStatusResponse> statuses = agentService.getAllAgentStatus(merchantId);
        return ResponseEntity.ok(statuses);
    }

    /**
     * 批量执行Agent任务
     */
    @PostMapping("/agents/execute-batch")
    public ResponseEntity<BatchExecutionResponse> executeBatchTasks(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestBody List<String> taskTypes) {
        log.info("商家 {} 批量执行Agent任务: {}", merchantId, taskTypes);
        BatchExecutionResponse response = agentService.executeBatchTasks(merchantId, taskTypes);
        return ResponseEntity.ok(response);
    }
}
