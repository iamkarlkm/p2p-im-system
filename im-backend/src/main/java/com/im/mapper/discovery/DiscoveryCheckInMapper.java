package com.im.mapper.discovery;

import com.im.entity.discovery.DiscoveryCheckIn;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * 探店打卡Mapper接口
 */
@Mapper
public interface DiscoveryCheckInMapper {
    
    @Insert("INSERT INTO discovery_check_in (user_id, poi_id, merchant_id, check_in_type, " +
            "longitude, latitude, accuracy, address, location_name, enter_geofence, " +
            "exit_geofence, geofence_trigger_time, dwell_minutes, content, images, video_url, " +
            "rating, spend_amount, people_count, avg_spend, tags, recommended, recommend_text, " +
            "is_public, visibility, is_expert_check_in, expert_level, check_in_time, " +
            "create_time, update_time, deleted) VALUES " +
            "(#{userId}, #{poiId}, #{merchantId}, #{checkInType}, #{longitude}, #{latitude}, " +
            "#{accuracy}, #{address}, #{locationName}, #{enterGeofence}, #{exitGeofence}, " +
            "#{geofenceTriggerTime}, #{dwellMinutes}, #{content}, #{images}, #{videoUrl}, " +
            "#{rating}, #{spendAmount}, #{peopleCount}, #{avgSpend}, #{tags}, #{recommended}, " +
            "#{recommendText}, #{isPublic}, #{visibility}, #{isExpertCheckIn}, #{expertLevel}, " +
            "#{checkInTime}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiscoveryCheckIn checkIn);
    
    @Select("SELECT * FROM discovery_check_in WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY check_in_time DESC LIMIT #{offset}, #{limit}")
    List<DiscoveryCheckIn> selectByUserId(@Param("userId") Long userId,
                                          @Param("offset") Integer offset,
                                          @Param("limit") Integer limit);
    
    @Select("SELECT * FROM discovery_check_in WHERE poi_id = #{poiId} AND deleted = 0 " +
            "AND is_public = 1 AND audit_status = 'APPROVED' " +
            "ORDER BY check_in_time DESC LIMIT #{offset}, #{limit}")
    List<DiscoveryCheckIn> selectByPoiId(@Param("poiId") Long poiId,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    
    @Select("SELECT COUNT(*) FROM discovery_check_in WHERE user_id = #{userId} AND deleted = 0")
    Integer countByUserId(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(DISTINCT poi_id) FROM discovery_check_in " +
            "WHERE user_id = #{userId} AND deleted = 0")
    Integer countDistinctPoisByUserId(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(DISTINCT city_code) FROM discovery_check_in d " +
            "JOIN poi p ON d.poi_id = p.id WHERE d.user_id = #{userId} AND d.deleted = 0")
    Integer countDistinctCitiesByUserId(@Param("userId") Long userId);
    
    @Select("SELECT d.poi_id, p.name, p.latitude, p.longitude, COUNT(*) as check_in_count " +
            "FROM discovery_check_in d JOIN poi p ON d.poi_id = p.id " +
            "WHERE d.user_id = #{userId} AND d.deleted = 0 " +
            "<if test='cityCode != null'>AND p.city_code = #{cityCode}</if> " +
            "GROUP BY d.poi_id ORDER BY check_in_count DESC")
    List<Map<String, Object>> selectUserFootprint(@Param("userId") Long userId,
                                                   @Param("cityCode") String cityCode);
    
    @Select("SELECT user_id, COUNT(*) as check_in_count, COUNT(DISTINCT poi_id) as poi_count, " +
            "SUM(like_count) as total_likes FROM discovery_check_in " +
            "WHERE deleted = 0 AND is_expert_check_in = 1 " +
            "<if test='cityCode != null'>AND city_code = #{cityCode}</if> " +
            "GROUP BY user_id ORDER BY check_in_count DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectExpertUsers(@Param("cityCode") String cityCode,
                                                 @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);
    
    @Update("UPDATE discovery_check_in SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_check_in SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_check_in SET share_count = share_count + 1 WHERE id = #{id}")
    int incrementShareCount(@Param("id") Long id);
}
