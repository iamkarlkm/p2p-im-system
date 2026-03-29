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
 * 探店榜单实体类
 * 存储各类探店榜单信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryRanking {
    
    /** 榜单ID */
    private Long id;
    
    /** 榜单名称 */
    private String rankingName;
    
    /** 榜单类型：HOT-热门榜, NEW-新店榜, HIDDEN-隐藏小店, FOOD-美食榜, DRINK-饮品榜 */
    private String rankingType;
    
    /** 榜单描述 */
    private String description;
    
    /** 榜单副标题 */
    private String subTitle;
    
    /** 城市代码 */
    private String cityCode;
    
    /** 城市名称 */
    private String cityName;
    
    /** 区域代码 */
    private String districtCode;
    
    /** 区域名称 */
    private String districtName;
    
    /** 商圈ID */
    private Long businessDistrictId;
    
    /** 商圈名称 */
    private String businessDistrictName;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 分类名称 */
    private String categoryName;
    
    /** 榜单封面图 */
    private String coverImage;
    
    /** 榜单图标 */
    private String icon;
    
    /** 榜单主题色 */
    private String themeColor;
    
    /** 更新周期：DAILY-日榜, WEEKLY-周榜, MONTHLY-月榜 */
    private String updateCycle;
    
    /** 榜单开始时间 */
    private LocalDateTime startTime;
    
    /** 榜单结束时间 */
    private LocalDateTime endTime;
    
    /** 上榜店铺数量 */
    private Integer storeCount;
    
    /** 上榜店铺列表 */
    private List<DiscoveryRankingItem> items;
    
    /** 榜单浏览数 */
    private Integer viewCount;
    
    /** 榜单分享数 */
    private Integer shareCount;
    
    /** 榜单收藏数 */
    private Integer favoriteCount;
    
    /** 是否为官方榜单 */
    private Boolean isOfficial;
    
    /** 是否为精选榜单 */
    private Boolean isFeatured;
    
    /** 精选排序 */
    private Integer featureOrder;
    
    /** 排序权重 */
    private Integer sortOrder;
    
    /** 状态：ACTIVE-展示中, INACTIVE-未激活, EXPIRED-已过期 */
    private String status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 最后统计时间 */
    private LocalDateTime lastStatisticsTime;
    
    /** 扩展字段 */
    private Map<String, Object> extra;
    
    /** 是否删除 */
    private Boolean deleted;
}
