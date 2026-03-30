package com.im.backend.modules.merchant.dispatch.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.dispatch.entity.DeliveryCapacityResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 运力资源Mapper
 * Feature #309: Instant Delivery Capacity Dispatch
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Mapper
public interface DeliveryCapacityResourceMapper extends BaseMapper<DeliveryCapacityResource> {

    /**
     * 查询空闲骑手
     * 
     * @param serviceAreaId 服务区域ID
     * @return 骑手列表
     */
    @Select("SELECT * FROM delivery_capacity_resource WHERE status = 1 AND service_area_id = #{serviceAreaId} AND deleted = 0")
    List<DeliveryCapacityResource> selectAvailableRiders(@Param("serviceAreaId") Long serviceAreaId);

    /**
     * 查询附近骑手
     * 
     * @param lng    经度
     * @param lat    纬度
     * @param radius 半径(米)
     * @return 骑手列表
     */
    @Select("SELECT *, (6371 * ACOS(COS(RADIANS(#{lat})) * COS(RADIANS(current_lat)) * COS(RADIANS(current_lng) - RADIANS(#{lng})) + SIN(RADIANS(#{lat})) * SIN(RADIANS(current_lat)))) AS distance FROM delivery_capacity_resource WHERE status = 1 AND deleted = 0 HAVING distance < #{radius} ORDER BY distance")
    List<DeliveryCapacityResource> selectNearbyRiders(@Param("lng") Double lng, 
                                                       @Param("lat") Double lat, 
                                                       @Param("radius") Double radius);

    /**
     * 更新骑手状态
     * 
     * @param riderId 骑手ID
     * @param status  状态
     * @return 影响行数
     */
    @Update("UPDATE delivery_capacity_resource SET status = #{status}, update_time = NOW() WHERE rider_id = #{riderId}")
    int updateStatus(@Param("riderId") Long riderId, @Param("status") Integer status);

    /**
     * 更新骑手位置
     * 
     * @param riderId 骑手ID
     * @param lng     经度
     * @param lat     纬度
     * @return 影响行数
     */
    @Update("UPDATE delivery_capacity_resource SET current_lng = #{lng}, current_lat = #{lat}, location_update_time = NOW(), update_time = NOW() WHERE rider_id = #{riderId}")
    int updateLocation(@Param("riderId") Long riderId, @Param("lng") Double lng, @Param("lat") Double lat);

    /**
     * 增加当前订单数
     * 
     * @param riderId 骑手ID
     * @return 影响行数
     */
    @Update("UPDATE delivery_capacity_resource SET current_orders = current_orders + 1, last_dispatch_time = NOW(), update_time = NOW() WHERE rider_id = #{riderId}")
    int incrementOrderCount(@Param("riderId") Long riderId);

    /**
     * 减少当前订单数
     * 
     * @param riderId 骑手ID
     * @return 影响行数
     */
    @Update("UPDATE delivery_capacity_resource SET current_orders = GREATEST(current_orders - 1, 0), today_completed_orders = today_completed_orders + 1, update_time = NOW() WHERE rider_id = #{riderId}")
    int decrementOrderCount(@Param("riderId") Long riderId);
}
