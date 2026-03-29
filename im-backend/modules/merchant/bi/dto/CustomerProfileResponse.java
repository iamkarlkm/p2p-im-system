package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户画像分析响应DTO
 */
@Data
public class CustomerProfileResponse {

    /** 商户ID */
    private Long merchantId;

    /** 统计时段 */
    private String period;

    /** 总顾客数 */
    private Integer totalCustomers;

    /** 新客占比 */
    private BigDecimal newCustomerRatio;

    /** 老客占比 */
    private BigDecimal returningCustomerRatio;

    /** 年龄分布 */
    private List<AgeDistribution> ageDistribution;

    /** 性别分布 */
    private List<GenderDistribution> genderDistribution;

    /** 地域分布热力图数据 */
    private List<GeoHeatmapData> geoHeatmapData;

    /** 城市TOP10 */
    private List<CityDistribution> cityTop10;

    /** 消费频次分布 */
    private List<FrequencyDistribution> frequencyDistribution;

    /** 消费偏好标签 */
    private List<PreferenceTag> preferenceTags;

    /**
     * 年龄分布
     */
    @Data
    public static class AgeDistribution {
        private String ageGroup;
        private Integer count;
        private Double percentage;
    }

    /**
     * 性别分布
     */
    @Data
    public static class GenderDistribution {
        private String gender;
        private Integer count;
        private Double percentage;
    }

    /**
     * 地理热力图数据
     */
    @Data
    public static class GeoHeatmapData {
        private Double longitude;
        private Double latitude;
        private Integer intensity;
        private String district;
    }

    /**
     * 城市分布
     */
    @Data
    public static class CityDistribution {
        private String city;
        private Integer count;
        private Double percentage;
    }

    /**
     * 频次分布
     */
    @Data
    public static class FrequencyDistribution {
        private String frequency;
        private Integer count;
        private Double percentage;
    }

    /**
     * 偏好标签
     */
    @Data
    public static class PreferenceTag {
        private String tag;
        private Integer count;
        private Double percentage;
    }
}
