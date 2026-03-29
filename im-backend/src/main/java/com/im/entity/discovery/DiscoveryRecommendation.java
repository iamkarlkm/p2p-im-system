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
 * 探店推荐实体类
 * 存储个性化探店推荐结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryRecommendation {
    
    /** 推荐ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 推荐POI ID */
    private Long poiId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 推荐类型：NEW-新店, POPULAR-热门, HIDDEN-隐藏小店, SIMILAR-相似推荐 */
    private String recommendType;
    
    /** 推荐分数（0-100） */
    private Double recommendScore;
    
    /** 推荐理由 */
    private String recommendReason;
    
    /** 匹配标签列表 */
    private List<String> matchTags;
    
    /** 距离（米） */
    private Double distance;
    
    /** 预计到达时间（分钟） */
    private Integer estimatedArrivalMinutes;
    
    /** 热度排名 */
    private Integer popularityRank;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 人均消费 */
    private BigDecimal avgPrice;
    
    /** 营业时间匹配度 */
    private Double businessHourMatch;
    
    /** 口味匹配度 */
    private Double tasteMatch;
    
    /** 价格匹配度 */
    private Double priceMatch;
    
    /** 场景匹配度 */
    private Double sceneMatch;
    
    /** 用户好友推荐数 */
    private Integer friendRecommendCount;
    
    /** 探店达人推荐数 */
    private Integer expertRecommendCount;
    
    /** 店铺图片URL */
    private String coverImage;
    
    /** 推荐位置（经纬度） */
    private Double latitude;
    private Double longitude;
    
    /** 商圈名称 */
    private String businessDistrict;
    
    /** 分类标签 */
    private List<String> categoryTags;
    
    /** 特色标签 */
    private List<String> featureTags;
    
    /** 当前状态：OPEN-营业中, CLOSED-已打烊, BUSY-繁忙 */
    private String status;
    
    /** 是否需要排队 */
    private Boolean needQueue;
    
    /** 当前排队人数 */
    private Integer queueCount;
    
    /** 优惠活动列表 */
    private List<Map<String, Object>> promotions;
    
    /** 是否已打卡 */
    private Boolean hasCheckedIn;
    
    /** 打卡次数 */
    private Integer checkInCount;
    
    /** 上次访问时间 */
    private LocalDateTime lastVisitTime;
    
    /** 推荐状态：PENDING-待展示, SHOWN-已展示, CLICKED-已点击, VISITED-已访问 */
    private String displayStatus;
    
    /** 展示时间 */
    private LocalDateTime displayTime;
    
    /** 点击时间 */
    private LocalDateTime clickTime;
    
    /** 有效开始时间 */
    private LocalDateTime validStartTime;
    
    /** 有效结束时间 */
    private LocalDateTime validEndTime;
    
    /** 推荐来源：ALGORITHM-算法, EDITOR-编辑精选, FRIEND-好友推荐, GEO-地理围栏 */
    private String source;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 扩展字段 */
    private Map<String, Object> extra;
    
    /** 是否删除 */
    private Boolean deleted;
}
