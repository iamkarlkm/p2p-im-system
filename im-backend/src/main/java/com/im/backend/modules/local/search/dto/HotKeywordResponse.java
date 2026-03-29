package com.im.backend.modules.local.search.dto;

import lombok.Data;

import java.util.List;

/**
 * 搜索热词列表响应DTO
 */
@Data
public class HotKeywordResponse {

    /**
     * 热词列表
     */
    private List<HotKeywordItem> keywords;

    /**
     * 更新时间
     */
    private String updateTime;

    @Data
    public static class HotKeywordItem {
        /**
         * 热词
         */
        private String keyword;

        /**
         * 排名
         */
        private Integer rank;

        /**
         * 搜索次数
         */
        private Long searchCount;

        /**
         * 趋势：UP/DOWN/STABLE/NEW
         */
        private String trend;

        /**
         * 趋势图标URL
         */
        private String trendIcon;
    }
}
