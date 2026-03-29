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
 * 探店路线规划实体类
 * 存储多店铺串联探店路线
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryRoute {
    
    /** 路线ID */
    private Long id;
    
    /** 路线名称 */
    private String routeName;
    
    /** 路线描述 */
    private String description;
    
    /** 创建用户ID */
    private Long userId;
    
    /** 路线类型：SYSTEM-系统推荐, USER-用户创建, OFFICIAL-官方精选 */
    private String routeType;
    
    /** 场景标签：DATE-约会, FAMILY-亲子, FRIENDS-朋友聚会, SOLO-独自探店 */
    private String sceneTag;
    
    /** 预算范围：LOW-经济型, MEDIUM-中等, HIGH-高端 */
    private String budgetLevel;
    
    /** 预计总消费 */
    private BigDecimal estimatedTotalCost;
    
    /** 预计总时长（分钟） */
    private Integer estimatedTotalMinutes;
    
    /** 起点经度 */
    private Double startLongitude;
    
    /** 起点纬度 */
    private Double startLatitude;
    
    /** 起点地址 */
    private String startAddress;
    
    /** 终点经度 */
    private Double endLongitude;
    
    /** 终点纬度 */
    private Double endLatitude;
    
    /** 终点地址 */
    private String endAddress;
    
    /** 路线总距离（米） */
    private Double totalDistance;
    
    /** 包含的POI数量 */
    private Integer poiCount;
    
    /** POI列表（按顺序） */
    private List<DiscoveryRoutePoi> poiList;
    
    /** 路线图片 */
    private String coverImage;
    
    /** 路线标签 */
    private List<String> tags;
    
    /** 适合时间段：MORNING-上午, AFTERNOON-下午, EVENING-晚上, NIGHT-深夜 */
    private String suitableTime;
    
    /** 适合人数 */
    private Integer suitablePeople;
    
    /** 推荐指数（1-5） */
    private BigDecimal recommendScore;
    
    /** 使用次数 */
    private Integer usageCount;
    
    /** 收藏数 */
    private Integer favoriteCount;
    
    /** 分享数 */
    private Integer shareCount;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 浏览数 */
    private Integer viewCount;
    
    /** 是否为精选路线 */
    private Boolean isFeatured;
    
    /** 精选排序 */
    private Integer featureOrder;
    
    /** 状态：DRAFT-草稿, PUBLISHED-已发布, OFFLINE-已下线 */
    private String status;
    
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
