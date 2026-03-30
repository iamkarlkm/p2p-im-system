package com.im.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.entity.DeliveryRider;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 配送骑手Mapper
 */
@Mapper
public interface DeliveryRiderMapper extends BaseMapper<DeliveryRider> {

    /**
     * 根据用户ID查询
     */
    @Select("SELECT * FROM delivery_rider WHERE user_id = #{userId} AND deleted = 0")
    DeliveryRider selectByUserId(@Param("userId") Long userId);

    /**
     * 根据手机号查询
     */
    @Select("SELECT * FROM delivery_rider WHERE phone = #{phone} AND deleted = 0")
    DeliveryRider selectByPhone(@Param("phone") String phone);

    /**
     * 查询空闲骑手
     */
    @Select("SELECT * FROM delivery_rider WHERE work_status = 'IDLE' AND account_status = 'ACTIVE' AND audit_status = 'APPROVED' AND deleted = 0 LIMIT #{limit}")
    List<DeliveryRider> selectIdleRiders(@Param("limit") Integer limit);

    /**
     * 查询附近空闲骑手(简化版，实际应用需要Geo计算)
     */
    @Select("SELECT * FROM delivery_rider WHERE work_status = 'IDLE' AND account_status = 'ACTIVE' AND audit_status = 'APPROVED' AND deleted = 0 " +
            "ORDER BY ABS(current_longitude - #{longitude}) + ABS(current_latitude - #{latitude}) ASC LIMIT #{limit}")
    List<DeliveryRider> selectNearbyIdleRiders(@Param("longitude") Double longitude, @Param("latitude") Double latitude, @Param("limit") Integer limit);

    /**
     * 更新骑手位置
     */
    @Update("UPDATE delivery_rider SET current_longitude = #{longitude}, current_latitude = #{latitude}, location_update_time = NOW(), update_time = NOW() WHERE id = #{riderId}")
    int updateLocation(@Param("riderId") Long riderId, @Param("longitude") Double longitude, @Param("latitude") Double latitude);

    /**
     * 更新工作状态
     */
    @Update("UPDATE delivery_rider SET work_status = #{workStatus}, update_time = NOW() WHERE id = #{riderId}")
    int updateWorkStatus(@Param("riderId") Long riderId, @Param("workStatus") String workStatus);

    /**
     * 增加今日订单计数
     */
    @Update("UPDATE delivery_rider SET today_order_count = today_order_count + 1, total_order_count = total_order_count + 1, update_time = NOW() WHERE id = #{riderId}")
    int incrementOrderCount(@Param("riderId") Long riderId);

    /**
     * 增加今日完成计数
     */
    @Update("UPDATE delivery_rider SET today_completed_count = today_completed_count + 1, total_completed_count = total_completed_count + 1, update_time = NOW() WHERE id = #{riderId}")
    int incrementCompletedCount(@Param("riderId") Long riderId);

    /**
     * 重置今日统计
     */
    @Update("UPDATE delivery_rider SET today_order_count = 0, today_completed_count = 0, update_time = NOW()")
    int resetDailyStats();
}
