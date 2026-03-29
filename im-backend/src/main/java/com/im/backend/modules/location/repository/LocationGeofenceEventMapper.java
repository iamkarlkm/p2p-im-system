package com.im.backend.modules.location.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.location.model.entity.LocationGeofenceEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 位置围栏事件数据访问层
 */
@Mapper
public interface LocationGeofenceEventMapper extends BaseMapper<LocationGeofenceEvent> {

    /**
     * 查询会话的围栏事件
     */
    @Select("SELECT * FROM location_geofence_event WHERE session_id = #{sessionId} " +
            "ORDER BY trigger_time DESC LIMIT #{limit}")
    List<LocationGeofenceEvent> selectBySessionId(@Param("sessionId") String sessionId, @Param("limit") Integer limit);

    /**
     * 查询用户的最新事件
     */
    @Select("SELECT * FROM location_geofence_event WHERE session_id = #{sessionId} AND user_id = #{userId} " +
            "AND event_type = #{eventType} ORDER BY trigger_time DESC LIMIT 1")
    LocationGeofenceEvent selectLatestByUserAndType(@Param("sessionId") String sessionId,
                                                     @Param("userId") Long userId,
                                                     @Param("eventType") String eventType);

    /**
     * 更新事件处理状态
     */
    @Update("UPDATE location_geofence_event SET status = #{status}, im_message_id = #{messageId}, " +
            "notification_sent = true, notification_time = NOW(), updated_at = NOW() WHERE id = #{id}")
    int updateNotificationStatus(@Param("id") Long id, @Param("status") String status,
                                  @Param("messageId") String messageId);

    /**
     * 查询待处理的到达事件
     */
    @Select("SELECT * FROM location_geofence_event WHERE status = 'PENDING' " +
            "AND event_type = 'ENTER_DESTINATION' ORDER BY created_at ASC LIMIT 100")
    List<LocationGeofenceEvent> selectPendingArrivalEvents();
}
