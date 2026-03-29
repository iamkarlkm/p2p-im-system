package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 营销活动实体
 * 本地生活营销活动引擎 - 活动配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingActivity {

    /** 活动ID */
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 活动名称 */
    private String name;

    /** 活动类型: 1-满减活动 2-折扣活动 3-秒杀活动 4-拼团活动 5-砍价活动 */
    private Integer type;

    /** 活动描述 */
    private String description;

    /** 活动封面图 */
    private String coverImage;

    /** 活动分享图 */
    private String shareImage;

    /** 活动开始时间 */
    private LocalDateTime startTime;

    /** 活动结束时间 */
    private LocalDateTime endTime;

    /** 活动规则配置(JSON) */
    private String rules;

    /** 参与门槛: 0-无门槛 1-新用户专享 2-会员专享 3-满额参与 */
    private Integer participationThreshold;

    /** 门槛金额 */
    private BigDecimal thresholdAmount;

    /** 参与次数限制(-1表示不限) */
    private Integer participationLimit;

    /** 库存数量(-1表示不限) */
    private Integer stockQuantity;

    /** 已参与人数/销量 */
    private Integer soldQuantity;

    /** 是否需要分享: 0-否 1-是 */
    private Integer requireShare;

    /** 分享标题 */
    private String shareTitle;

    /** 分享描述 */
    private String shareDesc;

    /** LBS地理围栏: 0-禁用 1-启用 */
    private Integer geofenceEnabled;

    /** 围栏中心经度 */
    private Double fenceLongitude;

    /** 围栏中心纬度 */
    private Double fenceLatitude;

    /** 围栏半径(米) */
    private Integer fenceRadius;

    /** 状态: 0-草稿 1-未开始 2-进行中 3-已结束 4-已取消 */
    private Integer status;

    /** 浏览次数 */
    private Integer viewCount;

    /** 分享次数 */
    private Integer shareCount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 创建人ID */
    private Long createBy;

    /** 是否删除: 0-否 1-是 */
    private Integer deleted;
}
