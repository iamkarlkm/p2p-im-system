package com.im.backend.modules.navigation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.navigation.entity.NavigationRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 导航路线Repository
 */
@Mapper
public interface NavigationRouteRepository extends BaseMapper<NavigationRoute> {

    /**
     * 根据用户ID查询路线列表
     */
    @Select("SELECT * FROM im_navigation_route WHERE user_id = #{userId} AND deleted = 0 ORDER BY last_used_time DESC")
    List<NavigationRoute> findByUserId(@Param("userId") Long userId);

    /**
     * 分页查询用户路线
     */
    IPage<NavigationRoute> findPageByUserId(Page<NavigationRoute> page, @Param("userId") Long userId);

    /**
     * 查询用户收藏的路线
     */
    @Select("SELECT * FROM im_navigation_route WHERE user_id = #{userId} AND is_favorite = 1 AND deleted = 0 ORDER BY update_time DESC")
    List<NavigationRoute> findFavoriteRoutes(@Param("userId") Long userId);

    /**
     * 根据起点和终点查询历史路线
     */
    @Select("SELECT * FROM im_navigation_route WHERE user_id = #{userId} " +
            "AND start_longitude = #{startLng} AND start_latitude = #{startLat} " +
            "AND end_longitude = #{endLng} AND end_latitude = #{endLat} " +
            "AND deleted = 0 ORDER BY usage_count DESC LIMIT 5")
    List<NavigationRoute> findSimilarRoutes(@Param("userId") Long userId,
                                             @Param("startLng") Double startLng,
                                             @Param("startLat") Double startLat,
                                             @Param("endLng") Double endLng,
                                             @Param("endLat") Double endLat);

    /**
     * 更新路线使用次数和最后使用时间
     */
    @Update("UPDATE im_navigation_route SET usage_count = usage_count + 1, " +
            "last_used_time = NOW() WHERE id = #{routeId}")
    int incrementUsageCount(@Param("routeId") Long routeId);

    /**
     * 设置路线收藏状态
     */
    @Update("UPDATE im_navigation_route SET is_favorite = #{isFavorite} WHERE id = #{routeId}")
    int updateFavoriteStatus(@Param("routeId") Long routeId, @Param("isFavorite") Boolean isFavorite);

    /**
     * 查询高频使用路线
     */
    @Select("SELECT * FROM im_navigation_route WHERE user_id = #{userId} " +
            "AND usage_count >= #{minUsage} AND deleted = 0 ORDER BY usage_count DESC LIMIT 10")
    List<NavigationRoute> findFrequentRoutes(@Param("userId") Long userId, @Param("minUsage") Integer minUsage);
}
