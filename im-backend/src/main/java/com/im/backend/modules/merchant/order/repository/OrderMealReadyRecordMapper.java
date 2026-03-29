package com.im.backend.modules.merchant.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.order.entity.OrderMealReadyRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 订单出餐记录Mapper
 */
public interface OrderMealReadyRecordMapper extends BaseMapper<OrderMealReadyRecord> {

    /**
     * 根据订单ID查询出餐记录
     */
    @Select("SELECT * FROM im_order_meal_ready_record WHERE order_id = #{orderId} ORDER BY create_time DESC LIMIT 1")
    OrderMealReadyRecord selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 更新出餐状态
     */
    @Update("UPDATE im_order_meal_ready_record SET status = #{status}, meal_ready_time = NOW(), update_time = NOW() WHERE order_id = #{orderId}")
    int updateMealStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 查询商户待出餐订单数
     */
    @Select("SELECT COUNT(*) FROM im_order_meal_ready_record WHERE merchant_id = #{merchantId} AND status = 0")
    int countPendingByMerchantId(@Param("merchantId") Long merchantId);
}
