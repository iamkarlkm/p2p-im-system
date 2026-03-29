package com.im.backend.modules.merchant.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.order.entity.OrderDeliveryTracking;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import java.util.List;

/**
 * 订单配送追踪Mapper
 */
public interface OrderDeliveryTrackingMapper extends BaseMapper<OrderDeliveryTracking> {

    /**
     * 根据订单ID查询最新配送追踪记录
     */
    @Select("SELECT * FROM im_order_delivery_tracking WHERE order_id = #{orderId} ORDER BY location_time DESC LIMIT 1")
    OrderDeliveryTracking selectLatestByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单ID查询所有追踪记录
     */
    @Select("SELECT * FROM im_order_delivery_tracking WHERE order_id = #{orderId} ORDER BY location_time ASC")
    List<OrderDeliveryTracking> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据骑手ID查询当前配送订单
     */
    @Select("SELECT * FROM im_order_delivery_tracking WHERE rider_id = #{riderId} AND delivery_status IN (1,2,3,4) ORDER BY update_time DESC LIMIT 1")
    OrderDeliveryTracking selectCurrentByRiderId(@Param("riderId") Long riderId);

    /**
     * 批量插入位置记录
     */
    int batchInsert(@Param("list") List<OrderDeliveryTracking> records);

    /**
     * 查询订单的轨迹点(用于轨迹回放)
     */
    @Select("SELECT * FROM im_order_delivery_tracking WHERE order_id = #{orderId} AND location_time >= #{startTime} AND location_time <= #{endTime} ORDER BY location_time ASC")
    List<OrderDeliveryTracking> selectTrackPoints(@Param("orderId") Long orderId, @Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);
}
