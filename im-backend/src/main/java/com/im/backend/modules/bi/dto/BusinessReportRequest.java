package com.im.backend.modules.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 经营报表查询请求DTO
 */
@Data
public class BusinessReportRequest {

    /** 商户ID */
    private Long merchantId;

    /** 开始日期 */
    private LocalDate startDate;

    /** 结束日期 */
    private LocalDate endDate;

    /** 对比类型: day, week, month */
    private String compareType;

    /** 数据粒度: day, week, month */
    private String granularity;
}
