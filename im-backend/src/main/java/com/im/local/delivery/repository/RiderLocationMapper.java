package com.im.local.delivery.repository;

import com.im.local.delivery.entity.RiderLocation;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 骑手位置轨迹数据访问层
 */
@Mapper
public interface RiderLocationMapper {
    
    @Insert("INSERT INTO rider_location (rider_id, delivery_order_id, lat, lng, accuracy, " +
            "altitude, speed, direction, located_at, geo_hash, address, location_type, " +
            "battery_level, is_mock, created_at) " +
            "VALUES (#{riderId}, #{deliveryOrderId}, #{lat}, #{lng}, #{accuracy}, " +
            "#{altitude}, #{speed}, #{direction}, #{locatedAt}, #{geoHash}, #{address}, " +
            "#{locationType}, #{batteryLevel}, #{isMock}, NOW())")
    int insert(RiderLocation location);
    
    @Select("SELECT * FROM rider_location WHERE id = #{id}")
    RiderLocation selectById(Long id);
    
    @Select("SELECT * FROM rider_location WHERE rider_id = #{riderId} " +
            "AND created_at >= DATE_SUB(NOW(), INTERVAL #{hours} HOUR) " +
            "ORDER BY located_at DESC")
    List<RiderLocation> selectRecentByRider(@Param("riderId") Long riderId, @Param("hours") Integer hours);
    
    @Select("SELECT * FROM rider_location WHERE delivery_order_id = #{orderId} " +
            "ORDER BY located_at ASC")
    List<RiderLocation> selectByDeliveryOrder(Long orderId);
    
    @Select("SELECT * FROM rider_location WHERE rider_id = #{riderId} " +
            "ORDER BY located_at DESC LIMIT 1")
    RiderLocation selectLatestByRider(Long riderId);
    
    @Insert("<script>" +
            "INSERT INTO rider_location (rider_id, delivery_order_id, lat, lng, accuracy, " +
            "altitude, speed, direction, located_at, geo_hash, location_type, created_at) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.riderId}, #{item.deliveryOrderId}, #{item.lat}, #{item.lng}, #{item.accuracy}, " +
            "#{item.altitude}, #{item.speed}, #{item.direction}, #{item.locatedAt}, #{item.geoHash}, " +
            "#{item.locationType}, NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<RiderLocation> locations);
}
