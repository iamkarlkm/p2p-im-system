package com.im.local.scheduler.service;

import com.im.local.scheduler.dto.*;
import java.util.List;

/**
 * 智能调度服务接口
 */
public interface ISmartDispatchService {
    
    /**
     * 智能派单
     */
    DispatchResultResponse dispatchOrder(DispatchOrderRequest request);
    
    /**
     * 批量智能派单
     */
    List<DispatchResultResponse> batchDispatchOrders(List<DispatchOrderRequest> requests);
    
    /**
     * 围栏内订单聚合
     */
    List<Long> aggregateOrdersInGeofence(Long geofenceId);
    
    /**
     * 计算最优配送路径
     */
    List<DispatchResultResponse.RouteNode> calculateOptimalRoute(RoutePlanRequest request);
    
    /**
     * 跨围栏运力借调
     */
    boolean borrowStaffFromNearbyGeofence(Long targetGeofenceId, Integer staffCount);
    
    /**
     * 重新分配订单
     */
    DispatchResultResponse reassignBatch(Long batchId, Long newStaffId);
    
    /**
     * 获取调度建议
     */
    List<String> getDispatchSuggestions(Long geofenceId);
}
