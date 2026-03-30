package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;

/**
 * 本地生活搜索请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {

    /**
     * 搜索关键词
     */
    @Size(max = 100, message = "关键词长度不能超过100字符")
    private String keyword;

    /**
     * 搜索类型: KEYWORD-关键词, POI-地点, MERCHANT-商户, COUPON-优惠券, ACTIVITY-活动
     */
    @Pattern(regexp = "KEYWORD|POI|MERCHANT|COUPON|ACTIVITY", message = "无效的搜索类型")
    private String searchType;

    /**
     * 经度(-180 ~ 180)
     */
    @DecimalMin(value = "-180.0", message = "经度不能小于-180")
    @DecimalMax(value = "180.0", message = "经度不能大于180")
    private Double longitude;

    /**
     * 纬度(-90 ~ 90)
     */
    @DecimalMin(value = "-90.0", message = "纬度不能小于-90")
    @DecimalMax(value = "90.0", message = "纬度不能大于90")
    private Double latitude;

    /**
     * 搜索半径(米), 默认3000
     */
    @Min(value = 100, message = "搜索半径最小100米")
    @Max(value = 50000, message = "搜索半径最大50000米")
    @Builder.Default
    private Integer radius = 3000;

    /**
     * 城市编码
     */
    @Size(max = 20, message = "城市编码长度不能超过20")
    private String cityCode;

    /**
     * POI分类过滤: FOOD-美食, SHOPPING-购物, ENTERTAINMENT-娱乐, LIFE-生活服务
     */
    private List<String> poiTypes;

    /**
     * 价格区间过滤[min, max]
     */
    private List<Integer> priceRange;

    /**
     * 最低评分过滤(0-5)
     */
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private Double minRating;

    /**
     * 排序方式: DISTANCE-距离最近, RATING-评分最高, POPULAR-人气最高, SMART-智能排序
     */
    @Pattern(regexp = "DISTANCE|RATING|POPULAR|SMART", message = "无效的排序方式")
    @Builder.Default
    private String sortBy = "SMART";

    /**
     * 页码(从1开始)
     */
    @Min(value = 1, message = "页码从1开始")
    @Builder.Default
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    @Min(value = 1)
    @Max(value = 50)
    @Builder.Default
    private Integer pageSize = 20;

    /**
     * 是否只查营业中的商户
     */
    @Builder.Default
    private Boolean openNow = false;

    /**
     * 搜索来源
     */
    private String source;

    /**
     * 会话ID(用于追踪)
     */
    private String sessionId;

    /**
     * 获取搜索半径(带默认值)
     */
    public Integer getRadius() {
        return radius == null ? 3000 : radius;
    }

    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (getPageNum() - 1) * getPageSize();
    }

    /**
     * 获取页码(带默认值)
     */
    public Integer getPageNum() {
        return pageNum == null ? 1 : pageNum;
    }

    /**
     * 获取每页数量(带默认值)
     */
    public Integer getPageSize() {
        return pageSize == null ? 20 : pageSize;
    }
}
