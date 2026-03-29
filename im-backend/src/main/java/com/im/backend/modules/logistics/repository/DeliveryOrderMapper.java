package com.im.backend.modules.logistics.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.logistics.entity.DeliveryOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配送订单数据访问层
 */
@Repository
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {

    /**
     * 根据状态查询订单列表
     */
    @Select("SELECT * FROM delivery_order WHERE status = #{status} AND deleted = 0 ORDER BY create_time ASC")
    List<DeliveryOrder> selectByStatus(@Param("status") Integer status);

    /**
     * 查询骑手的订单列表
     */
    @Select("SELECT * FROM delivery_order WHERE rider_id = #{riderId} AND deleted = 0 ORDER BY create_time DESC")
    List<DeliveryOrder> selectByRiderId(@Param("riderId") Long riderId);

    /**
     * 查询待分配订单列表
     */
    @Select("SELECT * FROM delivery_order WHERE status = 1 AND deleted = 0 ORDER BY create_time ASC LIMIT #{limit}")
    List<DeliveryOrder> selectPendingOrders(@Param("limit") Integer limit);

    /**
     * 更新订单状态和骑手
     */
    @Update("UPDATE delivery_order SET status = #{status}, rider_id = #{riderId}, rider_name = #{riderName}, " +
            "rider_phone = #{riderPhone}, update_time = NOW() WHERE id = #{orderId}")
    int assignRider(@Param("orderId") Long orderId, @Param("riderId") Long riderId, 
                    @Param("riderName") String riderName, @Param("riderPhone") String riderPhone,
                    @Param("status") Integer status);

    /**
     * 更新订单状态
     */
    @Update("UPDATE delivery_order SET status = #{status}, update_time = NOW() WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 分页查询订单
     */
    IPage<DeliveryOrder> selectPageByStatus(Page<DeliveryOrder> page, @Param("status") Integer status);
}
