package com.im.backend.modules.miniprogram.dto;

import lombok.Data;

/**
 * 项目统计
 */
@Data
public class ProjectStatistics {

    /**
     * 总访问量
     */
    private Long totalVisits;

    /**
     * 今日访问量
     */
    private Long todayVisits;

    /**
     * 页面数量
     */
    private Integer pageCount;

    /**
     * 组件数量
     */
    private Integer componentCount;

    /**
     * 最后发布时间
     */
    private String lastPublishTime;
}
