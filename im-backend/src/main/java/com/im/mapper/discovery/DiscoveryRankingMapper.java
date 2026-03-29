package com.im.mapper.discovery;

import com.im.entity.discovery.DiscoveryRanking;
import com.im.entity.discovery.DiscoveryRankingItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 探店榜单Mapper接口
 */
@Mapper
public interface DiscoveryRankingMapper {
    
    @Select("<script>" +
            "SELECT * FROM discovery_ranking WHERE city_code = #{cityCode} " +
            "AND status = 'ACTIVE' AND deleted = 0 " +
            "<if test='rankingType != null'>AND ranking_type = #{rankingType}</if> " +
            "ORDER BY feature_order, sort_order LIMIT #{offset}, #{limit}" +
            "</script>")
    List<DiscoveryRanking> selectByCityAndType(@Param("cityCode") String cityCode,
                                                @Param("rankingType") String rankingType,
                                                @Param("offset") Integer offset,
                                                @Param("limit") Integer limit);
    
    @Select("SELECT * FROM discovery_ranking WHERE id = #{rankingId} AND deleted = 0")
    DiscoveryRanking selectById(@Param("rankingId") Long rankingId);
    
    @Select("SELECT * FROM discovery_ranking_item WHERE ranking_id = #{rankingId} " +
            "ORDER BY rank LIMIT 100")
    List<DiscoveryRankingItem> selectRankingItems(@Param("rankingId") Long rankingId);
    
    @Update("UPDATE discovery_ranking SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_ranking SET share_count = share_count + 1 WHERE id = #{id}")
    int incrementShareCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_ranking SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    int incrementFavoriteCount(@Param("id") Long id);
}
