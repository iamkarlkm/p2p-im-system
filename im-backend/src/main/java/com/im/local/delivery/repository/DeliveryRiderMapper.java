package com.im.local.delivery.repository;

import com.im.local.delivery.entity.DeliveryRider;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 骑手数据访问层
 */
@Mapper
public interface DeliveryRiderMapper {
    
    @Insert("INSERT INTO delivery_rider (user_id, real_name, phone, id_card, employee_no, " +
            "station_id, current_lat, current_lng, location_updated_at, status, " +
            "today_order_count, today_distance, rating, total_deliveries, auth_status, " +
            "work_city, work_district, account_status, created_at, updated_at) " +
            "VALUES (#{userId}, #{realName}, #{phone}, #{idCard}, #{employeeNo}, " +
            "#{stationId}, #{currentLat}, #{currentLng}, #{locationUpdatedAt}, #{status}, " +
            "#{todayOrderCount}, #{todayDistance}, #{rating}, #{totalDeliveries}, #{authStatus}, " +
            "#{workCity}, #{workDistrict}, #{accountStatus}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DeliveryRider rider);
    
    @Select("SELECT * FROM delivery_rider WHERE id = #{id}")
    DeliveryRider selectById(Long id);
    
    @Select("SELECT * FROM delivery_rider WHERE user_id = #{userId}")
    DeliveryRider selectByUserId(Long userId);
    
    @Select("SELECT * FROM delivery_rider WHERE phone = #{phone}")
    DeliveryRider selectByPhone(String phone);
    
    @Select("SELECT * FROM delivery_rider WHERE station_id = #{stationId} AND status = #{status}")
    List<DeliveryRider> selectByStationAndStatus(@Param("stationId") Long stationId, @Param("status") Integer status);
    
    @Select("SELECT * FROM delivery_rider WHERE status = 1 AND account_status = 1 " +
            "AND work_city = #{city} LIMIT #{limit}")
    List<DeliveryRider> selectAvailableByCity(@Param("city") String city, @Param("limit") Integer limit);
    
    @Update("UPDATE delivery_rider SET current_lat = #{lat}, current_lng = #{lng}, " +
            "location_updated_at = NOW(), updated_at = NOW() WHERE id = #{riderId}")
    int updateLocation(@Param("riderId") Long riderId, @Param("lat") java.math.BigDecimal lat, 
                       @Param("lng") java.math.BigDecimal lng);
    
    @Update("UPDATE delivery_rider SET status = #{status}, updated_at = NOW() WHERE id = #{riderId}")
    int updateStatus(@Param("riderId") Long riderId, @Param("status") Integer status);
    
    @Update("UPDATE delivery_rider SET today_order_count = today_order_count + 1, " +
            "total_deliveries = total_deliveries + 1, updated_at = NOW() WHERE id = #{riderId}")
    int incrementOrderCount(Long riderId);
    
    @Select("SELECT * FROM delivery_rider WHERE status = 1 AND account_status = 1")
    List<DeliveryRider> selectAllAvailable();
}
