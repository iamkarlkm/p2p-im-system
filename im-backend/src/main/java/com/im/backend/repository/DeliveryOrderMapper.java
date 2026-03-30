package com.im.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.entity.DeliveryOrder;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 配送订单Mapper
 */
@Mapper
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {

    /**
     * 根据订单编号查询
     */
    @Select("SELECT * FROM delivery_order WHERE order_no = #{orderNo} AND deleted = 0")
    DeliveryOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据商户订单ID查询
     */
    @Select("SELECT * FROM delivery_order WHERE merchant_order_id = #{merchantOrderId} AND deleted = 0")
    List<DeliveryOrder> selectByMerchantOrderId(@Param("merchantOrderId") Long merchantOrderId);

    /**
     * 查询用户配送订单
     */
    @Select("SELECT * FROM delivery_order WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<DeliveryOrder> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询骑手配送订单
     */
    @Select("SELECT * FROM delivery_order WHERE rider_id = #{riderId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<DeliveryOrder> selectByRiderId(@Param("riderId") Long riderId, @Param("limit") Integer limit);

    /**
     * 查询待分配订单
     */
    @Select("SELECT * FROM delivery_order WHERE status = 'PENDING' AND deleted = 0 ORDER BY create_time ASC LIMIT #{limit}")
    List<DeliveryOrder> selectPendingOrders(@Param("limit") Integer limit);

    /**
     * 查询骑手当前配送中订单
     */
    @Select("SELECT * FROM delivery_order WHERE rider_id = #{riderId} AND status IN ('ASSIGNED', 'PICKING', 'DELIVERING') AND deleted = 0")
    List<DeliveryOrder> selectRiderCurrentOrders(@Param("riderId") Long riderId);

    /**
     * 统计今日订单数
     */
    @Select("SELECT COUNT(*) FROM delivery_order WHERE create_time >= #{startOfDay} AND deleted = 0")
    Integer countTodayOrders(@Param("startOfDay") LocalDateTime startOfDay);

    /**
     * 更新订单状态
     */
    @Update("UPDATE delivery_order SET status = #{newStatus}, update_time = NOW() WHERE id = #{orderId} AND status = #{expectedStatus}")
    int updateStatus(@Param("orderId") Long orderId, @Param("expectedStatus") String expectedStatus, @Param("newStatus") String newStatus);

    /**
     * 分配骑手
     */
    @Update("UPDATE delivery_order SET rider_id = #{riderId}, status = 'ASSIGNED', assigned_time = NOW(), update_time = NOW() WHERE id = #{orderId} AND status = 'PENDING'")
    int assignRider(@Param("orderId") Long orderId, @Param("riderId") Long riderId);
}
