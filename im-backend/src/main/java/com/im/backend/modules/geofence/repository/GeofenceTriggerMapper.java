package com.im.backend.modules.geofence.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.geofence.entity.GeofenceTrigger;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 围栏触发记录Mapper
 */
public interface GeofenceTriggerMapper extends BaseMapper<GeofenceTrigger> {

    /**
     * 查询用户的最新触发记录
     */
    @Select("SELECT * FROM im_geofence_trigger WHERE user_id = #{userId} AND geofence_id = #{geofenceId} " +
            "AND deleted = 0 ORDER BY trigger_time DESC LIMIT 1")
    GeofenceTrigger selectLatestByUserAndGeofence(@Param("userId") Long userId, @Param("geofenceId") Long geofenceId);

    /**
     * 查询用户今日触发记录
     */
    @Select("SELECT * FROM im_geofence_trigger WHERE user_id = #{userId} AND DATE(trigger_time) = CURDATE() AND deleted = 0")
    List<GeofenceTrigger> selectTodayByUserId(@Param("userId") Long userId);

    /**
     * 分页查询围栏触发记录
     */
    IPage<GeofenceTrigger> selectPageByGeofenceId(Page<GeofenceTrigger> page, @Param("geofenceId") Long geofenceId);

    /**
     * 更新处理状态
     */
    @Update("UPDATE im_geofence_trigger SET process_status = #{status}, process_result = #{result} WHERE id = #{id}")
    int updateProcessStatus(@Param("id") Long id, @Param("status") String status, @Param("result") String result);

    /**
     * 查询待处理的触发记录
     */
    @Select("SELECT * FROM im_geofence_trigger WHERE process_status = 'PENDING' AND deleted = 0 LIMIT 100")
    List<GeofenceTrigger> selectPendingTriggers();

    /**
     * 统计用户今日进入某围栏次数
     */
    @Select("SELECT COUNT(*) FROM im_geofence_trigger WHERE user_id = #{userId} AND geofence_id = #{geofenceId} " +
            "AND trigger_type = 'ENTER' AND DATE(trigger_time) = CURDATE() AND deleted = 0")
    int countTodayEnterByUserAndGeofence(@Param("userId") Long userId, @Param("geofenceId") Long geofenceId);
}
