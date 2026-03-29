package com.im.modules.merchant.automation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.common.dto.Result;
import com.im.modules.merchant.automation.dto.*;
import com.im.modules.merchant.automation.service.IAutoMarketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 自动化营销控制器
 */
@RestController
@RequestMapping("/api/v1/merchant/marketing")
@RequiredArgsConstructor
public class AutoMarketingController {
    
    private final IAutoMarketingService autoMarketingService;
    
    /**
     * 创建营销任务
     */
    @PostMapping("/task")
    public Result<AutoMarketingTaskResponse> createTask(@Valid @RequestBody CreateAutoMarketingTaskRequest request) {
        AutoMarketingTaskResponse response = autoMarketingService.createTask(request);
        return Result.success(response);
    }
    
    /**
     * 更新营销任务
     */
    @PutMapping("/task/{taskId}")
    public Result<AutoMarketingTaskResponse> updateTask(@PathVariable String taskId, 
                                                         @Valid @RequestBody CreateAutoMarketingTaskRequest request) {
        AutoMarketingTaskResponse response = autoMarketingService.updateTask(taskId, request);
        return Result.success(response);
    }
    
    /**
     * 删除营销任务
     */
    @DeleteMapping("/task/{taskId}")
    public Result<Void> deleteTask(@PathVariable String taskId) {
        autoMarketingService.deleteTask(taskId);
        return Result.success();
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/task/{taskId}")
    public Result<AutoMarketingTaskResponse> getTaskDetail(@PathVariable String taskId) {
        AutoMarketingTaskResponse response = autoMarketingService.getTaskDetail(taskId);
        return Result.success(response);
    }
    
    /**
     * 查询商户任务列表
     */
    @GetMapping("/merchant/{merchantId}/tasks")
    public Result<IPage<AutoMarketingTaskResponse>> getMerchantTasks(
            @PathVariable String merchantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AutoMarketingTaskResponse> pageParam = new Page<>(page, size);
        IPage<AutoMarketingTaskResponse> result = autoMarketingService.getMerchantTasks(pageParam, merchantId);
        return Result.success(result);
    }
    
    /**
     * 启用任务
     */
    @PostMapping("/task/{taskId}/enable")
    public Result<Void> enableTask(@PathVariable String taskId) {
        autoMarketingService.enableTask(taskId);
        return Result.success();
    }
    
    /**
     * 暂停任务
     */
    @PostMapping("/task/{taskId}/pause")
    public Result<Void> pauseTask(@PathVariable String taskId) {
        autoMarketingService.pauseTask(taskId);
        return Result.success();
    }
    
    /**
     * 立即执行任务
     */
    @PostMapping("/task/{taskId}/execute")
    public Result<Void> executeTaskNow(@PathVariable String taskId) {
        autoMarketingService.executeTaskNow(taskId);
        return Result.success();
    }
}
