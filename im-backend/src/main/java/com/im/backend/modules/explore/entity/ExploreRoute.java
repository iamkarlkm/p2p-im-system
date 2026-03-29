package com.im.backend.modules.explore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 探店路线实体类
 * 用于存储用户创建的探店路线规划
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_explore_route")
public class ExploreRoute extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 路线ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 创建用户ID */
    private Long userId;

    /** 路线名称 */
    private String routeName;

    /** 路线描述 */
    private String description;

    /** 路线主题标签（JSON数组：美食/文艺/购物等） */
    private String tags;

    /** 起点POI ID */
    private Long startPoiId;

    /** 起点名称 */
    private String startPoiName;

    /** 终点POI ID */
    private Long endPoiId;

    /** 终点名称 */
    private String endPoiName;

    /** 路线包含POI列表（JSON数组，有序） */
    private String poiList;

    /** POI数量 */
    private Integer poiCount;

    /** 总距离（米） */
    private BigDecimal totalDistance;

    /** 预计总耗时（分钟） */
    private Integer estimatedDuration;

    /** 预计总消费（元） */
    private BigDecimal estimatedCost;

    /** 交通方式：1-步行 2-骑行 3-驾车 4-公共交通 5-混合 */
    private Integer transportMode;

    /** 路线类型：1-系统推荐 2-用户自定义 */
    private Integer routeType;

    /** 路线数据（JSON格式，存储详细路径点） */
    private String routeData;

    /** 封面图片 */
    private String coverImage;

    /** 浏览次数 */
    private Integer viewCount;

    /** 收藏次数 */
    private Integer favoriteCount;

    /** 使用次数（用户实际导航使用） */
    private Integer useCount;

    /** 点赞次数 */
    private Integer likeCount;

    /** 推荐分数 */
    private BigDecimal recommendScore;

    /** 路线状态：0-草稿 1-已发布 2-已下架 */
    private Integer status;

    /** 是否公开：0-私有 1-公开 */
    private Integer isPublic;

    /** 是否精选路线：0-否 1-是 */
    private Integer isFeatured;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

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
     * 增加收藏次数
     */
    public void incrementFavoriteCount() {
        this.favoriteCount = (this.favoriteCount == null ? 0 : this.favoriteCount) + 1;
    }

    /**
     * 增加使用次数
     */
    public void incrementUseCount() {
        this.useCount = (this.useCount == null ? 0 : this.useCount) + 1;
    }

    /**
     * 计算推荐分数
     */
    public void calculateRecommendScore() {
        double score = 0.0;
        score += (viewCount == null ? 0 : viewCount) * 0.1;
        score += (favoriteCount == null ? 0 : favoriteCount) * 2.0;
        score += (useCount == null ? 0 : useCount) * 5.0;
        score += (likeCount == null ? 0 : likeCount) * 1.5;
        
        if (Integer.valueOf(1).equals(isFeatured)) {
            score *= 1.3;
        }
        
        this.recommendScore = BigDecimal.valueOf(score);
    }

    /**
     * 获取交通方式文本
     */
    public String getTransportModeText() {
        switch (transportMode == null ? 0 : transportMode) {
            case 1: return "步行";
            case 2: return "骑行";
            case 3: return "驾车";
            case 4: return "公共交通";
            case 5: return "混合";
            default: return "未知";
        }
    }

    /**
     * 获取路线类型文本
     */
    public String getRouteTypeText() {
        switch (routeType == null ? 0 : routeType) {
            case 1: return "系统推荐";
            case 2: return "用户自定义";
            default: return "未知";
        }
    }

    // ==================== 静态常量 ====================

    /** 交通方式：步行 */
    public static final int TRANSPORT_WALK = 1;
    
    /** 交通方式：骑行 */
    public static final int TRANSPORT_BIKE = 2;
    
    /** 交通方式：驾车 */
    public static final int TRANSPORT_DRIVE = 3;
    
    /** 交通方式：公共交通 */
    public static final int TRANSPORT_PUBLIC = 4;
    
    /** 交通方式：混合 */
    public static final int TRANSPORT_MIXED = 5;

    /** 路线类型：系统推荐 */
    public static final int ROUTE_TYPE_SYSTEM = 1;
    
    /** 路线类型：用户自定义 */
    public static final int ROUTE_TYPE_CUSTOM = 2;

    /** 状态：草稿 */
    public static final int STATUS_DRAFT = 0;
    
    /** 状态：已发布 */
    public static final int STATUS_PUBLISHED = 1;
    
    /** 状态：已下架 */
    public static final int STATUS_OFFLINE = 2;
}
