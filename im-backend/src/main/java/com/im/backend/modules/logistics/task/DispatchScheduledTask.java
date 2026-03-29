package com.im.backend.modules.logistics.task;

import com.im.backend.modules.logistics.service.ISmartDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 配送调度定时任务
 * 自动派单、订单超时处理等
 */
@Slf4j
@Component
public class DispatchScheduledTask {

    @Autowired
    private ISmartDispatchService dispatchService;

    /**
     * 自动派单任务
     * 每30秒执行一次
     */
    @Scheduled(fixedRate = 30000)
    public void autoDispatchTask() {
        try {
            log.debug("开始执行自动派单任务...");
            int count = dispatchService.batchDispatchOrders();
            if (count > 0) {
                log.info("自动派单任务完成: 成功分配 {} 个订单", count);
            }
        } catch (Exception e) {
            log.error("自动派单任务执行异常", e);
        }
    }
}
