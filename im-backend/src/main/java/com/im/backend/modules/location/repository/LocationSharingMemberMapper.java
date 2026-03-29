package com.im.backend.modules.location.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.location.model.entity.LocationSharingMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 位置共享成员数据访问层
 */
@Mapper
public interface LocationSharingMemberMapper extends BaseMapper<LocationSharingMember> {

    /**
     * 根据会话ID查询所有成员
     */
    @Select("SELECT * FROM location_sharing_member WHERE session_id = #{sessionId} ORDER BY joined_at ASC")
    List<LocationSharingMember> selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID和用户ID查询
     */
    @Select("SELECT * FROM location_sharing_member WHERE session_id = #{sessionId} AND user_id = #{userId}")
    LocationSharingMember selectBySessionIdAndUserId(@Param("sessionId") String sessionId, @Param("userId") Long userId);

    /**
     * 更新成员状态
     */
    @Update("UPDATE location_sharing_member SET status = #{status}, updated_at = NOW() " +
            "WHERE session_id = #{sessionId} AND user_id = #{userId}")
    int updateStatus(@Param("sessionId") String sessionId, @Param("userId") Long userId, @Param("status") String status);

    /**
     * 更新成员位置
     */
    @Update("UPDATE location_sharing_member SET last_lat = #{lat}, last_lng = #{lng}, " +
            "last_accuracy = #{accuracy}, last_location_time = #{locationTime}, " +
            "speed = #{speed}, battery_level = #{batteryLevel}, " +
            "distance_to_destination = #{distanceToDestination}, " +
            "estimated_arrival_time = #{eta}, last_active_at = NOW(), updated_at = NOW() " +
            "WHERE session_id = #{sessionId} AND user_id = #{userId}")
    int updateLocation(@Param("sessionId") String sessionId, @Param("userId") Long userId,
                       @Param("lat") Double lat, @Param("lng") Double lng,
                       @Param("accuracy") Double accuracy, @Param("locationTime") LocalDateTime locationTime,
                       @Param("speed") Double speed, @Param("batteryLevel") Integer batteryLevel,
                       @Param("distanceToDestination") Integer distanceToDestination,
                       @Param("eta") LocalDateTime eta);

    /**
     * 标记成员已到达
     */
    @Update("UPDATE location_sharing_member SET has_arrived = true, arrived_at = NOW(), " +
            "updated_at = NOW() WHERE session_id = #{sessionId} AND user_id = #{userId}")
    int markAsArrived(@Param("sessionId") String sessionId, @Param("userId") Long userId);

    /**
     * 查询活跃成员数量
     */
    @Select("SELECT COUNT(*) FROM location_sharing_member WHERE session_id = #{sessionId} AND status = 'ACTIVE'")
    int countActiveMembers(@Param("sessionId") String sessionId);
}
