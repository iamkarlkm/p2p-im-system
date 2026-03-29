package com.im.backend.modules.location.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.location.model.entity.SharedLocationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 共享位置记录数据访问层
 */
@Mapper
public interface SharedLocationRecordMapper extends BaseMapper<SharedLocationRecord> {

    /**
     * 查询会话的位置记录
     */
    @Select("SELECT * FROM shared_location_record WHERE session_id = #{sessionId} " +
            "AND created_at >= #{startTime} ORDER BY created_at DESC LIMIT #{limit}")
    List<SharedLocationRecord> selectBySessionId(@Param("sessionId") String sessionId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("limit") Integer limit);

    /**
     * 查询用户的最新位置
     */
    @Select("SELECT * FROM shared_location_record WHERE session_id = #{sessionId} AND user_id = #{userId} " +
            "ORDER BY created_at DESC LIMIT 1")
    SharedLocationRecord selectLatestByUserId(@Param("sessionId") String sessionId, @Param("userId") Long userId);

    /**
     * 查询会话中所有成员的最新位置
     */
    @Select("SELECT r.* FROM shared_location_record r " +
            "INNER JOIN (SELECT user_id, MAX(created_at) as max_time FROM shared_location_record " +
            "WHERE session_id = #{sessionId} GROUP BY user_id) latest " +
            "ON r.user_id = latest.user_id AND r.created_at = latest.max_time " +
            "WHERE r.session_id = #{sessionId}")
    List<SharedLocationRecord> selectLatestLocations(@Param("sessionId") String sessionId);
}
