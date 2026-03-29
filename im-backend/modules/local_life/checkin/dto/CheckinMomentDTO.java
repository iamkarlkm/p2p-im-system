package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 签到动态响应DTO
 */
@Data
public class CheckinMomentDTO {

    /**
     * 动态ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * POI名称
     */
    private String poiName;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 图片URLs
     */
    private List<String> imageUrls;

    /**
     * 地理位置名称
     */
    private String locationName;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 当前用户是否已点赞
     */
    private Boolean hasLiked;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
}
