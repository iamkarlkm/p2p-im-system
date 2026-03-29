package com.im.backend.modules.merchant.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.order.entity.OrderFulfillmentSession;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * 订单履约会话Mapper
 */
public interface OrderFulfillmentSessionMapper extends BaseMapper<OrderFulfillmentSession> {

    /**
     * 根据订单ID查询会话
     */
    @Select("SELECT * FROM im_order_fulfillment_session WHERE order_id = #{orderId} AND deleted = 0")
    OrderFulfillmentSession selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据会话ID查询
     */
    @Select("SELECT * FROM im_order_fulfillment_session WHERE session_id = #{sessionId} AND deleted = 0")
    OrderFulfillmentSession selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据用户ID查询活跃会话列表
     */
    @Select("SELECT * FROM im_order_fulfillment_session WHERE user_id = #{userId} AND status = 0 AND deleted = 0 ORDER BY create_time DESC")
    List<OrderFulfillmentSession> selectActiveByUserId(@Param("userId") Long userId);

    /**
     * 根据商户ID查询活跃会话列表
     */
    @Select("SELECT * FROM im_order_fulfillment_session WHERE merchant_id = #{merchantId} AND status = 0 AND deleted = 0 ORDER BY create_time DESC")
    List<OrderFulfillmentSession> selectActiveByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 根据骑手ID查询活跃会话列表
     */
    @Select("SELECT * FROM im_order_fulfillment_session WHERE rider_id = #{riderId} AND status = 0 AND deleted = 0 ORDER BY create_time DESC")
    List<OrderFulfillmentSession> selectActiveByRiderId(@Param("riderId") Long riderId);

    /**
     * 更新骑手ID
     */
    @Update("UPDATE im_order_fulfillment_session SET rider_id = #{riderId}, update_time = NOW() WHERE order_id = #{orderId}")
    int updateRiderId(@Param("orderId") Long orderId, @Param("riderId") Long riderId);

    /**
     * 更新会话状态
     */
    @Update("UPDATE im_order_fulfillment_session SET status = #{status}, update_time = NOW() WHERE session_id = #{sessionId}")
    int updateStatus(@Param("sessionId") String sessionId, @Param("status") Integer status);

    /**
     * 结束会话
     */
    @Update("UPDATE im_order_fulfillment_session SET status = 2, end_time = NOW(), update_time = NOW() WHERE session_id = #{sessionId}")
    int endSession(@Param("sessionId") String sessionId);

    /**
     * 更新预计送达时间
     */
    @Update("UPDATE im_order_fulfillment_session SET estimated_delivery_time = #{estimatedTime}, update_time = NOW() WHERE order_id = #{orderId}")
    int updateEstimatedDeliveryTime(@Param("orderId") Long orderId, @Param("estimatedTime") java.time.LocalDateTime estimatedTime);
}
