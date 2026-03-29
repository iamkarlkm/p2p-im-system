package com.im.backend.modules.logistics.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.logistics.entity.RiderLocationTrace;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 骑手位置轨迹数据访问层
 */
@Repository
public interface RiderLocationTraceMapper extends BaseMapper<RiderLocationTrace> {

    /**
     * 查询订单的配送轨迹
     */
    @Select("SELECT * FROM rider_location_trace WHERE order_id = #{orderId} ORDER BY report_time ASC")
    List<RiderLocationTrace> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询骑手某段时间内的轨迹
     */
    @Select("SELECT * FROM rider_location_trace WHERE rider_id = #{riderId} " +
            "AND report_time >= #{startTime} AND report_time <= #{endTime} ORDER BY report_time ASC")
    List<RiderLocationTrace> selectByRiderAndTimeRange(@Param("riderId") Long riderId,
                                                       @Param("startTime") String startTime,
                                                       @Param("endTime") String endTime);

    /**
     * 查询骑手的最新位置
     */
    @Select("SELECT * FROM rider_location_trace WHERE rider_id = #{riderId} ORDER BY report_time DESC LIMIT 1")
    RiderLocationTrace selectLatestByRiderId(@Param("riderId") Long riderId);
}
