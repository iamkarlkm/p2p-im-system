package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 营销活动响应DTO
 */
@Data
public class MarketingActivityResponse {

    /** 活动ID */
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 商户名称 */
    private String merchantName;

    /** 活动名称 */
    private String name;

    /** 活动类型 */
    private Integer type;

    /** 活动类型名称 */
    private String typeName;

    /** 描述 */
    private String description;

    /** 封面图 */
    private String coverImage;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 状态 */
    private Integer status;

    /** 状态名称 */
    private String statusName;

    /** 销量/参与人数 */
    private Integer soldQuantity;

    /** 浏览次数 */
    private Integer viewCount;

    /** 创建时间 */
    private LocalDateTime createTime;
}
