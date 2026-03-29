package com.im.backend.modules.local.service;

import com.im.backend.modules.local.dto.*;
import java.util.List;

/**
 * 智能调度服务接口
 */
public interface ISmartDispatchService {
    
    /**
     * 创建调度任务
     */
    DispatchTaskResponse createDispatchTask(CreateDispatchTaskRequest request);
    
    /**
     * 获取任务详情
     */
    DispatchTaskResponse getTaskById(String taskId);
    
    /**
     * 智能分配任务
     */
    DispatchTaskResponse assignTask(String taskId);
    
    /**
     * 标记取货
     */
    DispatchTaskResponse markPickup(String taskId);
    
    /**
     * 标记送达
     */
    DispatchTaskResponse markDelivered(String taskId);
    
    /**
     * 取消任务
     */
    DispatchTaskResponse cancelTask(String taskId, String reason);
    
    /**
     * 获取服务人员的活跃任务
     */
    List<DispatchTaskResponse> getActiveTasksByStaff(String staffId);
    
    /**
     * 路径规划
     */
    PathPlanningResponse planPath(PathPlanningRequest request);
    
    /**
     * 多订单路径优化
     */
    PathPlanningResponse optimizeMultiOrderPath(String staffId, List<String> taskIds);
    
    /**
     * 计算配送费
     */
    java.math.BigDecimal calculateDeliveryFee(BigDecimal distance, BigDecimal weight);
}
