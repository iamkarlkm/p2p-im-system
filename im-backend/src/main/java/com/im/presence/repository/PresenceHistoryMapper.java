package com.im.presence.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.presence.entity.PresenceHistory;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 状态变更历史Mapper
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Mapper
public interface PresenceHistoryMapper extends BaseMapper<PresenceHistory> {
    
    /**
     * 查询用户状态历史
     */
    @Select("SELECT * FROM im_presence_history " +
            "WHERE user_id = #{userId} " +
            "ORDER BY change_time DESC LIMIT #{limit}")
    List<PresenceHistory> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 查询时间范围内的状态变更
     */
    @Select("SELECT * FROM im_presence_history " +
            "WHERE user_id = #{userId} " +
            "AND change_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY change_time DESC")
    List<PresenceHistory> selectByTimeRange(@Param("userId") Long userId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 批量插入历史记录
     */
    @Insert("<script>" +
            "INSERT INTO im_presence_history (user_id, old_status, new_status, change_reason, " +
            "change_time, device_type, device_id, ip_address, location, session_id, create_time) VALUES " +
            "<foreach collection='list' item='item' separator=','> " +
            "(#{item.userId}, #{item.oldStatus}, #{item.newStatus}, #{item.changeReason}, " +
            "#{item.changeTime}, #{item.deviceType}, #{item.deviceId}, #{item.ipAddress}, " +
            "#{item.location}, #{item.sessionId}, NOW()) " +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<PresenceHistory> historyList);
}
