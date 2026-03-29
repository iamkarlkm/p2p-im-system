package com.im.backend.modules.location.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.location.model.entity.LocationSharingSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 位置共享会话数据访问层
 */
@Mapper
public interface LocationSharingSessionMapper extends BaseMapper<LocationSharingSession> {

    /**
     * 根据会话ID查询
     */
    @Select("SELECT * FROM location_sharing_session WHERE session_id = #{sessionId} AND status != 'ENDED'")
    LocationSharingSession selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 查询用户的活跃会话
     */
    @Select("SELECT s.* FROM location_sharing_session s " +
            "INNER JOIN location_sharing_member m ON s.session_id = m.session_id " +
            "WHERE m.user_id = #{userId} AND s.status = 'ACTIVE' " +
            "ORDER BY s.created_at DESC")
    List<LocationSharingSession> selectActiveSessionsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户创建的会话
     */
    @Select("SELECT * FROM location_sharing_session WHERE creator_id = #{creatorId} ORDER BY created_at DESC")
    List<LocationSharingSession> selectByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 更新会话状态
     */
    @Update("UPDATE location_sharing_session SET status = #{status}, actual_end_time = #{endTime}, updated_at = NOW() " +
            "WHERE session_id = #{sessionId}")
    int updateStatus(@Param("sessionId") String sessionId, @Param("status") String status, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询过期的会话
     */
    @Select("SELECT * FROM location_sharing_session WHERE status = 'ACTIVE' AND expected_end_time < NOW()")
    List<LocationSharingSession> selectExpiredSessions();
}
