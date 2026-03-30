package com.im.backend.modules.merchant.logistics.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.merchant.logistics.entity.LogisticsDeliveryOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物流配送订单数据访问层 - 功能#311: 本地物流配送调度
 */
@Repository
public interface LogisticsDeliveryOrderMapper extends BaseMapper<LogisticsDeliveryOrder> {

    /**
     * 查询骑手的配送订单
     */
    IPage<LogisticsDeliveryOrder> selectByRiderId(Page<LogisticsDeliveryOrder> page, 
                                                   @Param("riderId") Long riderId,
                                                   @Param("status") Integer status);

    /**
     * 查询商户的配送订单
     */
    IPage<LogisticsDeliveryOrder> selectByMerchantId(Page<LogisticsDeliveryOrder> page,
                                                      @Param("merchantId") Long merchantId,
                                                      @Param("status") Integer status);

    /**
     * 查询待分配订单
     */
    @Select("SELECT * FROM logistics_delivery_order WHERE status = 1 AND deleted = 0 ORDER BY create_time ASC LIMIT #{limit}")
    List<LogisticsDeliveryOrder> selectPendingOrders(@Param("limit") Integer limit);

    /**
     * 分配骑手
     */
    @Update("UPDATE logistics_delivery_order SET rider_id = #{riderId}, status = 2, update_time = NOW() WHERE id = #{orderId}")
    int assignRider(@Param("orderId") Long orderId, @Param("riderId") Long riderId);

    /**
     * 更新订单状态
     */
    @Update("UPDATE logistics_delivery_order SET status = #{status}, update_time = NOW() WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 完成配送
     */
    @Update("UPDATE logistics_delivery_order SET status = 5, actual_arrival_time = NOW(), update_time = NOW() WHERE id = #{orderId}")
    int completeDelivery(@Param("orderId") Long orderId);
}
