package com.im.local.merchant.dto;

import lombok.Data;

/**
 * 报表导出请求DTO
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
public class ReportExportRequest {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 报表类型
     * DASHBOARD-仪表盘, REVENUE-营收, TRAFFIC-客流, REVIEW-评价
     */
    private String reportType;
    
    /**
     * 导出格式
     * PDF, EXCEL, CSV
     */
    private String format;
    
    /**
     * 时间范围类型
     */
    private String timeRangeType;
    
    /**
     * 是否包含图表
     */
    private Boolean includeCharts;
    
    /**
     * 接收邮箱
     */
    private String email;
}
