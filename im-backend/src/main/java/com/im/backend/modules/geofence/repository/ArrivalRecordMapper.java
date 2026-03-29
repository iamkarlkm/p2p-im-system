package com.im.backend.modules.geofence.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.geofence.entity.ArrivalRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 到店记录Mapper
 */
public interface ArrivalRecordMapper extends BaseMapper<ArrivalRecord> {

    /**
     * 查询用户在店的记录
     */
    @Select("SELECT * FROM im_arrival_record WHERE user_id = #{userId} AND store_id = #{storeId} " +
            "AND status = 'IN_STORE' AND deleted = 0 ORDER BY enter_time DESC LIMIT 1")
    ArrivalRecord selectInStoreRecord(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 查询用户今日到店次数
     */
    @Select("SELECT COUNT(*) FROM im_arrival_record WHERE user_id = #{userId} AND store_id = #{storeId} " +
            "AND DATE(enter_time) = CURDATE() AND deleted = 0")
    int countTodayArrival(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 分页查询用户到店记录
     */
    IPage<ArrivalRecord> selectPageByUserId(Page<ArrivalRecord> page, @Param("userId") Long userId);

    /**
     * 分页查询门店到店记录
     */
    IPage<ArrivalRecord> selectPageByStoreId(Page<ArrivalRecord> page, @Param("storeId") Long storeId);

    /**
     * 更新离店信息
     */
    @Update("UPDATE im_arrival_record SET leave_time = #{leaveTime}, leave_longitude = #{leaveLng}, " +
            "leave_latitude = #{leaveLat}, stay_duration_minutes = #{duration}, status = 'LEFT' " +
            "WHERE id = #{id}")
    int updateLeaveInfo(@Param("id") Long id, @Param("leaveTime") LocalDateTime leaveTime,
                        @Param("leaveLng") Double leaveLng, @Param("leaveLat") Double leaveLat,
                        @Param("duration") Integer duration);

    /**
     * 更新服务推送状态
     */
    @Update("UPDATE im_arrival_record SET service_pushed = 1 WHERE id = #{id}")
    int updateServicePushed(@Param("id") Long id);

    /**
     * 查询门店今日到店人数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM im_arrival_record WHERE store_id = #{storeId} " +
            "AND DATE(enter_time) = CURDATE() AND deleted = 0")
    int countTodayUniqueUsers(@Param("storeId") Long storeId);

    /**
     * 查询门店当前在店人数
     */
    @Select("SELECT COUNT(*) FROM im_arrival_record WHERE store_id = #{storeId} " +
            "AND status = 'IN_STORE' AND deleted = 0")
    int countCurrentInStore(@Param("storeId") Long storeId);
}
