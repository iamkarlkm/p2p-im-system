package com.im.backend.modules.local_life.search.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索热词/趋势DTO
 */
@Data
public class SearchTrendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索词
     */
    private String keyword;

    /**
     * 搜索次数
     */
    private Long searchCount;

    /**
     * 热度排名
     */
    private Integer rank;

    /**
     * 趋势: UP-上升, DOWN-下降, FLAT-持平, NEW-新上榜
     */
    private String trend;

    /**
     * 环比变化率
     */
    private Double changeRate;

    /**
     * 关联类别
     */
    private String category;

    /**
     * 统计时间
     */
    private LocalDateTime statTime;

    /**
     * 热门搜索结果预览
     */
    private List<HotSearchResultDTO> hotResults;

    /**
     * 热门搜索结果预览DTO
     */
    @Data
    public static class HotSearchResultDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long poiId;
        private String poiName;
        private String image;
        private Double rating;
    }
}
