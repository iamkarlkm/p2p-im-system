package com.im.backend.modules.local_life.search.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 搜索上下文对象
 * 用于多轮对话的状态管理
 */
@Data
public class SearchContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 位置约束
     */
    private LocationConstraint locationConstraint;

    /**
     * 价格约束
     */
    private PriceConstraint priceConstraint;

    /**
     * 类别约束集合
     */
    private Set<String> categoryConstraints = new HashSet<>();

    /**
     * 评分约束
     */
    private Double minRating;

    /**
     * 排序偏好
     */
    private String sortPreference;

    /**
     * 已排除的关键词
     */
    private Set<String> excludedKeywords = new HashSet<>();

    /**
     * 必需包含的关键词
     */
    private Set<String> requiredKeywords = new HashSet<>();

    /**
     * 时间约束
     */
    private TimeConstraint timeConstraint;

    /**
     * 用户偏好标签
     */
    private Set<String> userPreferenceTags = new HashSet<>();

    /**
     * 扩展属性
     */
    private Map<String, Object> extraProperties = new HashMap<>();

    /**
     * 添加上下文约束
     */
    public void addCategoryConstraint(String category) {
        if (category != null && !category.isEmpty()) {
            this.categoryConstraints.add(category);
        }
    }

    /**
     * 添加排除关键词
     */
    public void addExcludedKeyword(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            this.excludedKeywords.add(keyword);
        }
    }

    /**
     * 添加必需关键词
     */
    public void addRequiredKeyword(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            this.requiredKeywords.add(keyword);
        }
    }

    @Data
    public static class LocationConstraint {
        /**
         * 位置类型: NEARBY-附近, DISTRICT-商圈, CITY-城市, SPECIFIC-具体地址
         */
        private String type;

        /**
         * 中心点经度
         */
        private Double longitude;

        /**
         * 中心点纬度
         */
        private Double latitude;

        /**
         * 搜索半径(米)
         */
        private Integer radius;

        /**
         * 具体地址描述
         */
        private String address;

        /**
         * 商圈ID
         */
        private Long districtId;

        public static LocationConstraint nearby(Double longitude, Double latitude, Integer radius) {
            LocationConstraint constraint = new LocationConstraint();
            constraint.setType("NEARBY");
            constraint.setLongitude(longitude);
            constraint.setLatitude(latitude);
            constraint.setRadius(radius != null ? radius : 3000);
            return constraint;
        }
    }

    @Data
    public static class PriceConstraint {
        /**
         * 最低价格
         */
        private Integer minPrice;

        /**
         * 最高价格
         */
        private Integer maxPrice;

        /**
         * 人均消费级别: LOW-低, MEDIUM-中, HIGH-高, LUXURY-豪华
         */
        private String priceLevel;

        public static PriceConstraint range(Integer min, Integer max) {
            PriceConstraint constraint = new PriceConstraint();
            constraint.setMinPrice(min);
            constraint.setMaxPrice(max);
            return constraint;
        }
    }

    @Data
    public static class TimeConstraint {
        /**
         * 星期几 (1-7)
         */
        private Integer dayOfWeek;

        /**
         * 时间段开始
         */
        private String startTime;

        /**
         * 时间段结束
         */
        private String endTime;

        /**
         * 是否营业中
         */
        private Boolean isOpenNow;
    }
}
