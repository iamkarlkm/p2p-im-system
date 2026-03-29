package com.im.backend.modules.delivery.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.delivery.entity.DeliveryOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 配送订单Mapper
 */
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {
    
    @Select("SELECT * FROM delivery_order WHERE rider_id = #{riderId} " +
            "AND status IN (2,3,4,5,6) AND deleted = 0 ORDER BY create_time DESC")
    List<DeliveryOrder> selectActiveOrdersByRider(@Param("riderId") Long riderId);
    
    @Select("SELECT * FROM delivery_order WHERE status = 0 AND deleted = 0 " +
            "ORDER BY create_time ASC LIMIT 100")
    List<DeliveryOrder> selectPendingOrders();
    
    @Update("UPDATE delivery_order SET rider_id = #{riderId}, status = 2, " +
            "assigned_time = NOW(), update_time = NOW() WHERE id = #{orderId} " +
            "AND status = 0")
    int assignOrder(@Param("orderId") Long orderId, @Param("riderId") Long riderId);
    
    @Update("UPDATE delivery_order SET status = #{status}, update_time = NOW() " +
            "WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
}
