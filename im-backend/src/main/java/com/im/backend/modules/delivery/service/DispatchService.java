package com.im.backend.modules.delivery.service;

import com.im.backend.modules.delivery.dto.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 智能调度服务接口
 * 本地物流配送智能调度引擎 - 核心调度算法
 */
public interface DispatchService {

    /**
     * 智能派单（最近骑手匹配）
     */
    DispatchResultVO dispatchOrder(Long orderId);

    /**
     * 批量顺路单优化（TSP路径规划）
     */
    List<DispatchResultVO> optimizeBatchDispatch(List<Long> orderIds);

    /**
     * 计算顺路指数
     */
    Double calculateRouteMatchIndex(Long riderId, List<Long> orderIds);

    /**
     * 运力预测与动态调度
     */
    CapacityForecastVO forecastCapacity(Long stationId, String forecastDate);

    /**
     * 重新分配超时订单
     */
    boolean reassignTimeoutOrders();

    /**
     * 地理围栏检查
     */
    boolean checkGeofence(Long riderId, BigDecimal lat, BigDecimal lng, String fenceType);

    /**
     * 计算最优配送路径（A*算法）
     */
    List<RoutePointVO> calculateOptimalRoute(Long riderId, List<Long> orderIds);

    /**
     * 获取骑手负载均衡建议
     */
    List<RiderLoadBalanceVO> getLoadBalanceSuggestions(Long stationId);

    /**
     * 触发地理围栏事件
     */
    void triggerGeofenceEvent(GeofenceEventDTO event);
}
