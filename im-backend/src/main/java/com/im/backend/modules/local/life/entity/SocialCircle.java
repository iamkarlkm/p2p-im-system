package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 社交圈子实体
 * 基于兴趣或地理位置的社交圈
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_social_circle")
public class SocialCircle {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 圈子唯一标识 */
    private String circleCode;

    /** 圈子名称 */
    private String name;

    /** 圈子简介 */
    private String description;

    /** 圈子头像 */
    private String avatar;

    /** 圈子封面 */
    private String coverImage;

    /** 圈子分类: FOOD-美食, SPORT-运动, TRAVEL-旅行, PARENT-亲子, PET-宠物, ENTERTAINMENT-娱乐, OTHER-其他 */
    private String category;

    /** 圈子类型: PUBLIC-公开, PRIVATE-私密, INVITE_ONLY-邀请制 */
    private String circleType;

    /** 圈子状态: ACTIVE-活跃, INACTIVE-不活跃, BANNED-封禁, DISBANDED-解散 */
    private String status;

    /** 创建者用户ID */
    private Long creatorId;

    /** 创建者昵称 */
    private String creatorNickname;

    /** 圈主用户ID */
    private Long ownerId;

    /** 关联POI类型标签 */
    private String poiTypeTags;

    /** 地理位置经度 */
    private Double longitude;

    /** 地理位置纬度 */
    private Double latitude;

    /** GeoHash编码 */
    private String geoHash;

    /** 所在城市编码 */
    private String cityCode;

    /** 所在城市名称 */
    private String cityName;

    /** 成员数量 */
    private Integer memberCount;

    /** 帖子数量 */
    private Integer postCount;

    /** 今日活跃数 */
    private Integer todayActiveCount;

    /** 圈子等级: LV1-LV10 */
    private Integer level;

    /** 圈子积分 */
    private Integer points;

    /** 圈子公告 */
    private String announcement;

    /** 入圈是否需要审核 */
    private Boolean requireApproval;

    /** 成员发帖权限: ALL-所有人, ADMIN-仅管理员 */
    private String postPermission;

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
