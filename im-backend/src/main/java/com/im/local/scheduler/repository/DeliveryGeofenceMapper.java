package com.im.local.scheduler.repository;

import com.im.local.scheduler.entity.DeliveryGeofence;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.math.BigDecimal;

/**
 * 配送围栏数据访问层
 */
@Mapper
public interface DeliveryGeofenceMapper {
    
    @Insert("INSERT INTO delivery_geofence (name, type, shape_type, " +
            "center_lng, center_lat, radius, polygon_points, geohash_grids, " +
            "city_code, district_code, current_order_count, current_staff_count, " +
            "saturation_rate, dynamic_radius, base_radius, peak_expansion_ratio, " +
            "dynamic_adjust_enabled, status, created_at, updated_at) " +
            "VALUES (#{name}, #{type}, #{shapeType}, " +
            "#{centerLng}, #{centerLat}, #{radius}, #{polygonPoints}, #{geohashGrids}, " +
            "#{cityCode}, #{districtCode}, #{currentOrderCount}, #{currentStaffCount}, " +
            "#{saturationRate}, #{dynamicRadius}, #{baseRadius}, #{peakExpansionRatio}, " +
            "#{dynamicAdjustEnabled}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "geofenceId")
    int insert(DeliveryGeofence geofence);
    
    @Select("SELECT * FROM delivery_geofence WHERE geofence_id = #{geofenceId}")
    DeliveryGeofence selectById(Long geofenceId);
    
    @Select("SELECT * FROM delivery_geofence WHERE status = 1 ORDER BY geofence_id DESC")
    List<DeliveryGeofence> selectAllActive();
    
    @Select("SELECT * FROM delivery_geofence WHERE city_code = #{cityCode} AND status = 1")
    List<DeliveryGeofence> selectByCityCode(String cityCode);
    
    @Select("SELECT * FROM delivery_geofence WHERE center_lng BETWEEN #{minLng} AND #{maxLng} " +
            "AND center_lat BETWEEN #{minLat} AND #{maxLat} AND status = 1")
    List<DeliveryGeofence> selectInRange(@Param("minLng") BigDecimal minLng,
                                          @Param("maxLng") BigDecimal maxLng,
                                          @Param("minLat") BigDecimal minLat,
                                          @Param("maxLat") BigDecimal maxLat);
    
    @Update("UPDATE delivery_geofence SET " +
            "current_order_count = #{currentOrderCount}, " +
            "current_staff_count = #{currentStaffCount}, " +
            "saturation_rate = #{saturationRate}, " +
            "dynamic_radius = #{dynamicRadius}, " +
            "updated_at = NOW() " +
            "WHERE geofence_id = #{geofenceId}")
    int updateStats(DeliveryGeofence geofence);
    
    @Update("UPDATE delivery_geofence SET " +
            "dynamic_radius = #{dynamicRadius}, " +
            "updated_at = NOW() " +
            "WHERE geofence_id = #{geofenceId}")
    int updateDynamicRadius(@Param("geofenceId") Long geofenceId, 
                            @Param("dynamicRadius") Integer dynamicRadius);
    
    @Delete("DELETE FROM delivery_geofence WHERE geofence_id = #{geofenceId}")
    int deleteById(Long geofenceId);
    
    @Select("SELECT COUNT(*) FROM delivery_geofence WHERE status = 1")
    int countActive();
    
    @Select("SELECT SUM(current_order_count) FROM delivery_geofence WHERE status = 1")
    Integer sumTotalOrders();
    
    @Select("SELECT SUM(current_staff_count) FROM delivery_geofence WHERE status = 1")
    Integer sumTotalStaff();
}
