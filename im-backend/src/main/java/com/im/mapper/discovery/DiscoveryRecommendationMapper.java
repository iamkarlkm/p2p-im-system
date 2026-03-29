package com.im.mapper.discovery;

import com.im.entity.discovery.DiscoveryRecommendation;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 探店推荐Mapper接口
 */
@Mapper
public interface DiscoveryRecommendationMapper {
    
    @Select("SELECT * FROM discovery_recommendation WHERE user_id = #{userId} " +
            "AND deleted = 0 ORDER BY recommend_score DESC LIMIT #{offset}, #{limit}")
    List<DiscoveryRecommendation> selectByUserAndLocation(@Param("userId") Long userId,
                                                           @Param("geoHash") String geoHash,
                                                           @Param("longitude") Double longitude,
                                                           @Param("latitude") Double latitude,
                                                           @Param("offset") Integer offset,
                                                           @Param("limit") Integer limit);
    
    @Select("SELECT poi_id as id, name, longitude, latitude, check_in_radius as checkInRadius " +
            "FROM poi WHERE ST_Distance_Sphere(POINT(longitude, latitude), POINT(#{lng}, #{lat})) <= #{radius}")
    List<Map<String, Object>> selectNearbyPois(@Param("lng") Double longitude, 
                                                @Param("lat") Double latitude, 
                                                @Param("radius") Integer radius);
    
    @Select("SELECT poi_id as poiId, name, type, longitude, latitude, avg_price as avgPrice, " +
            "recommend_reason as recommendReason, distance " +
            "FROM poi_recommendation WHERE ST_Distance_Sphere(POINT(longitude, latitude), " +
            "POINT(#{startLng}, #{startLat})) <= #{maxDistance} " +
            "ORDER BY distance LIMIT #{limit}")
    List<Map<String, Object>> selectRouteCandidates(@Param("startLng") Double startLng,
                                                     @Param("startLat") Double startLat,
                                                     @Param("userTags") Set<String> userTags,
                                                     @Param("limit") Integer limit,
                                                     @Param("maxDistance") Double maxDistance);
    
    @Select("SELECT poi_id, name, check_in_count as heatValue, latitude, longitude " +
            "FROM poi WHERE ST_Distance_Sphere(POINT(longitude, latitude), POINT(#{lng}, #{lat})) <= #{radius} " +
            "ORDER BY check_in_count DESC LIMIT #{limit}")
    List<Map<String, Object>> selectNearbyHotSpots(@Param("lng") Double longitude,
                                                    @Param("lat") Double latitude,
                                                    @Param("radius") Integer radius,
                                                    @Param("limit") Integer limit);
}
