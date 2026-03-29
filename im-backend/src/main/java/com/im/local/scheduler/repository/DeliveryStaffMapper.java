package com.im.local.scheduler.repository;

import com.im.local.scheduler.entity.DeliveryStaff;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.math.BigDecimal;

/**
 * 骑手数据访问层
 */
@Mapper
public interface DeliveryStaffMapper {
    
    @Insert("INSERT INTO delivery_staff (staff_name, phone, staff_type, status, " +
            "current_lng, current_lat, current_geohash, current_geofence_id, " +
            "today_completed_orders, today_delivery_distance, avg_delivery_time, " +
            "rating, current_order_count, max_order_capacity, delivery_area_id, " +
            "enabled, created_at, updated_at) " +
            "VALUES (#{staffName}, #{phone}, #{staffType}, #{status}, " +
            "#{currentLng}, #{currentLat}, #{currentGeohash}, #{currentGeofenceId}, " +
            "#{todayCompletedOrders}, #{todayDeliveryDistance}, #{avgDeliveryTime}, " +
            "#{rating}, #{currentOrderCount}, #{maxOrderCapacity}, #{deliveryAreaId}, " +
            "#{enabled}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "staffId")
    int insert(DeliveryStaff staff);
    
    @Select("SELECT * FROM delivery_staff WHERE staff_id = #{staffId}")
    DeliveryStaff selectById(Long staffId);
    
    @Select("SELECT * FROM delivery_staff WHERE phone = #{phone}")
    DeliveryStaff selectByPhone(String phone);
    
    @Select("SELECT * FROM delivery_staff WHERE status = #{status} AND enabled = 1")
    List<DeliveryStaff> selectByStatus(Integer status);
    
    @Select("SELECT * FROM delivery_staff WHERE current_geofence_id = #{geofenceId} AND enabled = 1")
    List<DeliveryStaff> selectByGeofenceId(Long geofenceId);
    
    @Select("SELECT * FROM delivery_staff WHERE status = 1 AND enabled = 1 " +
            "AND current_lng BETWEEN #{minLng} AND #{maxLng} " +
            "AND current_lat BETWEEN #{minLat} AND #{maxLat}")
    List<DeliveryStaff> selectIdleStaffInRange(@Param("minLng") BigDecimal minLng,
                                                @Param("maxLng") BigDecimal maxLng,
                                                @Param("minLat") BigDecimal minLat,
                                                @Param("maxLat") BigDecimal maxLat);
    
    @Update("UPDATE delivery_staff SET " +
            "current_lng = #{currentLng}, " +
            "current_lat = #{currentLat}, " +
            "current_geohash = #{currentGeohash}, " +
            "current_geofence_id = #{currentGeofenceId}, " +
            "location_updated_at = NOW(), " +
            "updated_at = NOW() " +
            "WHERE staff_id = #{staffId}")
    int updateLocation(DeliveryStaff staff);
    
    @Update("UPDATE delivery_staff SET " +
            "status = #{status}, " +
            "updated_at = NOW() " +
            "WHERE staff_id = #{staffId}")
    int updateStatus(@Param("staffId") Long staffId, @Param("status") Integer status);
    
    @Update("UPDATE delivery_staff SET " +
            "current_order_count = #{currentOrderCount}, " +
            "today_completed_orders = #{todayCompletedOrders}, " +
            "updated_at = NOW() " +
            "WHERE staff_id = #{staffId}")
    int updateOrderStats(DeliveryStaff staff);
    
    @Select("SELECT COUNT(*) FROM delivery_staff WHERE status = #{status} AND enabled = 1")
    int countByStatus(Integer status);
    
    @Select("SELECT * FROM delivery_staff WHERE enabled = 1 ORDER BY staff_id DESC LIMIT #{limit}")
    List<DeliveryStaff> selectRecent(@Param("limit") Integer limit);
}
