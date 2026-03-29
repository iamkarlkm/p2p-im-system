package com.im.modules.merchant.automation.controller;

import com.im.common.dto.Result;
import com.im.modules.merchant.automation.dto.*;
import com.im.modules.merchant.automation.service.IMerchantReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 商户智能提醒控制器
 */
@RestController
@RequestMapping("/api/v1/merchant/reminder")
@RequiredArgsConstructor
public class MerchantReminderController {
    
    private final IMerchantReminderService reminderService;
    
    /**
     * 获取商户提醒配置
     */
    @GetMapping("/config/{merchantId}")
    public Result<MerchantReminderConfigResponse> getConfig(@PathVariable String merchantId) {
        MerchantReminderConfigResponse response = reminderService.getConfig(merchantId);
        return Result.success(response);
    }
    
    /**
     * 更新商户提醒配置
     */
    @PutMapping("/config")
    public Result<MerchantReminderConfigResponse> updateConfig(@Valid @RequestBody UpdateReminderConfigRequest request) {
        MerchantReminderConfigResponse response = reminderService.updateConfig(request);
        return Result.success(response);
    }
    
    /**
     * 获取未读提醒数
     */
    @GetMapping("/merchant/{merchantId}/unread-count")
    public Result<Map<String, Integer>> getUnreadCount(@PathVariable String merchantId) {
        int count = reminderService.getUnreadCount(merchantId);
        return Result.success(Map.of("count", count));
    }
    
    /**
     * 获取提醒列表
     */
    @GetMapping("/merchant/{merchantId}/list")
    public Result<List<MerchantReminderRecordResponse>> getReminderList(
            @PathVariable String merchantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MerchantReminderRecordResponse> list = reminderService.getReminderList(merchantId, page, size);
        return Result.success(list);
    }
    
    /**
     * 标记提醒为已读
     */
    @PostMapping("/record/{recordId}/read")
    public Result<Void> markAsRead(@PathVariable String recordId) {
        reminderService.markAsRead(recordId);
        return Result.success();
    }
    
    /**
     * 触发异常订单提醒(测试接口)
     */
    @PostMapping("/test/abnormal-order")
    public Result<Void> triggerAbnormalOrder(@RequestParam String merchantId, 
                                              @RequestParam String orderId,
                                              @RequestParam String reason) {
        reminderService.triggerAbnormalOrderReminder(merchantId, orderId, reason);
        return Result.success();
    }
    
    /**
     * 触发库存不足提醒(测试接口)
     */
    @PostMapping("/test/low-stock")
    public Result<Void> triggerLowStock(@RequestParam String merchantId,
                                         @RequestParam String productId,
                                         @RequestParam String productName,
                                         @RequestParam int currentStock) {
        reminderService.triggerLowStockReminder(merchantId, productId, productName, currentStock);
        return Result.success();
    }
    
    /**
     * 发送经营日报(测试接口)
     */
    @PostMapping("/test/daily-report")
    public Result<Void> sendDailyReport(@RequestParam String merchantId) {
        reminderService.sendDailyReport(merchantId);
        return Result.success();
    }
}
