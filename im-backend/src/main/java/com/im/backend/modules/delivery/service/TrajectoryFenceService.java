package com.im.backend.modules.delivery.service;

import com.im.backend.modules.delivery.model.dto.*;
import java.util.List;

/**
 * 轨迹围栏服务接口
 */
public interface TrajectoryFenceService {
    
    /**
     * 上传轨迹点
     */
    void uploadTrajectory(TrajectoryUploadRequest request);
    
    /**
     * 批量上传轨迹点
     */
    void batchUploadTrajectory(List<TrajectoryUploadRequest> requests);
    
    /**
     * 检测围栏
     */
    FenceCheckResult checkFence(FenceCheckRequest request);
    
    /**
     * 获取围栏告警列表
     */
    List<FenceAlertResponse> getFenceAlerts(Long riderId, Integer status);
    
    /**
     * 处理围栏告警
     */
    void handleFenceAlert(Long alertId, String result, Long handlerId);
    
    /**
     * 获取偏离统计
     */
    Object getDeviationStats(Long riderId, Long taskId);
}
