package com.im.location.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.location.entity.GeofenceTriggerRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地理围栏触发记录 Mapper
 */
public interface GeofenceTriggerRecordMapper extends BaseMapper<GeofenceTriggerRecord> {
    
    /**
     * 查询围栏的触发记录
     */
    @Select("SELECT * FROM geofence_trigger_record WHERE geofence_id = #{geofenceId} ORDER BY trigger_time DESC")
    List<GeofenceTriggerRecord> selectByGeofenceId(@Param("geofenceId") String geofenceId);
    
    /**
     * 查询会话的触发记录
     */
    @Select("SELECT * FROM geofence_trigger_record WHERE session_id = #{sessionId} ORDER BY trigger_time DESC LIMIT 100")
    List<GeofenceTriggerRecord> selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 查询用户最近的触发记录
     */
    @Select("SELECT * FROM geofence_trigger_record WHERE geofence_id = #{geofenceId} AND user_id = #{userId} " +
            "ORDER BY trigger_time DESC LIMIT 1")
    GeofenceTriggerRecord selectLatestByUserAndGeofence(@Param("geofenceId") String geofenceId, @Param("userId") Long userId);
    
    /**
     * 查询未处理的触发记录
     */
    @Select("SELECT * FROM geofence_trigger_record WHERE process_status = 0 ORDER BY create_time ASC LIMIT 50")
    List<GeofenceTriggerRecord> selectUnprocessedRecords();
}
