package com.im.entity.discovery;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

/**
 * 探店打卡记录实体类
 * 记录用户的探店打卡行为
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryCheckIn {
    
    /** 打卡ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** POI ID */
    private Long poiId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 打卡类型：AUTO-自动检测, MANUAL-手动打卡, QR-扫码打卡 */
    private String checkInType;
    
    /** 打卡经度 */
    private Double longitude;
    
    /** 打卡纬度 */
    private Double latitude;
    
    /** 打卡精度（米） */
    private Double accuracy;
    
    /** 打卡地址 */
    private String address;
    
    /** 地理位置名称 */
    private String locationName;
    
    /** 是否在进入围栏时打卡 */
    private Boolean enterGeofence;
    
    /** 是否在离开围栏时打卡 */
    private Boolean exitGeofence;
    
    /** 围栏触发时间 */
    private LocalDateTime geofenceTriggerTime;
    
    /** 停留时长（分钟） */
    private Integer dwellMinutes;
    
    /** 探店笔记内容 */
    private String content;
    
    /** 探店图片列表 */
    private List<String> images;
    
    /** 探店视频URL */
    private String videoUrl;
    
    /** 评分（1-5分） */
    private BigDecimal rating;
    
    /** 消费金额 */
    private BigDecimal spendAmount;
    
    /** 消费人数 */
    private Integer peopleCount;
    
    /** 人均消费 */
    private BigDecimal avgSpend;
    
    /** 标签列表 */
    private List<String> tags;
    
    /** 是否推荐 */
    private Boolean recommended;
    
    /** 推荐语 */
    private String recommendText;
    
    /** 是否公开 */
    private Boolean isPublic;
    
    /** 可见范围：ALL-所有人, FRIENDS-仅好友, PRIVATE-仅自己 */
    private String visibility;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 评论数 */
    private Integer commentCount;
    
    /** 分享数 */
    private Integer shareCount;
    
    /** 收藏数 */
    private Integer favoriteCount;
    
    /** 浏览数 */
    private Integer viewCount;
    
    /** 是否为探店达人打卡 */
    private Boolean isExpertCheckIn;
    
    /** 探店达人等级 */
    private Integer expertLevel;
    
    /** 是否为优质内容 */
    private Boolean isQuality;
    
    /** 内容质量分数 */
    private Double qualityScore;
    
    /** 审核状态：PENDING-审核中, APPROVED-已通过, REJECTED-已拒绝 */
    private String auditStatus;
    
    /** 审核原因 */
    private String auditReason;
    
    /** 打卡时间 */
    private LocalDateTime checkInTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 扩展字段 */
    private Map<String, Object> extra;
    
    /** 是否删除 */
    private Boolean deleted;
}
