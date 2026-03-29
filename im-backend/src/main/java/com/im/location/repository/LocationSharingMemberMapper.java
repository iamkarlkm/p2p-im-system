package com.im.location.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.location.entity.LocationSharingMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 位置共享成员 Mapper
 */
public interface LocationSharingMemberMapper extends BaseMapper<LocationSharingMember> {
    
    /**
     * 根据会话ID和用户ID查询
     */
    @Select("SELECT * FROM location_sharing_member WHERE session_id = #{sessionId} AND user_id = #{userId}")
    LocationSharingMember selectBySessionAndUser(@Param("sessionId") String sessionId, @Param("userId") Long userId);
    
    /**
     * 查询会话的所有成员
     */
    @Select("SELECT * FROM location_sharing_member WHERE session_id = #{sessionId} ORDER BY join_time ASC")
    List<LocationSharingMember> selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 查询会话的活跃成员
     */
    @Select("SELECT * FROM location_sharing_member WHERE session_id = #{sessionId} AND member_status = 1")
    List<LocationSharingMember> selectActiveMembers(@Param("sessionId") String sessionId);
    
    /**
     * 更新成员位置
     */
    @Update("UPDATE location_sharing_member SET longitude = #{longitude}, latitude = #{latitude}, " +
            "accuracy = #{accuracy}, altitude = #{altitude}, speed = #{speed}, bearing = #{bearing}, " +
            "battery_level = #{batteryLevel}, location_update_time = NOW(), update_time = NOW() " +
            "WHERE session_id = #{sessionId} AND user_id = #{userId}")
    int updateLocation(@Param("sessionId") String sessionId, @Param("userId") Long userId,
                       @Param("longitude") Double longitude, @Param("latitude") Double latitude,
                       @Param("accuracy") Double accuracy, @Param("altitude") Double altitude,
                       @Param("speed") Double speed, @Param("bearing") Double bearing,
                       @Param("batteryLevel") Integer batteryLevel);
    
    /**
     * 更新进入围栏状态
     */
    @Update("UPDATE location_sharing_member SET in_geofence = 1, enter_geofence_time = NOW(), update_time = NOW() " +
            "WHERE session_id = #{sessionId} AND user_id = #{userId}")
    int updateEnterGeofence(@Param("sessionId") String sessionId, @Param("userId") Long userId);
    
    /**
     * 更新离开围栏状态
     */
    @Update("UPDATE location_sharing_member SET in_geofence = 0, update_time = NOW() " +
            "WHERE session_id = #{sessionId} AND user_id = #{userId}")
    int updateExitGeofence(@Param("sessionId") String sessionId, @Param("userId") Long userId);
    
    /**
     * 更新到达状态
     */
    @Update("UPDATE location_sharing_member SET arrived_status = #{status}, eta_minutes = #{etaMinutes}, update_time = NOW() " +
            "WHERE session_id = #{sessionId} AND user_id = #{userId}")
    int updateArrivalStatus(@Param("sessionId") String sessionId, @Param("userId") Long userId,
                            @Param("status") Integer status, @Param("etaMinutes") Integer etaMinutes);
    
    /**
     * 更新成员状态
     */
    @Update("UPDATE location_sharing_member SET member_status = #{status}, leave_time = CASE WHEN #{status} = 2 THEN NOW() ELSE leave_time END, " +
            "update_time = NOW() WHERE session_id = #{sessionId} AND user_id = #{userId}")
    int updateMemberStatus(@Param("sessionId") String sessionId, @Param("userId") Long userId, @Param("status") Integer status);
}
