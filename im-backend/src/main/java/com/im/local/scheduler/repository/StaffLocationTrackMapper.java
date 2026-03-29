package com.im.local.scheduler.repository;

import com.im.local.scheduler.entity.StaffLocationTrack;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.time.LocalDateTime;

/**
 * 骑手轨迹数据访问层
 */
@Mapper
public interface StaffLocationTrackMapper {
    
    @Insert("INSERT INTO staff_location_track (staff_id, lng, lat, accuracy, " +
            "altitude, speed, direction, geohash, geofence_ids, source_type, " +
            "report_time, device_time, created_at) " +
            "VALUES (#{staffId}, #{lng}, #{lat}, #{accuracy}, " +
            "#{altitude}, #{speed}, #{direction}, #{geohash}, #{geofenceIds}, " +
            "#{sourceType}, #{reportTime}, #{deviceTime}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "trackId")
    int insert(StaffLocationTrack track);
    
    @Select("SELECT * FROM staff_location_track WHERE track_id = #{trackId}")
    StaffLocationTrack selectById(Long trackId);
    
    @Select("SELECT * FROM staff_location_track WHERE staff_id = #{staffId} " +
            "AND created_at >= #{startTime} AND created_at <= #{endTime} " +
            "ORDER BY created_at DESC")
    List<StaffLocationTrack> selectByStaffIdAndTimeRange(@Param("staffId") Long staffId,
                                                          @Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);
    
    @Select("SELECT * FROM staff_location_track WHERE staff_id = #{staffId} " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<StaffLocationTrack> selectRecentByStaffId(@Param("staffId") Long staffId,
                                                    @Param("limit") Integer limit);
    
    @Select("SELECT * FROM staff_location_track WHERE geohash LIKE CONCAT(#{geohashPrefix}, '%') " +
            "AND created_at >= #{startTime} ORDER BY created_at DESC LIMIT #{limit}")
    List<StaffLocationTrack> selectByGeohashPrefix(@Param("geohashPrefix") String geohashPrefix,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("limit") Integer limit);
    
    @Select("SELECT DISTINCT staff_id FROM staff_location_track " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 5 MINUTE)")
    List<Long> selectActiveStaffIds();
    
    @Delete("DELETE FROM staff_location_track WHERE created_at < #{beforeTime}")
    int deleteBeforeTime(LocalDateTime beforeTime);
}
