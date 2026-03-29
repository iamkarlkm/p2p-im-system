package com.im.backend.modules.merchant.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.order.entity.OrderDeliveryException;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * 订单配送异常Mapper
 */
public interface OrderDeliveryExceptionMapper extends BaseMapper<OrderDeliveryException> {

    /**
     * 根据订单ID查询异常记录
     */
    @Select("SELECT * FROM im_order_delivery_exception WHERE order_id = #{orderId} ORDER BY create_time DESC")
    List<OrderDeliveryException> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据骑手ID查询异常记录
     */
    @Select("SELECT * FROM im_order_delivery_exception WHERE rider_id = #{riderId} AND handle_status = 0 ORDER BY create_time DESC")
    List<OrderDeliveryException> selectPendingByRiderId(@Param("riderId") Long riderId);

    /**
     * 更新处理状态
     */
    @Update("UPDATE im_order_delivery_exception SET handle_status = #{handleStatus}, handler_id = #{handlerId}, handle_result = #{handleResult}, handle_time = NOW(), update_time = NOW() WHERE id = #{id}")
    int updateHandleStatus(@Param("id") Long id, @Param("handleStatus") Integer handleStatus, @Param("handlerId") Long handlerId, @Param("handleResult") String handleResult);

    /**
     * 查询待处理异常数
     */
    @Select("SELECT COUNT(*) FROM im_order_delivery_exception WHERE handle_status = 0")
    int countPendingExceptions();
}
