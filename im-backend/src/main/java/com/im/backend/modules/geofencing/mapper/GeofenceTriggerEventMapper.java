package com.im.backend.modules.geofencing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.geofencing.entity.GeofenceTriggerEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 围栏触发事件Mapper接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Mapper
public interface GeofenceTriggerEventMapper extends BaseMapper<GeofenceTriggerEvent> {

    /**
     * 查询用户围栏历史
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件列表
     */
    @Select("SELECT * FROM geofence_trigger_event WHERE user_id = #{userId} AND trigger_time BETWEEN #{startTime} AND #{endTime} " +
            "AND deleted = 0 ORDER BY trigger_time DESC")
    List<GeofenceTriggerEvent> selectByUserAndTimeRange(@Param("userId") Long userId, 
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 查询围栏触发统计
     * @param geofenceId 围栏ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 触发次数
     */
    @Select("SELECT COUNT(*) FROM geofence_trigger_event WHERE geofence_id = #{geofenceId} " +
            "AND trigger_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0")
    Integer countByGeofenceAndTimeRange(@Param("geofenceId") Long geofenceId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 查询用户最后触发事件
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     * @return 事件
     */
    @Select("SELECT * FROM geofence_trigger_event WHERE user_id = #{userId} AND geofence_id = #{geofenceId} " +
            "AND deleted = 0 ORDER BY trigger_time DESC LIMIT 1")
    GeofenceTriggerEvent selectLastByUserAndGeofence(@Param("userId") Long userId, @Param("geofenceId") Long geofenceId);

    /**
     * 查询指定会话的事件
     * @param sessionId 会话ID
     * @return 事件列表
     */
    @Select("SELECT * FROM geofence_trigger_event WHERE session_id = #{sessionId} AND deleted = 0 ORDER BY trigger_time")
    List<GeofenceTriggerEvent> selectBySessionId(@Param("sessionId") String sessionId);
}
