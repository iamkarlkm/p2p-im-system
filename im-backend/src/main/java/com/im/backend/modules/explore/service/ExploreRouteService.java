package com.im.backend.modules.explore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.explore.entity.ExploreRoute;

import java.math.BigDecimal;
import java.util.List;

/**
 * 探店路线服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface ExploreRouteService extends IService<ExploreRoute> {

    /**
     * 创建探店路线
     */
    ExploreRoute createRoute(ExploreRoute route);

    /**
     * 更新探店路线
     */
    ExploreRoute updateRoute(ExploreRoute route);

    /**
     * 获取路线详情
     */
    ExploreRoute getRouteDetail(Long routeId);

    /**
     * 获取用户的路线列表
     */
    IPage<ExploreRoute> getUserRoutes(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取推荐的探店路线
     */
    IPage<ExploreRoute> getRecommendedRoutes(BigDecimal longitude, BigDecimal latitude, 
                                             Integer pageNum, Integer pageSize);

    /**
     * 智能规划探店路线
     * 基于用户选择的POI列表，使用TSP算法优化路线
     */
    ExploreRoute generateSmartRoute(List<Long> poiIds, Integer transportMode, Long userId);

    /**
     * 收藏路线
     */
    boolean favoriteRoute(Long routeId, Long userId);

    /**
     * 取消收藏路线
     */
    boolean unfavoriteRoute(Long routeId, Long userId);

    /**
     * 使用路线（导航开始）
     */
    boolean useRoute(Long routeId, Long userId);

    /**
     * 获取精选路线列表
     */
    IPage<ExploreRoute> getFeaturedRoutes(Integer pageNum, Integer pageSize);

    /**
     * 设置路线精选状态
     */
    boolean setFeatured(Long routeId, Boolean featured);

    /**
     * 删除路线
     */
    boolean deleteRoute(Long routeId, Long userId);

    /**
     * 获取热门路线榜单
     */
    List<ExploreRoute> getHotRoutes(Integer limit);
}
