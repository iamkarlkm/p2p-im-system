package com.im.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 搜索服务配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "im.search")
public class SearchProperties {

    /** 默认搜索半径（米） */
    private Integer defaultRadius = 5000;

    /** 最大搜索半径（米） */
    private Integer maxRadius = 50000;

    /** 默认每页大小 */
    private Integer defaultPageSize = 20;

    /** 最大每页大小 */
    private Integer maxPageSize = 50;

    /** 搜索超时（毫秒） */
    private Integer searchTimeout = 200;

    /** 是否启用语义搜索 */
    private Boolean enableSemantic = true;

    /** 语义匹配阈值 */
    private Float semanticThreshold = 0.7f;

    /** 搜索建议数量 */
    private Integer suggestionCount = 5;

    /** 热门搜索数量 */
    private Integer hotSearchCount = 10;

    /** 搜索历史保留天数 */
    private Integer searchHistoryDays = 30;

    /** 索引名称 */
    private String poiIndexName = "poi_search_v1";
}
