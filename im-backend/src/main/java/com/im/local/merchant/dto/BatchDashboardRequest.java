package com.im.local.merchant.dto;

import lombok.Data;
import java.util.List;

/**
 * 批量仪表盘请求DTO
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
public class BatchDashboardRequest {
    
    /**
     * 商户ID列表
     */
    private List<Long> merchantIds;
    
    /**
     * 基础请求参数
     */
    private MerchantDashboardRequest baseRequest;
}
