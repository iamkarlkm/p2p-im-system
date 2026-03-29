package com.im.backend.modules.navigation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.navigation.dto.RouteRequestDTO;
import com.im.backend.modules.navigation.dto.RouteResponseDTO;
import com.im.backend.modules.navigation.entity.NavigationRoute;

import java.util.List;

/**
 * 导航服务接口
 */
public interface NavigationService {

    /**
     * 规划路线
     */
    RouteResponseDTO planRoute(RouteRequestDTO request);

    /**
     * 批量规划多条路线方案
     */
    List<RouteResponseDTO> planMultipleRoutes(RouteRequestDTO request);

    /**
     * 重新规划路线(根据实时路况)
     */
    RouteResponseDTO reRoute(Long routeId, String reason);

    /**
     * 保存路线
     */
    Long saveRoute(RouteRequestDTO request, RouteResponseDTO response);

    /**
     * 获取路线详情
     */
    RouteResponseDTO getRouteDetail(Long routeId);

    /**
     * 获取用户路线列表
     */
    List<RouteResponseDTO> getUserRoutes(Long userId);

    /**
     * 分页获取用户路线
     */
    IPage<RouteResponseDTO> getUserRoutesPage(Page<NavigationRoute> page, Long userId);

    /**
     * 获取收藏路线
     */
    List<RouteResponseDTO> getFavoriteRoutes(Long userId);

    /**
     * 获取相似历史路线
     */
    List<RouteResponseDTO> getSimilarRoutes(Long userId, Double startLng, Double startLat, 
                                            Double endLng, Double endLat);

    /**
     * 更新路线收藏状态
     */
    void updateFavoriteStatus(Long routeId, Boolean isFavorite);

    /**
     * 删除路线
     */
    void deleteRoute(Long routeId);

    /**
     * 使用路线(增加使用次数)
     */
    void useRoute(Long routeId);

    /**
     * 获取实时路况
     */
    RouteResponseDTO.TrafficInfoDTO getRealTimeTraffic(String polyline);

    /**
     * 预估到达时间
     */
    String estimateArrivalTime(Double startLng, Double startLat, 
                               Double endLng, Double endLat, String travelMode);

    /**
     * 检查限行信息
     */
    RouteResponseDTO.RestrictionInfoDTO checkRestriction(String polyline, String plateNumber);
}
