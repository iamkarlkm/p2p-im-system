package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动媒体资源实体
 * 存储活动的图片、视频等媒体资源
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_activity_media")
public class ActivityMedia {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 媒体唯一标识 */
    private String mediaCode;

    /** 关联活动ID */
    private Long activityId;

    /** 媒体类型: IMAGE-图片, VIDEO-视频, LIVE-直播 */
    private String mediaType;

    /** 媒体URL */
    private String mediaUrl;

    /** 缩略图URL */
    private String thumbnailUrl;

    /** 视频时长(秒) */
    private Integer duration;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 宽度 */
    private Integer width;

    /** 高度 */
    private Integer height;

    /** 上传用户ID */
    private Long uploaderId;

    /** 上传用户昵称 */
    private String uploaderNickname;

    /** 媒体描述 */
    private String description;

    /** 排序权重 */
    private Integer sortOrder;

    /** 是否封面 */
    private Boolean isCover;

    /** 媒体状态: NORMAL-正常, PROCESSING-处理中, FAILED-处理失败 */
    private String status;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞次数 */
    private Integer likeCount;

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
