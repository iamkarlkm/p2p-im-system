package com.im.backend.modules.merchant.dispatch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.merchant.dispatch.entity.DeliveryCapacityResource;
import com.im.backend.modules.merchant.dispatch.entity.DispatchTask;
import com.im.backend.modules.merchant.dispatch.repository.DeliveryCapacityResourceMapper;
import com.im.backend.modules.merchant.dispatch.repository.DispatchTaskMapper;
import com.im.backend.modules.merchant.dispatch.service.IDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 运力调度服务实现
 * Feature #309: Instant Delivery Capacity Dispatch
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Slf4j
@Service
public class DispatchServiceImpl extends ServiceImpl<DispatchTaskMapper, DispatchTask> 
        implements IDispatchService {

    @Autowired
    private DispatchTaskMapper dispatchTaskMapper;

    @Autowired
    private DeliveryCapacityResourceMapper capacityResourceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long smartDispatch(DispatchTask task) {
        // 获取附近可用骑手
        List<DeliveryCapacityResource> availableRiders = capacityResourceMapper.selectNearbyRiders(
                task.getPickupLng(), task.getPickupLat(), 5000.0); // 5公里范围内

        if (availableRiders.isEmpty()) {
            log.warn("No available riders found for task: {}", task.getOrderId());
            return null;
        }

        // 智能选择算法：距离 + 评分 + 当前订单数
        DeliveryCapacityResource selectedRider = availableRiders.stream()
                .filter(r -> r.getCurrentOrders() < r.getMaxOrders())
                .min(Comparator.comparingInt(DeliveryCapacityResource::getCurrentOrders)
                        .thenComparing(r -> r.getRating() != null ? -r.getRating().doubleValue() : 0))
                .orElse(null);

        if (selectedRider == null) {
            log.warn("No suitable rider found for task: {}", task.getOrderId());
            return null;
        }

        // 分配骑手
        task.setRiderId(selectedRider.getRiderId());
        task.setDispatchStatus(1);
        task.setDispatchType(1);
        task.setDispatchTime(LocalDateTime.now());
        dispatchTaskMapper.insert(task);

        // 更新骑手订单数
        capacityResourceMapper.incrementOrderCount(selectedRider.getRiderId());

        log.info("Smart dispatched task: {} to rider: {}", task.getOrderId(), selectedRider.getRiderId());
        return selectedRider.getRiderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean grabOrder(Long taskId, Long riderId) {
        // 检查骑手状态
        DeliveryCapacityResource rider = capacityResourceMapper.selectById(riderId);
        if (rider == null || rider.getStatus() != 1) {
            return false;
        }

        // 尝试抢单
        int rows = dispatchTaskMapper.assignRider(taskId, riderId);
        if (rows > 0) {
            capacityResourceMapper.incrementOrderCount(riderId);
            log.info("Rider: {} grabbed order: {}", riderId, taskId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean acceptOrder(Long taskId, Long riderId) {
        int rows = dispatchTaskMapper.acceptTask(taskId, riderId);
        if (rows > 0) {
            // 更新骑手状态为配送中
            capacityResourceMapper.updateStatus(riderId, 3);
            log.info("Rider: {} accepted order: {}", riderId, taskId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeOrder(Long taskId) {
        DispatchTask task = dispatchTaskMapper.selectById(taskId);
        if (task == null) {
            return false;
        }

        int rows = dispatchTaskMapper.completeTask(taskId);
        if (rows > 0) {
            // 更新骑手订单数
            capacityResourceMapper.decrementOrderCount(task.getRiderId());
            log.info("Completed order: {}", taskId);
            return true;
        }
        return false;
    }

    @Override
    public List<DeliveryCapacityResource> getNearbyAvailableRiders(Double lng, Double lat, Double radius) {
        return capacityResourceMapper.selectNearbyRiders(lng, lat, radius);
    }

    @Override
    public Boolean updateRiderLocation(Long riderId, Double lng, Double lat) {
        int rows = capacityResourceMapper.updateLocation(riderId, lng, lat);
        return rows > 0;
    }

    @Override
    public Boolean updateRiderStatus(Long riderId, Integer status) {
        int rows = capacityResourceMapper.updateStatus(riderId, status);
        return rows > 0;
    }

    @Override
    public List<DispatchTask> getPendingDispatchTasks() {
        return dispatchTaskMapper.selectPendingTasks();
    }

    @Override
    public void batchDispatch() {
        List<DispatchTask> pendingTasks = dispatchTaskMapper.selectPendingTasks();
        log.info("Batch dispatching {} pending tasks", pendingTasks.size());

        for (DispatchTask task : pendingTasks) {
            try {
                smartDispatch(task);
            } catch (Exception e) {
                log.error("Failed to dispatch task: {}", task.getOrderId(), e);
            }
        }
    }

    @Override
    public List<DispatchTask> getRiderCurrentTasks(Long riderId) {
        return dispatchTaskMapper.selectRiderTasks(riderId, 2); // 配送中状态
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long reassignTask(Long taskId) {
        DispatchTask task = dispatchTaskMapper.selectById(taskId);
        if (task == null) {
            return null;
        }

        // 取消原分配
        Long oldRiderId = task.getRiderId();
        capacityResourceMapper.decrementOrderCount(oldRiderId);

        // 重新派单
        task.setRiderId(null);
        task.setDispatchStatus(0);
        return smartDispatch(task);
    }
}
