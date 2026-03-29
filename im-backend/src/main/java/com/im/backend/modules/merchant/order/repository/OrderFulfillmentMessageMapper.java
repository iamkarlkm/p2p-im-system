package com.im.backend.modules.merchant.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.order.entity.OrderFulfillmentMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * 订单履约消息Mapper
 */
public interface OrderFulfillmentMessageMapper extends BaseMapper<OrderFulfillmentMessage> {

    /**
     * 根据会话ID查询消息列表
     */
    @Select("SELECT * FROM im_order_fulfillment_message WHERE session_id = #{sessionId} AND deleted = 0 ORDER BY create_time ASC")
    List<OrderFulfillmentMessage> selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据订单ID查询消息列表
     */
    @Select("SELECT * FROM im_order_fulfillment_message WHERE order_id = #{orderId} AND deleted = 0 ORDER BY create_time ASC")
    List<OrderFulfillmentMessage> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 分页查询会话消息
     */
    List<OrderFulfillmentMessage> selectBySessionIdWithPage(@Param("sessionId") String sessionId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询未读消息
     */
    @Select("SELECT * FROM im_order_fulfillment_message WHERE session_id = #{sessionId} AND read_status = 0 AND sender_id != #{userId} AND deleted = 0")
    List<OrderFulfillmentMessage> selectUnreadMessages(@Param("sessionId") String sessionId, @Param("userId") Long userId);

    /**
     * 标记消息为已读
     */
    @Update("UPDATE im_order_fulfillment_message SET read_status = 1, read_time = NOW() WHERE session_id = #{sessionId} AND sender_id != #{userId} AND read_status = 0")
    int markAsRead(@Param("sessionId") String sessionId, @Param("userId") Long userId);

    /**
     * 查询最新消息
     */
    @Select("SELECT * FROM im_order_fulfillment_message WHERE session_id = #{sessionId} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    OrderFulfillmentMessage selectLatestMessage(@Param("sessionId") String sessionId);

    /**
     * 批量插入消息
     */
    int batchInsert(@Param("list") List<OrderFulfillmentMessage> messages);
}
