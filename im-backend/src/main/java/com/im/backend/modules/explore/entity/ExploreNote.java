package com.im.backend.modules.explore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 探店笔记实体类
 * 用于存储用户发布的探店笔记内容
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_explore_note")
public class ExploreNote extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 笔记ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 发布用户ID */
    private Long userId;

    /** 关联POI商户ID */
    private Long poiId;

    /** 商户名称（冗余存储） */
    private String poiName;

    /** 笔记标题 */
    private String title;

    /** 笔记内容 */
    private String content;

    /** 笔记类型：1-图文 2-短视频 */
    private Integer noteType;

    /** 封面图片URL */
    private String coverImage;

    /** 图片列表（JSON数组） */
    private String images;

    /** 视频URL */
    private String videoUrl;

    /** 视频时长（秒） */
    private Integer videoDuration;

    /** 探店评分（1-5分） */
    private BigDecimal rating;

    /** 口味评分 */
    private BigDecimal tasteRating;

    /** 环境评分 */
    private BigDecimal environmentRating;

    /** 服务评分 */
    private BigDecimal serviceRating;

    /** 性价比评分 */
    private BigDecimal valueRating;

    /** 人均消费（元） */
    private BigDecimal perCapitaCost;

    /** 消费标签（JSON数组：约会/聚餐/商务等） */
    private String tags;

    /** 地理位置-经度 */
    private BigDecimal longitude;

    /** 地理位置-纬度 */
    private BigDecimal latitude;

    /** 地理位置名称 */
    private String locationName;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞次数 */
    private Integer likeCount;

    /** 收藏次数 */
    private Integer favoriteCount;

    /** 评论次数 */
    private Integer commentCount;

    /** 分享次数 */
    private Integer shareCount;

    /** 推荐权重分数 */
    private BigDecimal recommendScore;

    /** 笔记状态：0-草稿 1-已发布 2-审核中 3-已拒绝 4-已下架 */
    private Integer status;

    /** 是否精选：0-否 1-是 */
    private Integer isFeatured;

    /** 是否置顶：0-否 1-是 */
    private Integer isPinned;

    /** 审核拒绝原因 */
    private String rejectReason;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 最后修改时间 */
    private LocalDateTime lastModifiedTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 增加浏览次数
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    /**
     * 增加点赞次数
     */
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }

    /**
     * 减少点赞次数
     */
    public void decrementLikeCount() {
        this.likeCount = Math.max(0, (this.likeCount == null ? 0 : this.likeCount) - 1);
    }

    /**
     * 增加收藏次数
     */
    public void incrementFavoriteCount() {
        this.favoriteCount = (this.favoriteCount == null ? 0 : this.favoriteCount) + 1;
    }

    /**
     * 减少收藏次数
     */
    public void decrementFavoriteCount() {
        this.favoriteCount = Math.max(0, (this.favoriteCount == null ? 0 : this.favoriteCount) - 1);
    }

    /**
     * 增加评论次数
     */
    public void incrementCommentCount() {
        this.commentCount = (this.commentCount == null ? 0 : this.commentCount) + 1;
    }

    /**
     * 减少评论次数
     */
    public void decrementCommentCount() {
        this.commentCount = Math.max(0, (this.commentCount == null ? 0 : this.commentCount) - 1);
    }

    /**
     * 计算综合推荐分数
     * 基于浏览量、点赞、收藏、评论等数据计算
     */
    public void calculateRecommendScore() {
        double score = 0.0;
        score += (viewCount == null ? 0 : viewCount) * 0.1;
        score += (likeCount == null ? 0 : likeCount) * 2.0;
        score += (favoriteCount == null ? 0 : favoriteCount) * 3.0;
        score += (commentCount == null ? 0 : commentCount) * 2.5;
        score += (shareCount == null ? 0 : shareCount) * 4.0;
        
        // 精选笔记加权
        if (Integer.valueOf(1).equals(isFeatured)) {
            score *= 1.5;
        }
        
        // 评分加权
        if (rating != null) {
            score *= (1 + rating.doubleValue() / 10);
        }
        
        this.recommendScore = BigDecimal.valueOf(score);
    }

    /**
     * 检查笔记是否已发布
     */
    public boolean isPublished() {
        return Integer.valueOf(1).equals(status);
    }

    /**
     * 检查是否为精选笔记
     */
    public boolean isFeatured() {
        return Integer.valueOf(1).equals(isFeatured);
    }

    /**
     * 获取笔记类型的文本描述
     */
    public String getNoteTypeText() {
        switch (noteType == null ? 0 : noteType) {
            case 1: return "图文";
            case 2: return "短视频";
            default: return "未知";
        }
    }

    /**
     * 获取状态的文本描述
     */
    public String getStatusText() {
        switch (status == null ? 0 : status) {
            case 0: return "草稿";
            case 1: return "已发布";
            case 2: return "审核中";
            case 3: return "已拒绝";
            case 4: return "已下架";
            default: return "未知";
        }
    }

    // ==================== 静态常量 ====================

    /** 笔记类型：图文 */
    public static final int NOTE_TYPE_IMAGE_TEXT = 1;
    
    /** 笔记类型：短视频 */
    public static final int NOTE_TYPE_VIDEO = 2;

    /** 状态：草稿 */
    public static final int STATUS_DRAFT = 0;
    
    /** 状态：已发布 */
    public static final int STATUS_PUBLISHED = 1;
    
    /** 状态：审核中 */
    public static final int STATUS_AUDITING = 2;
    
    /** 状态：已拒绝 */
    public static final int STATUS_REJECTED = 3;
    
    /** 状态：已下架 */
    public static final int STATUS_OFFLINE = 4;
}
