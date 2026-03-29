package com.im.backend.modules.local.search.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local.search.entity.SearchQueryLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索查询日志Mapper
 */
@Mapper
public interface SearchQueryLogMapper extends BaseMapper<SearchQueryLog> {

    /**
     * 查询用户最近的搜索历史
     */
    @Select("SELECT * FROM search_query_log WHERE user_id = #{userId} " +
            "ORDER BY search_time DESC LIMIT #{limit}")
    List<SearchQueryLog> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 统计时间段内的热门搜索词
     */
    @Select("SELECT normalized_query, COUNT(*) as count FROM search_query_log " +
            "WHERE search_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY normalized_query ORDER BY count DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> selectHotKeywords(@Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime,
                                                           @Param("limit") Integer limit);

    /**
     * 查询搜索转化率
     */
    @Select("SELECT AVG(CASE WHEN click_count > 0 THEN 1 ELSE 0 END) as conversion_rate " +
            "FROM search_query_log WHERE search_time BETWEEN #{startTime} AND #{endTime}")
    Double selectConversionRate(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);
}
