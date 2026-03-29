package com.im.location.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.location.entity.LocationSharingSession;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 位置共享会话 Mapper
 */
public interface LocationSharingSessionMapper extends BaseMapper<LocationSharingSession> {
    
    /**
     * 根据会话ID查询
     */
    @Select("SELECT * FROM location_sharing_session WHERE session_id = #{sessionId} AND status != 3")
    LocationSharingSession selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 查询用户参与的进行中的会话
     */
    @Select("SELECT s.* FROM location_sharing_session s " +
            "INNER JOIN location_sharing_member m ON s.session_id = m.session_id " +
            "WHERE m.user_id = #{userId} AND s.status = 1 AND m.member_status = 1 " +
            "ORDER BY s.update_time DESC")
    List<LocationSharingSession> selectActiveSessionsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户创建的会话列表
     */
    @Select("SELECT * FROM location_sharing_session WHERE creator_id = #{userId} ORDER BY create_time DESC")
    List<LocationSharingSession> selectByCreatorId(@Param("userId") Long userId);
    
    /**
     * 更新会话状态
     */
    @Update("UPDATE location_sharing_session SET status = #{status}, update_time = NOW() WHERE session_id = #{sessionId}")
    int updateStatus(@Param("sessionId") String sessionId, @Param("status") Integer status);
    
    /**
     * 增加参与人数
     */
    @Update("UPDATE location_sharing_session SET participant_count = participant_count + 1, update_time = NOW() WHERE session_id = #{sessionId}")
    int incrementParticipantCount(@Param("sessionId") String sessionId);
    
    /**
     * 减少参与人数
     */
    @Update("UPDATE location_sharing_session SET participant_count = participant_count - 1, update_time = NOW() WHERE session_id = #{sessionId}")
    int decrementParticipantCount(@Param("sessionId") String sessionId);
}
