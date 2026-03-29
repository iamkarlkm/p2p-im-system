package com.im.mapper.discovery;

import com.im.entity.discovery.DiscoveryRoute;
import com.im.entity.discovery.DiscoveryRoutePoi;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 探店路线Mapper接口
 */
@Mapper
public interface DiscoveryRouteMapper {
    
    @Select("<script>" +
            "SELECT * FROM discovery_route WHERE status = 'PUBLISHED' AND deleted = 0 " +
            "<if test='sceneTag != null'>AND scene_tag = #{sceneTag}</if> " +
            "<if test='cityCode != null'>AND city_code = #{cityCode}</if> " +
            "ORDER BY is_featured DESC, feature_order, recommend_score DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<DiscoveryRoute> selectBySceneAndCity(@Param("sceneTag") String sceneTag,
                                               @Param("cityCode") String cityCode,
                                               @Param("offset") Integer offset,
                                               @Param("limit") Integer limit);
    
    @Select("SELECT * FROM discovery_route WHERE id = #{routeId} AND deleted = 0")
    DiscoveryRoute selectById(@Param("routeId") Long routeId);
    
    @Select("SELECT * FROM discovery_route_poi WHERE route_id = #{routeId} ORDER BY sequence")
    List<DiscoveryRoutePoi> selectRoutePois(@Param("routeId") Long routeId);
    
    @Insert("INSERT INTO discovery_route (user_id, route_name, description, route_type, " +
            "scene_tag, budget_level, estimated_total_cost, estimated_total_minutes, " +
            "start_longitude, start_latitude, start_address, end_longitude, end_latitude, " +
            "end_address, total_distance, poi_count, cover_image, tags, suitable_time, " +
            "suitable_people, recommend_score, status, create_time, update_time, deleted) VALUES " +
            "(#{userId}, #{routeName}, #{description}, #{routeType}, #{sceneTag}, #{budgetLevel}, " +
            "#{estimatedTotalCost}, #{estimatedTotalMinutes}, #{startLongitude}, #{startLatitude}, " +
            "#{startAddress}, #{endLongitude}, #{endLatitude}, #{endAddress}, #{totalDistance}, " +
            "#{poiCount}, #{coverImage}, #{tags}, #{suitableTime}, #{suitablePeople}, " +
            "#{recommendScore}, #{status}, #{createTime}, #{updateTime}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiscoveryRoute route);
    
    @Insert("<script>" +
            "INSERT INTO discovery_route_poi (route_id, poi_id, sequence, poi_name, poi_type, " +
            "longitude, latitude, address, distance_from_prev, estimated_minutes_from_prev, " +
            "suggested_stay_minutes, estimated_cost, recommend_text, feature_intro, must_try_items, " +
            "image_url, business_hours, rating, avg_price, tags, remark) VALUES " +
            "<foreach collection='poiList' item='poi' separator=','>" +
            "(#{poi.routeId}, #{poi.poiId}, #{poi.sequence}, #{poi.poiName}, #{poi.poiType}, " +
            "#{poi.longitude}, #{poi.latitude}, #{poi.address}, #{poi.distanceFromPrev}, " +
            "#{poi.estimatedMinutesFromPrev}, #{poi.suggestedStayMinutes}, #{poi.estimatedCost}, " +
            "#{poi.recommendText}, #{poi.featureIntro}, #{poi.mustTryItems}, #{poi.imageUrl}, " +
            "#{poi.businessHours}, #{poi.rating}, #{poi.avgPrice}, #{poi.tags}, #{poi.remark})" +
            "</foreach>" +
            "</script>")
    int batchInsertRoutePois(@Param("poiList") List<DiscoveryRoutePoi> poiList);
    
    @Update("UPDATE discovery_route SET usage_count = usage_count + 1 WHERE id = #{id}")
    int incrementUsageCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_route SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    int incrementFavoriteCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_route SET share_count = share_count + 1 WHERE id = #{id}")
    int incrementShareCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_route SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_route SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
}
