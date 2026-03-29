package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;

/**
 * 经营数据查询请求DTO
 */
@Data
public class BusinessStatsQueryRequest {

    /** 商户ID */
    private Long merchantId;

    /** 统计时段类型 */
    private String periodType;

    /** 开始日期 */
    private String startDate;

    /** 结束日期 */
    private String endDate;

    /** 是否包含时段分布 */
    private Boolean includeHourly;

    /** 是否包含趋势 */
    private Boolean includeTrend;

    /** 是否包含对比 */
    private Boolean includeComparison;
}
