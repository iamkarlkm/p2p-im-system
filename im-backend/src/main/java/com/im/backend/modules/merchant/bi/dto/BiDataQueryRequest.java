package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * BI数据查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiDataQueryRequest {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 对比类型: day/week/month/year
     */
    private String compareType;
    
    /**
     * 数据维度: revenue/order/customer
     */
    private String dimension;
    
    /**
     * 区域级别: province/city/district
     */
    private String regionLevel;
    
    /**
     * 分页参数
     */
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
