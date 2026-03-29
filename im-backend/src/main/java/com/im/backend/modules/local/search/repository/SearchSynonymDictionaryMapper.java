package com.im.backend.modules.local.search.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local.search.entity.SearchSynonymDictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 搜索同义词词典Mapper
 */
@Mapper
public interface SearchSynonymDictionaryMapper extends BaseMapper<SearchSynonymDictionary> {

    /**
     * 根据标准词查询
     */
    @Select("SELECT * FROM search_synonym_dictionary WHERE standard_word = #{word} AND enabled = 1 LIMIT 1")
    SearchSynonymDictionary selectByStandardWord(@Param("word") String word);

    /**
     * 查询所有启用的同义词
     */
    @Select("SELECT * FROM search_synonym_dictionary WHERE enabled = 1")
    List<SearchSynonymDictionary> selectAllEnabled();

    /**
     * 模糊查询标准词
     */
    @Select("SELECT * FROM search_synonym_dictionary WHERE standard_word LIKE CONCAT('%', #{keyword}, '%') " +
            "AND enabled = 1 LIMIT #{limit}")
    List<SearchSynonymDictionary> searchByKeyword(@Param("keyword") String keyword, @Param("limit") Integer limit);
}
