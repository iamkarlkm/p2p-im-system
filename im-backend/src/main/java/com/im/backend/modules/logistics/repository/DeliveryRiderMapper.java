package com.im.backend.modules.logistics.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.logistics.entity.DeliveryRider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 骑手数据访问层
 */
@Repository
public interface DeliveryRiderMapper extends BaseMapper<DeliveryRider> {

    /**
     * 根据用户ID查询骑手
     */
    @Select("SELECT * FROM delivery_rider WHERE user_id = #{userId} AND deleted = 0")
    DeliveryRider selectByUserId(@Param("userId") Long userId);

    /**
     * 查询在线空闲的骑手
     */
    @Select("SELECT * FROM delivery_rider WHERE work_status = 1 AND audit_status = 1 AND deleted = 0")
    List<DeliveryRider> selectOnlineIdleRiders();

    /**
     * 查询在线骑手列表
     */
    @Select("SELECT * FROM delivery_rider WHERE work_status IN (1, 2) AND audit_status = 1 AND deleted = 0")
    List<DeliveryRider> selectOnlineRiders();

    /**
     * 更新骑手位置
     */
    @Update("UPDATE delivery_rider SET current_longitude = #{longitude}, current_latitude = #{latitude}, " +
            "location_update_time = NOW(), update_time = NOW() WHERE id = #{riderId}")
    int updateLocation(@Param("riderId") Long riderId, @Param("longitude") BigDecimal longitude, 
                       @Param("latitude") BigDecimal latitude);

    /**
     * 更新骑手工作状态
     */
    @Update("UPDATE delivery_rider SET work_status = #{workStatus}, update_time = NOW() WHERE id = #{riderId}")
    int updateWorkStatus(@Param("riderId") Long riderId, @Param("workStatus") Integer workStatus);

    /**
     * 增加今日订单数
     */
    @Update("UPDATE delivery_rider SET today_order_count = today_order_count + 1, " +
            "total_order_count = total_order_count + 1, update_time = NOW() WHERE id = #{riderId}")
    int incrementOrderCount(@Param("riderId") Long riderId);
}
