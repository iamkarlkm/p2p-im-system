package com.im.backend.modules.local.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local.entity.Geofence;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 地理围栏Mapper
 */
@Repository
public interface GeofenceMapper extends BaseMapper<Geofence> {
    
    /**
     * 根据商户ID查询围栏
     */
    @Select("SELECT * FROM geofence WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY level ASC")
    List<Geofence> selectByMerchantId(@Param("merchantId") String merchantId);
    
    /**
     * 查询启用的围栏
     */
    @Select("SELECT * FROM geofence WHERE status = 1 AND deleted = 0")
    List<Geofence> selectActiveGeofences();
    
    /**
     * 更新围栏订单数
     */
    @Update("UPDATE geofence SET current_order_count = #{count}, update_time = NOW() WHERE id = #{geofenceId}")
    int updateOrderCount(@Param("geofenceId") String geofenceId, @Param("count") Integer count);
    
    /**
     * 更新围栏运力负载
     */
    @Update("UPDATE geofence SET capacity_load = #{load}, update_time = NOW() WHERE id = #{geofenceId}")
    int updateCapacityLoad(@Param("geofenceId") String geofenceId, @Param("load") Integer load);
    
    /**
     * 更新可用服务人员数
     */
    @Update("UPDATE geofence SET available_staff_count = #{count}, update_time = NOW() WHERE id = #{geofenceId}")
    int updateStaffCount(@Param("geofenceId") String geofenceId, @Param("count") Integer count);
    
    /**
     * 根据坐标查找包含的圆形围栏
     */
    @Select("SELECT *, (6371000 * ACOS(COS(RADIANS(#{lat})) * COS(RADIANS(center_latitude)) * " +
            "COS(RADIANS(center_longitude) - RADIANS(#{lng})) + SIN(RADIANS(#{lat})) * SIN(RADIANS(center_latitude)))) " +
            "AS distance FROM geofence WHERE type = 1 AND status = 1 AND deleted = 0 " +
            "HAVING distance <= radius ORDER BY distance ASC")
    List<Geofence> findCircleGeofencesByPoint(@Param("lng") BigDecimal longitude, @Param("lat") BigDecimal latitude);
}
