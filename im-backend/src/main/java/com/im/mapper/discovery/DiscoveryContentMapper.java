package com.im.mapper.discovery;

import com.im.entity.discovery.DiscoveryContent;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 探店内容Mapper接口
 */
@Mapper
public interface DiscoveryContentMapper {
    
    @Select("<script>" +
            "SELECT * FROM discovery_content WHERE deleted = 0 AND status = 'PUBLISHED' " +
            "<if test='poiId != null'>AND poi_id = #{poiId}</if> " +
            "<if test='contentType != null'>AND content_type = #{contentType}</if> " +
            "ORDER BY is_recommended DESC, recommend_weight DESC, publish_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<DiscoveryContent> selectByPoiAndType(@Param("poiId") Long poiId,
                                               @Param("contentType") String contentType,
                                               @Param("offset") Integer offset,
                                               @Param("limit") Integer limit);
    
    @Select("SELECT * FROM discovery_content WHERE deleted = 0 AND status = 'PUBLISHED' " +
            "AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) " +
            "<if test='cityCode != null'>AND city_code = #{cityCode}</if> " +
            "ORDER BY publish_time DESC LIMIT #{offset}, #{limit}")
    List<DiscoveryContent> searchByKeyword(@Param("keyword") String keyword,
                                            @Param("cityCode") String cityCode,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);
    
    @Select("<script>" +
            "SELECT * FROM discovery_content WHERE deleted = 0 AND status = 'PUBLISHED' " +
            "AND user_id IN " +
            "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>" +
            "#{userId}" +
            "</foreach> " +
            "ORDER BY publish_time DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<DiscoveryContent> selectByUserIds(@Param("userIds") List<Long> userIds,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);
    
    @Update("UPDATE discovery_content SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_content SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_content SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_content SET share_count = share_count + 1 WHERE id = #{id}")
    int incrementShareCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_content SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    int incrementFavoriteCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_content SET repost_count = repost_count + 1 WHERE id = #{id}")
    int incrementRepostCount(@Param("id") Long id);
}
