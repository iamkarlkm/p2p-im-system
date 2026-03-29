package com.im.backend.modules.delivery.service;

import com.im.backend.modules.delivery.model.dto.RouteOptimizeRequest;
import com.im.backend.modules.delivery.model.dto.RouteResponse;
import com.im.backend.modules.delivery.model.entity.RouteNode;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路径优化服务接口
 */
public interface PathOptimizationService {
    
    /**
     * 优化路径
     */
    RouteResponse optimizeRoute(RouteOptimizeRequest request);
    
    /**
     * 重新计算路线
     */
    RouteResponse recalculateRoute(Long routeId, List<Long> newTaskIds);
    
    /**
     * 计算两点距离
     */
    BigDecimal calculateDistance(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2);
    
    /**
     * 计算ETA
     */
    Integer calculateETA(BigDecimal fromLat, BigDecimal fromLng, BigDecimal toLat, BigDecimal toLng);
    
    /**
     * TSP求解
     */
    List<RouteNode> solveTSP(List<RouteNode> nodes, Integer strategy);
    
    /**
     * 获取配送热力图
     */
    Object getDeliveryHeatmap(String city, Long startTime, Long endTime);
}
