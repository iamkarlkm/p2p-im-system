package com.im.backend.modules.local.life.service;

import com.im.backend.modules.local.life.dto.*;

import java.util.List;

/**
 * 导航服务接口
 * Navigation Service Interface
 */
public interface NavigationService {

    /**
     * 规划路线
     */
    RoutePlanningResultDTO planRoute(RoutePlanningRequestDTO request, Long userId);

    /**
     * 多目的地路线规划
     */
    RoutePlanningResultDTO planMultiStopRoute(RoutePlanningRequestDTO request, List<RoutePlanningRequestDTO.WaypointDTO> stops, Long userId);

    /**
     * 开始导航
     */
    NavigationStatusDTO startNavigation(Long routeId, Long userId);

    /**
     * 更新位置
     */
    NavigationStatusDTO updateLocation(LocationUpdateRequestDTO request, Long userId);

    /**
     * 获取导航状态
     */
    NavigationStatusDTO getNavigationStatus(Long sessionId, Long userId);

    /**
     * 暂停导航
     */
    NavigationStatusDTO pauseNavigation(Long sessionId, Long userId);

    /**
     * 恢复导航
     */
    NavigationStatusDTO resumeNavigation(Long sessionId, Long userId);

    /**
     * 结束导航
     */
    void endNavigation(Long sessionId, Long userId);

    /**
     * 取消导航
     */
    void cancelNavigation(Long sessionId, Long userId);

    /**
     * 重新规划路线（偏航后）
     */
    RoutePlanningResultDTO reroute(Long sessionId, Long userId);

    /**
     * 收藏路线
     */
    void favoriteRoute(Long routeId, Long userId);

    /**
     * 取消收藏路线
     */
    void unfavoriteRoute(Long routeId, Long userId);

    /**
     * 获取收藏的路线列表
     */
    List<RoutePlanningResultDTO> getFavoriteRoutes(Long userId, Integer page, Integer size);

    /**
     * 删除路线
     */
    void deleteRoute(Long routeId, Long userId);

    /**
     * 获取导航历史记录
     */
    List<RoutePlanningResultDTO> getNavigationHistory(Long userId, Integer page, Integer size);
}
