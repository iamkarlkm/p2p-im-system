package com.im.presence.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.presence.entity.UserPresence;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户在线状态Mapper
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Mapper
public interface UserPresenceMapper extends BaseMapper<UserPresence> {
    
    /**
     * 查询用户在线状态
     */
    @Select("SELECT * FROM im_user_presence WHERE user_id = #{userId} LIMIT 1")
    UserPresence selectByUserId(@Param("userId") Long userId);
    
    /**
     * 批量查询用户状态
     */
    @Select("<script>" +
            "SELECT * FROM im_user_presence WHERE user_id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'> #{id} </foreach>" +
            "</script>")
    List<UserPresence> selectBatchByUserIds(@Param("userIds") List<Long> userIds);
    
    /**
     * 查询在线用户列表
     */
    @Select("SELECT * FROM im_user_presence WHERE status > 0 LIMIT #{limit}")
    List<UserPresence> selectOnlineUsers(@Param("limit") Integer limit);
    
    /**
     * 更新用户状态
     */
    @Update("UPDATE im_user_presence SET " +
            "status = #{status}, " +
            "last_active_time = NOW(), " +
            "last_active_timestamp = #{timestamp}, " +
            "update_time = NOW() " +
            "WHERE user_id = #{userId}")
    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status, @Param("timestamp") Long timestamp);
    
    /**
     * 更新登录信息
     */
    @Update("UPDATE im_user_presence SET " +
            "status = 1, " +
            "last_login_time = NOW(), " +
            "last_active_time = NOW(), " +
            "last_active_timestamp = #{timestamp}, " +
            "last_login_ip = #{ip}, " +
            "last_device_type = #{deviceType}, " +
            "last_device_id = #{deviceId}, " +
            "server_node = #{serverNode}, " +
            "session_count = session_count + 1, " +
            "update_time = NOW() " +
            "WHERE user_id = #{userId}")
    int updateLoginInfo(@Param("userId") Long userId, 
                        @Param("ip") String ip,
                        @Param("deviceType") Integer deviceType,
                        @Param("deviceId") String deviceId,
                        @Param("serverNode") String serverNode,
                        @Param("timestamp") Long timestamp);
    
    /**
     * 更新登出信息
     */
    @Update("UPDATE im_user_presence SET " +
            "status = CASE WHEN session_count <= 1 THEN 0 ELSE status END, " +
            "last_logout_time = NOW(), " +
            "session_count = GREATEST(session_count - 1, 0), " +
            "update_time = NOW() " +
            "WHERE user_id = #{userId}")
    int updateLogoutInfo(@Param("userId") Long userId);
    
    /**
     * 查询超时离线用户
     */
    @Select("SELECT * FROM im_user_presence " +
            "WHERE status > 0 AND last_active_time &lt; #{timeoutTime}")
    List<UserPresence> selectTimeoutUsers(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 统计在线用户数量
     */
    @Select("SELECT COUNT(*) FROM im_user_presence WHERE status > 0")
    int countOnlineUsers();
    
    /**
     * 统计各状态用户数量
     */
    @Select("SELECT status, COUNT(*) as count FROM im_user_presence GROUP BY status")
    List<java.util.Map<String, Object>> countByStatus();
}
