package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 附近优惠券查询请求DTO
 */
@Data
public class NearbyCouponRequest {

    /** 用户经度 */
    private Double longitude;

    /** 用户纬度 */
    private Double latitude;

    /** 搜索半径(米,默认5000) */
    private Integer radius = 5000;

    /** 优惠券类型筛选 */
    private Integer couponType;

    /** 排序方式: 1-距离最近 2-优惠力度 3-最新发布 */
    private Integer sortBy = 1;

    /** 页码 */
    private Integer pageNum = 1;

    /** 每页数量 */
    private Integer pageSize = 20;
}
