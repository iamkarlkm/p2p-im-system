package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;

/**
 * 营销效果查询请求DTO
 */
@Data
public class MarketingEffectQueryRequest {

    /** 商户ID */
    private Long merchantId;

    /** 营销类型 */
    private String marketingType;

    /** 统计时段类型 */
    private String periodType;

    /** 开始日期 */
    private String startDate;

    /** 结束日期 */
    private String endDate;

    /** 是否计算ROI */
    private Boolean calculateRoi;
}
