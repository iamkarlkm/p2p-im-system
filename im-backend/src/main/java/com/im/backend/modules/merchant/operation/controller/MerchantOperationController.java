package com.im.backend.modules.merchant.operation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.operation.dto.MerchantOperationConfigRequest;
import com.im.backend.modules.merchant.operation.dto.MerchantOperationDailyReportResponse;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationConfig;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationTask;
import com.im.backend.modules.merchant.operation.service.IMerchantOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户运营助手控制器
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/merchant/operation")
@Tag(name = "商户运营助手", description = "Local Merchant Smart Operation Assistant APIs")
public class MerchantOperationController {

    @Autowired
    private IMerchantOperationService merchantOperationService;

    /**
     * 保存运营配置
     */
    @PostMapping("/config")
    @Operation(summary = "保存运营配置", description = "创建或更新商户运营助手配置")
    public Result<Long> saveConfig(@Validated @RequestBody MerchantOperationConfigRequest request) {
        log.info("Saving operation config for merchant: {}", request.getMerchantId());
        Long configId = merchantOperationService.saveConfig(request);
        return Result.success(configId);
    }

    /**
     * 获取运营配置
     */
    @GetMapping("/config/{merchantId}")
    @Operation(summary = "获取运营配置", description = "获取商户运营助手配置信息")
    @Parameter(name = "merchantId", description = "商户ID", required = true)
    public Result<MerchantOperationConfig> getConfig(@PathVariable Long merchantId) {
        log.info("Getting operation config for merchant: {}", merchantId);
        MerchantOperationConfig config = merchantOperationService.getConfigByMerchantId(merchantId);
        return Result.success(config);
    }

    /**
     * 生成日报
     */
    @PostMapping("/report/generate/{merchantId}")
    @Operation(summary = "生成运营日报", description = "为指定商户生成指定日期的运营日报")
    @Parameter(name = "merchantId", description = "商户ID", required = true)
    public Result<MerchantOperationDailyReportResponse> generateDailyReport(
            @PathVariable Long merchantId,
            @RequestParam(required = false) LocalDate reportDate) {
        if (reportDate == null) {
            reportDate = LocalDate.now().minusDays(1);
        }
        log.info("Generating daily report for merchant: {}, date: {}", merchantId, reportDate);
        merchantOperationService.generateDailyReport(merchantId, reportDate);
        return Result.success(null);
    }

    /**
     * 分页查询日报
     */
    @GetMapping("/report/list/{merchantId}")
    @Operation(summary = "查询日报列表", description = "分页查询商户运营日报")
    @Parameter(name = "merchantId", description = "商户ID", required = true)
    public Result<Page<MerchantOperationDailyReportResponse>> queryDailyReportPage(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("Querying daily reports for merchant: {}, page: {}, size: {}", merchantId, pageNum, pageSize);
        Page<MerchantOperationDailyReportResponse> page = merchantOperationService.queryDailyReportPage(merchantId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取最新日报
     */
    @GetMapping("/report/latest/{merchantId}")
    @Operation(summary = "获取最新日报", description = "获取商户最新运营日报")
    @Parameter(name = "merchantId", description = "商户ID", required = true)
    public Result<MerchantOperationDailyReportResponse> getLatestDailyReport(@PathVariable Long merchantId) {
        log.info("Getting latest daily report for merchant: {}", merchantId);
        MerchantOperationDailyReportResponse report = merchantOperationService.getLatestDailyReport(merchantId);
        return Result.success(report);
    }

    /**
     * 创建运营任务
     */
    @PostMapping("/task")
    @Operation(summary = "创建运营任务", description = "创建商户运营任务")
    public Result<Long> createTask(@RequestBody MerchantOperationTask task) {
        log.info("Creating operation task for merchant: {}", task.getMerchantId());
        Long taskId = merchantOperationService.createOperationTask(task);
        return Result.success(taskId);
    }

    /**
     * 获取待处理任务列表
     */
    @GetMapping("/task/pending/{merchantId}")
    @Operation(summary = "获取待处理任务", description = "获取商户待处理的运营任务列表")
    @Parameter(name = "merchantId", description = "商户ID", required = true)
    public Result<List<MerchantOperationTask>> getPendingTasks(@PathVariable Long merchantId) {
        log.info("Getting pending tasks for merchant: {}", merchantId);
        List<MerchantOperationTask> tasks = merchantOperationService.getPendingTasks(merchantId);
        return Result.success(tasks);
    }

    /**
     * 执行任务
     */
    @PostMapping("/task/execute/{taskId}")
    @Operation(summary = "执行任务", description = "执行指定的运营任务")
    @Parameter(name = "taskId", description = "任务ID", required = true)
    public Result<Boolean> executeTask(
            @PathVariable Long taskId,
            @RequestParam(required = false, defaultValue = "手动执行完成") String result) {
        log.info("Executing task: {}", taskId);
        Boolean success = merchantOperationService.executeTask(taskId, result);
        return Result.success(success);
    }

    /**
     * 获取AI运营建议
     */
    @GetMapping("/suggestions/{merchantId}")
    @Operation(summary = "获取AI运营建议", description = "获取AI生成的运营建议")
    @Parameter(name = "merchantId", description = "商户ID", required = true)
    public Result<List<String>> getAIOperationSuggestions(@PathVariable Long merchantId) {
        log.info("Getting AI operation suggestions for merchant: {}", merchantId);
        List<String> suggestions = merchantOperationService.getAIOperationSuggestions(merchantId);
        return Result.success(suggestions);
    }

    /**
     * 开启自动营销
     */
    @PostMapping("/auto-marketing/enable/{merchantId}")
    @Operation(summary = "开启自动营销", description = "为商户开启自动营销功能")
    @Parameter(name = "merchantId", description = "商户ID", required = true)
    public Result<Boolean> enableAutoMarketing(
            @PathVariable Long merchantId,
            @RequestParam(required = false, defaultValue = "{}") String rules) {
        log.info("Enabling auto marketing for merchant: {}", merchantId);
        Boolean success = merchantOperationService.enableAutoMarketing(merchantId, rules);
        return Result.success(success);
    }

    /**
     * 触发批量生成日报
     */
    @PostMapping("/report/batch-generate")
    @Operation(summary = "批量生成日报", description = "批量为所有商户生成指定日期的日报")
    public Result<Void> batchGenerateDailyReports(
            @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now().minusDays(1);
        }
        log.info("Batch generating daily reports for date: {}", date);
        merchantOperationService.batchGenerateDailyReports(date);
        return Result.success(null);
    }
}
