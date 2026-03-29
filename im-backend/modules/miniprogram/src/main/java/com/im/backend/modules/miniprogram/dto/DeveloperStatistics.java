package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 开发者统计
 */
@Data
public class DeveloperStatistics {

    /**
     * 总下载量
     */
    private Long totalDownloads;

    /**
     * 本月下载量
     */
    private Long monthDownloads;

    /**
     * 平均评分
     */
    private BigDecimal avgRating;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 总收益
     */
    private BigDecimal totalIncome;

    /**
     * 本月收益
     */
    private BigDecimal monthIncome;
}
