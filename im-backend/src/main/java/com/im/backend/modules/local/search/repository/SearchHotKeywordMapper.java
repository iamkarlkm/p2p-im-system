package com.im.backend.modules.local.search.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local.search.entity.SearchHotKeyword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 搜索热词Mapper
 */
@Mapper
public interface SearchHotKeywordMapper extends BaseMapper<SearchHotKeyword> {

    /**
     * 查询当前热词列表
     */
    @Select("SELECT * FROM search_hot_keyword WHERE stat_date = #{statDate} " +
            "AND period_type = #{periodType} ORDER BY hot_rank ASC")
    List<SearchHotKeyword> selectByDateAndPeriod(@Param("statDate") String statDate,
                                                  @Param("periodType") String periodType);

    /**
     * 按分类查询热词
     */
    @Select("SELECT * FROM search_hot_keyword WHERE category = #{category} " +
            "AND stat_date = #{statDate} ORDER BY hot_rank ASC LIMIT #{limit}")
    List<SearchHotKeyword> selectByCategory(@Param("category") String category,
                                            @Param("statDate") String statDate,
                                            @Param("limit") Integer limit);

    /**
     * 查询上升趋势的热词
     */
    @Select("SELECT * FROM search_hot_keyword WHERE trend IN ('UP', 'EXPLOSIVE') " +
            "AND stat_date = #{statDate} ORDER BY search_count DESC LIMIT #{limit}")
    List<SearchHotKeyword> selectRisingKeywords(@Param("statDate") String statDate,
                                                 @Param("limit") Integer limit);
}
