package com.im.backend.modules.delivery.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.delivery.entity.DeliveryRider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 配送骑手Mapper
 */
public interface DeliveryRiderMapper extends BaseMapper<DeliveryRider> {
    
    @Select("SELECT * FROM delivery_rider WHERE status = 1 AND auth_status = 2 " +
            "AND work_status = 1 AND deleted = 0")
    List<DeliveryRider> selectOnlineAvailableRiders();
    
    @Update("UPDATE delivery_rider SET work_status = #{status}, " +
            "last_online_time = NOW(), update_time = NOW() WHERE id = #{riderId}")
    int updateWorkStatus(@Param("riderId") Long riderId, @Param("status") Integer status);
    
    @Update("UPDATE delivery_rider SET current_lat = #{lat}, current_lng = #{lng}, " +
            "location_update_time = NOW(), update_time = NOW() WHERE id = #{riderId}")
    int updateLocation(@Param("riderId") Long riderId, 
                       @Param("lat") java.math.BigDecimal lat, 
                       @Param("lng") java.math.BigDecimal lng);
}
