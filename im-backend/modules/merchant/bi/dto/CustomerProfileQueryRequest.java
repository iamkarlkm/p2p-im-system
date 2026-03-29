package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;

/**
 * 用户画像查询请求DTO
 */
@Data
public class CustomerProfileQueryRequest {

    /** 商户ID */
    private Long merchantId;

    /** 统计时段类型 */
    private String periodType;

    /** 开始日期 */
    private String startDate;

    /** 结束日期 */
    private String endDate;

    /** 地域聚合级别 (province/city/district) */
    private String geoLevel;
}
