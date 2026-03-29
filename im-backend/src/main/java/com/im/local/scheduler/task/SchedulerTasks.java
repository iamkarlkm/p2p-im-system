package com.im.local.scheduler.task;

import com.im.local.scheduler.service.IGeofenceService;
import com.im.local.scheduler.service.ISmartDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 调度系统定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerTasks {
    
    private final IGeofenceService geofenceService;
    private final ISmartDispatchService dispatchService;
    
    /**
     * 每分钟更新围栏动态边界
     */
    @Scheduled(fixedRate = 60000)
    public void updateGeofenceBoundaries() {
        log.debug("执行围栏动态边界更新任务");
        geofenceService.batchUpdateDynamicBoundaries();
    }
    
    /**
     * 每30秒处理待分配订单
     */
    @Scheduled(fixedRate = 30000)
    public void processPendingBatches() {
        log.debug("执行待分配订单处理任务");
        // 处理逻辑
    }
    
    /**
     * 每5分钟生成调度建议
     */
    @Scheduled(fixedRate = 300000)
    public void generateDispatchSuggestions() {
        log.info("生成调度建议报告");
        // 生成调度建议
    }
}
