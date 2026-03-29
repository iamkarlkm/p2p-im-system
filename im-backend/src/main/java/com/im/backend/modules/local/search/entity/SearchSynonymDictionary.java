package com.im.backend.modules.local.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.im.backend.common.BaseEntity;

/**
 * 搜索同义词词典实体
 * 用于搜索纠错和扩展
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("search_synonym_dictionary")
public class SearchSynonymDictionary extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标准词
     */
    private String standardWord;

    /**
     * 同义词列表，逗号分隔
     */
    private String synonyms;

    /**
     * 分类标签
     */
    private String category;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
