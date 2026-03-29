package com.im.backend.modules.delivery.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.delivery.entity.RiderLocation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 骑手位置Mapper
 */
public interface RiderLocationMapper extends BaseMapper<RiderLocation> {
    
    @Select("SELECT * FROM rider_location WHERE rider_id = #{riderId} " +
            "AND record_time >= DATE_SUB(NOW(), INTERVAL 2 HOUR) " +
            "AND deleted = 0 ORDER BY record_time ASC")
    List<RiderLocation> selectRecentLocations(@Param("riderId") Long riderId);
    
    @Select("SELECT * FROM rider_location WHERE rider_id = #{riderId} " +
            "AND deleted = 0 ORDER BY record_time DESC LIMIT 1")
    RiderLocation selectLatestLocation(@Param("riderId") Long riderId);
}
