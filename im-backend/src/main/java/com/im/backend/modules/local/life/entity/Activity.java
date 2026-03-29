package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 本地生活活动实体
 * 存储用户发布的各类活动信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_local_life_activity")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动唯一标识 */
    private String activityCode;

    /** 活动标题 */
    private String title;

    /** 活动描述 */
    private String description;

    /** 活动封面图 */
    private String coverImage;

    /** 活动分类: GATHERING-聚餐, SPORT-运动, TRAVEL-旅行, PARENT_CHILD-亲子, PET-宠物 */
    private String category;

    /** 活动状态: DRAFT-草稿, PUBLISHED-已发布, ONGOING-进行中, ENDED-已结束, CANCELLED-已取消 */
    private String status;

    /** 发布者用户ID */
    private Long publisherId;

    /** 发布者昵称 */
    private String publisherNickname;

    /** 发布者头像 */
    private String publisherAvatar;

    /** 绑定POI ID */
    private String poiId;

    /** POI名称 */
    private String poiName;

    /** 详细地址 */
    private String address;

    /** 经度 */
    private BigDecimal longitude;

    /** 纬度 */
    private BigDecimal latitude;

    /** GeoHash编码 (8位精度) */
    private String geoHash;

    /** 活动开始时间 */
    private LocalDateTime startTime;

    /** 活动结束时间 */
    private LocalDateTime endTime;

    /** 报名截止时间 */
    private LocalDateTime registrationDeadline;

    /** 最大参与人数 */
    private Integer maxParticipants;

    /** 当前报名人数 */
    private Integer currentParticipants;

    /** 支付方式: FREE-免费, AA-AA制, PAID-付费 */
    private String paymentType;

    /** 人均费用 */
    private BigDecimal perCapitaFee;

    /** 总费用 */
    private BigDecimal totalFee;

    /** 是否创建IM群组 */
    private Boolean createImGroup;

    /** IM群组ID */
    private String imGroupId;

    /** 浏览次数 */
    private Integer viewCount;

    /** 分享次数 */
    private Integer shareCount;

    /** 点赞次数 */
    private Integer likeCount;

    /** 评论次数 */
    private Integer commentCount;

    /** 活动热度分数 */
    private Double heatScore;

    /** 是否推荐 */
    private Boolean recommended;

    /** 推荐权重 */
    private Integer recommendWeight;

    /** 是否需要审核报名 */
    private Boolean requireApproval;

    /** 活动标签,逗号分隔 */
    private String tags;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 是否删除 */
    @TableLogic
    private Boolean deleted;
}
