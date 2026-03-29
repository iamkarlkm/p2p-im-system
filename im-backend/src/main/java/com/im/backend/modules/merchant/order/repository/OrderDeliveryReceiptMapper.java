package com.im.backend.modules.merchant.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.order.entity.OrderDeliveryReceipt;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 订单配送签收Mapper
 */
public interface OrderDeliveryReceiptMapper extends BaseMapper<OrderDeliveryReceipt> {

    /**
     * 根据订单ID查询签收记录
     */
    @Select("SELECT * FROM im_order_delivery_receipt WHERE order_id = #{orderId}")
    OrderDeliveryReceipt selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据签收码查询
     */
    @Select("SELECT * FROM im_order_delivery_receipt WHERE receipt_code = #{receiptCode}")
    OrderDeliveryReceipt selectByReceiptCode(@Param("receiptCode") String receiptCode);

    /**
     * 更新签收状态
     */
    @Update("UPDATE im_order_delivery_receipt SET status = #{status}, receipt_time = NOW(), update_time = NOW() WHERE order_id = #{orderId}")
    int updateReceiptStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 检查签收码是否存在
     */
    @Select("SELECT COUNT(*) FROM im_order_delivery_receipt WHERE receipt_code = #{receiptCode}")
    int countByReceiptCode(@Param("receiptCode") String receiptCode);
}
