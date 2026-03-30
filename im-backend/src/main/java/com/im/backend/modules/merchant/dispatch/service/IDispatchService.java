package com.im.backend.modules.merchant.dispatch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.merchant.dispatch.entity.DeliveryCapacityResource;
import com.im.backend.modules.merchant.dispatch.entity.DispatchTask;

import java.util.List;

/**
 * 运力调度服务接口
 * Feature #309: Instant Delivery Capacity Dispatch
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
public interface IDispatchService extends IService<DispatchTask> {

    /**
     * 智能派单
     * 
     * @param task 调度任务
     * @return 分配的骑手ID
     */
    Long smartDispatch(DispatchTask task);

    /**
     * 骑手抢单
     * 
     * @param taskId  任务ID
     * @param riderId 骑手ID
     * @return 是否成功
     */
    Boolean grabOrder(Long taskId, Long riderId);

    /**
     * 骑手接单
     * 
     * @param taskId  任务ID
     * @param riderId 骑手ID
     * @return 是否成功
     */
    Boolean acceptOrder(Long taskId, Long riderId);

    /**
     * 完成任务
     * 
     * @param taskId 任务ID
     * @return 是否成功
     */
    Boolean completeOrder(Long taskId);

    /**
     * 获取附近可用骑手
     * 
     * @param lng    经度
     * @param lat    纬度
     * @param radius 半径(米)
     * @return 骑手列表
     */
    List<DeliveryCapacityResource> getNearbyAvailableRiders(Double lng, Double lat, Double radius);

    /**
     * 更新骑手位置
     * 
     * @param riderId 骑手ID
     * @param lng     经度
     * @param lat     纬度
     * @return 是否成功
     */
    Boolean updateRiderLocation(Long riderId, Double lng, Double lat);

    /**
     * 更新骑手状态
     * 
     * @param riderId 骑手ID
     * @param status  状态
     * @return 是否成功
     */
    Boolean updateRiderStatus(Long riderId, Integer status);

    /**
     * 获取待分配任务列表
     * 
     * @return 任务列表
     */
    List<DispatchTask> getPendingDispatchTasks();

    /**
     * 批量智能派单
     */
    void batchDispatch();

    /**
     * 获取骑手的当前任务
     * 
     * @param riderId 骑手ID
     * @return 任务列表
     */
    List<DispatchTask> getRiderCurrentTasks(Long riderId);

    /**
     * 重新分配任务
     * 
     * @param taskId 任务ID
     * @return 新分配的骑手ID
     */
    Long reassignTask(Long taskId);
}
