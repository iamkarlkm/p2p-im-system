package com.im.backend.modules.merchant.logistics.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.logistics.entity.LogisticsRider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 配送骑手数据访问层 - 功能#311: 本地物流配送调度
 */
@Repository
public interface LogisticsRiderMapper extends BaseMapper<LogisticsRider> {

    /**
     * 根据用户ID查询骑手
     */
    @Select("SELECT * FROM logistics_rider WHERE user_id = #{userId} AND deleted = 0 LIMIT 1")
    LogisticsRider selectByUserId(@Param("userId") Long userId);

    /**
     * 查询在线空闲骑手
     */
    @Select("SELECT * FROM logistics_rider WHERE work_status = 1 AND deleted = 0 LIMIT #{limit}")
    List<LogisticsRider> selectOnlineRiders(@Param("limit") Integer limit);

    /**
     * 更新骑手位置
     */
    @Update("UPDATE logistics_rider SET current_lng = #{lng}, current_lat = #{lat}, location_update_time = NOW() WHERE id = #{riderId}")
    int updateLocation(@Param("riderId") Long riderId, @Param("lng") BigDecimal lng, @Param("lat") BigDecimal lat);

    /**
     * 更新工作状态
     */
    @Update("UPDATE logistics_rider SET work_status = #{status} WHERE id = #{riderId}")
    int updateWorkStatus(@Param("riderId") Long riderId, @Param("status") Integer status);

    /**
     * 增加今日订单数
     */
    @Update("UPDATE logistics_rider SET today_order_count = today_order_count + 1 WHERE id = #{riderId}")
    int incrementOrderCount(@Param("riderId") Long riderId);
}
