package com.im.backend.modules.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户画像分析响应DTO
 */
@Data
public class CustomerPortraitResponse {

    /** 总顾客数 */
    private Integer totalCustomers;

    /** 新客数 */
    private Integer newCustomers;

    /** 老客数 */
    private Integer oldCustomers;

    /** 新客占比 */
    private BigDecimal newCustomerRatio;

    /** 老客占比 */
    private BigDecimal oldCustomerRatio;

    /** 平均复购周期(天) */
    private Integer avgRepurchaseDays;

    /** 地域分布 */
    private List<RegionDistribution> regionDistribution;

    /** RFM分层分布 */
    private List<RfmDistribution> rfmDistribution;

    /** 消费偏好标签 */
    private List<PreferenceTag> preferenceTags;

    @Data
    public static class RegionDistribution {
        private String regionName;
        private Integer customerCount;
        private BigDecimal ratio;
    }

    @Data
    public static class RfmDistribution {
        private String segment;
        private String segmentName;
        private Integer customerCount;
        private BigDecimal ratio;
    }

    @Data
    public static class PreferenceTag {
        private String tagName;
        private Integer customerCount;
        private BigDecimal ratio;
    }
}
