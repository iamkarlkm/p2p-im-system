package com.im.backend.modules.geofencing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.geofencing.entity.UserGeofenceState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户围栏状态Mapper接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Mapper
public interface UserGeofenceStateMapper extends BaseMapper<UserGeofenceState> {

    /**
     * 查询用户当前所在的所有围栏
     * @param userId 用户ID
     * @return 围栏状态列表
     */
    @Select("SELECT * FROM user_geofence_state WHERE user_id = #{userId} AND deleted = 0")
    List<UserGeofenceState> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户在特定围栏的状态
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     * @return 围栏状态
     */
    @Select("SELECT * FROM user_geofence_state WHERE user_id = #{userId} AND geofence_id = #{geofenceId} AND deleted = 0")
    UserGeofenceState selectByUserAndGeofence(@Param("userId") Long userId, @Param("geofenceId") Long geofenceId);

    /**
     * 更新围栏状态
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     * @param state 新状态
     * @return 影响行数
     */
    @Update("UPDATE user_geofence_state SET current_state = #{state}, update_time = NOW() " +
            "WHERE user_id = #{userId} AND geofence_id = #{geofenceId}")
    int updateState(@Param("userId") Long userId, @Param("geofenceId") Long geofenceId, @Param("state") String state);

    /**
     * 查询订阅了指定围栏的所有用户
     * @param geofenceId 围栏ID
     * @return 用户状态列表
     */
    @Select("SELECT * FROM user_geofence_state WHERE geofence_id = #{geofenceId} AND subscribed = 1 AND deleted = 0")
    List<UserGeofenceState> selectSubscribedByGeofence(@Param("geofenceId") Long geofenceId);

    /**
     * 查询用户订阅的围栏
     * @param userId 用户ID
     * @return 围栏状态列表
     */
    @Select("SELECT * FROM user_geofence_state WHERE user_id = #{userId} AND subscribed = 1 AND deleted = 0")
    List<UserGeofenceState> selectSubscribedByUser(@Param("userId") Long userId);
}
