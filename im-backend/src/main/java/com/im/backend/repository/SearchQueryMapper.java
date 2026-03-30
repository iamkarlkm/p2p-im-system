package com.im.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.entity.LocalLifeSearchQuery;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 本地生活搜索查询Mapper
 */
@Mapper
public interface SearchQueryMapper extends BaseMapper<LocalLifeSearchQuery> {

    /**
     * 根据用户ID查询搜索历史
     */
    @Select("SELECT * FROM local_life_search_query WHERE user_id = #{userId} " +
            "AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<LocalLifeSearchQuery> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询热门搜索关键词
     */
    @Select("SELECT keyword, COUNT(*) as count FROM local_life_search_query " +
            "WHERE create_time >= #{startTime} AND keyword IS NOT NULL AND keyword != '' " +
            "AND deleted = 0 GROUP BY keyword ORDER BY count DESC LIMIT #{limit}")
    List<SearchHotWord> selectHotKeywords(@Param("startTime") LocalDateTime startTime, @Param("limit") Integer limit);

    /**
     * 根据GeoHash查询区域内搜索
     */
    @Select("SELECT * FROM local_life_search_query WHERE geo_hash LIKE CONCAT(#{geoHashPrefix}, '%') " +
            "AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<LocalLifeSearchQuery> selectByGeoHashPrefix(@Param("geoHashPrefix") String geoHashPrefix, @Param("limit") Integer limit);

    /**
     * 查询用户最近搜索(去重)
     */
    @Select("SELECT DISTINCT keyword, MAX(create_time) as last_time FROM local_life_search_query " +
            "WHERE user_id = #{userId} AND keyword IS NOT NULL AND keyword != '' " +
            "AND deleted = 0 GROUP BY keyword ORDER BY last_time DESC LIMIT #{limit}")
    List<UserSearchHistory> selectUserRecentSearches(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 统计搜索转化率
     */
    @Select("SELECT COUNT(*) as total_count, SUM(CASE WHEN has_click = 1 THEN 1 ELSE 0 END) as click_count " +
            "FROM local_life_search_query WHERE create_time >= #{startTime} AND deleted = 0")
    SearchConversionStats selectConversionStats(@Param("startTime") LocalDateTime startTime);

    /**
     * 清理过期搜索记录
     */
    @Delete("DELETE FROM local_life_search_query WHERE create_time < #{expireTime} AND deleted = 0")
    int deleteExpiredRecords(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 热门搜索词结果
     */
    @Data
    class SearchHotWord {
        private String keyword;
        private Integer count;
    }

    /**
     * 用户搜索历史
     */
    @Data
    class UserSearchHistory {
        private String keyword;
        private LocalDateTime lastTime;
    }

    /**
     * 搜索转化统计
     */
    @Data
    class SearchConversionStats {
        private Integer totalCount;
        private Integer clickCount;

        public Double getConversionRate() {
            if (totalCount == null || totalCount == 0) return 0.0;
            return (double) clickCount / totalCount * 100;
        }
    }
}
