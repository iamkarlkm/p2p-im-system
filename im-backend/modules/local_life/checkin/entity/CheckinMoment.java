package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 签到动态实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin_moment")
public class CheckinMoment extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 签到记录ID
     */
    private Long checkinId;

    /**
     * POI ID
     */
    private String poiId;

    /**
     * POI名称
     */
    private String poiName;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 图片URLs(JSON数组)
     */
    private String imageUrls;

    /**
     * 地理位置名称
     */
    private String locationName;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 是否推荐
     */
    private Boolean isRecommended;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
