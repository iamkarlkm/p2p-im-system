package com.im.backend.modules.delivery.service;

import com.im.backend.modules.delivery.model.dto.*;
import java.util.List;

/**
 * 配送任务服务接口
 */
public interface DeliveryTaskService {
    
    /**
     * 创建配送任务
     */
    TaskResponse createTask(CreateTaskRequest request);
    
    /**
     * 获取任务详情
     */
    TaskResponse getTaskById(Long taskId);
    
    /**
     * 分配骑手
     */
    void assignRider(Long taskId, Long riderId);
    
    /**
     * 更新任务状态
     */
    void updateTaskStatus(Long taskId, Integer status);
    
    /**
     * 获取骑手任务列表
     */
    List<TaskResponse> getRiderTasks(Long riderId, Integer status);
    
    /**
     * 获取任务统计
     */
    Object getTaskStats(Long merchantId, Long startTime, Long endTime);
    
    /**
     * 取消任务
     */
    void cancelTask(Long taskId, String reason);
}
