package com.im.modules.merchant.automation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.modules.merchant.automation.dto.*;

/**
 * 自动化营销服务接口
 */
public interface IAutoMarketingService {
    
    /**
     * 创建营销任务
     */
    AutoMarketingTaskResponse createTask(CreateAutoMarketingTaskRequest request);
    
    /**
     * 更新营销任务
     */
    AutoMarketingTaskResponse updateTask(String taskId, CreateAutoMarketingTaskRequest request);
    
    /**
     * 删除营销任务
     */
    void deleteTask(String taskId);
    
    /**
     * 获取任务详情
     */
    AutoMarketingTaskResponse getTaskDetail(String taskId);
    
    /**
     * 查询商户任务列表
     */
    IPage<AutoMarketingTaskResponse> getMerchantTasks(Page<AutoMarketingTaskResponse> page, String merchantId);
    
    /**
     * 启用任务
     */
    void enableTask(String taskId);
    
    /**
     * 暂停任务
     */
    void pauseTask(String taskId);
    
    /**
     * 立即执行任务
     */
    void executeTaskNow(String taskId);
    
    /**
     * 触发新用户欢迎
     */
    void triggerNewUserWelcome(String merchantId, String userId);
    
    /**
     * 触发订单评价邀请
     */
    void triggerOrderReviewInvite(String merchantId, String userId, String orderId);
}
