package com.im.entity.discovery;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 探店内容实体类
 * 存储探店笔记、短视频等内容
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryContent {
    
    /** 内容ID */
    private Long id;
    
    /** 发布用户ID */
    private Long userId;
    
    /** 内容类型：NOTE-笔记, VIDEO-视频, GALLERY-图集 */
    private String contentType;
    
    /** 内容标题 */
    private String title;
    
    /** 内容正文 */
    private String content;
    
    /** 内容摘要 */
    private String summary;
    
    /** 关联POI ID */
    private Long poiId;
    
    /** 关联商户ID */
    private Long merchantId;
    
    /** 关联店铺名称 */
    private String storeName;
    
    /** 图片列表 */
    private List<String> images;
    
    /** 视频URL */
    private String videoUrl;
    
    /** 视频封面 */
    private String videoCover;
    
    /** 视频时长（秒） */
    private Integer videoDuration;
    
    /** 标签列表 */
    private List<String> tags;
    
    /** 话题标签 */
    private List<String> topics;
    
    /** 内容来源：CHECKIN-打卡笔记, REVIEW-评价, SHARE-分享, ORIGINAL-原创内容 */
    private String source;
    
    /** 是否优质内容 */
    private Boolean isQuality;
    
    /** 内容质量分 */
    private Double qualityScore;
    
    /** 是否推荐到首页 */
    private Boolean isRecommended;
    
    /** 推荐权重 */
    private Integer recommendWeight;
    
    /** 浏览数 */
    private Integer viewCount;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 评论数 */
    private Integer commentCount;
    
    /** 分享数 */
    private Integer shareCount;
    
    /** 收藏数 */
    private Integer favoriteCount;
    
    /** 转发数 */
    private Integer repostCount;
    
    /** 是否为探店达人内容 */
    private Boolean isExpertContent;
    
    /** 达人等级 */
    private Integer expertLevel;
    
    /** 是否签约达人 */
    private Boolean isSignedExpert;
    
    /** 内容状态：DRAFT-草稿, PENDING-审核中, PUBLISHED-已发布, REJECTED-已拒绝, OFFLINE-已下线 */
    private String status;
    
    /** 审核原因 */
    private String auditReason;
    
    /** 发布时间 */
    private LocalDateTime publishTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 扩展字段 */
    private Map<String, Object> extra;
    
    /** 是否删除 */
    private Boolean deleted;
}
