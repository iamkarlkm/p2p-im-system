package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * POI信息实体类
 * 用于存储兴趣点（商家、景点、服务点等）信息
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@TableName("poi_info")
public class PoiInfo {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * POI唯一标识
     */
    private String poiId;
    
    /**
     * POI名称
     */
    private String name;
    
    /**
     * POI分类编码
     */
    private String categoryCode;
    
    /**
     * POI分类名称
     */
    private String categoryName;
    
    /**
     * 分类层级
     */
    private Integer categoryLevel;
    
    /**
     * 父分类编码
     */
    private String parentCategoryCode;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * GeoHash编码
     */
    private String geohash;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 电话号码
     */
    private String phone;
    
    /**
     * 营业时间
     */
    private String businessHours;
    
    /**
     * 平均消费
     */
    private BigDecimal avgPrice;
    
    /**
     * 评分（1-5）
     */
    private BigDecimal rating;
    
    /**
     * 评价数量
     */
    private Integer reviewCount;
    
    /**
     * 是否营业
     */
    private Boolean isOpen;
    
    /**
     * 距离（查询时临时使用）
     */
    @TableField(exist = false)
    private Double distance;
    
    /**
     * 人气值
     */
    private Long popularity;
    
    /**
     * 标签（逗号分隔）
     */
    private String tags;
    
    /**
     * 图片URL列表（逗号分隔）
     */
    private String imageUrls;
    
    /**
     * 图标URL
     */
    private String iconUrl;
    
    /**
     * 简介描述
     */
    private String description;
    
    /**
     * 特点/服务
     */
    private String features;
    
    /**
     * 关联商家ID
     */
    private Long merchantId;
    
    /**
     * 数据来源：1-内部 2-高德 3-百度 4-腾讯
     */
    private Integer dataSource;
    
    /**
     * 第三方POI ID
     */
    private String thirdPartyId;
    
    /**
     * 审核状态：0-待审核 1-已审核 2-审核拒绝
     */
    private Integer auditStatus;
    
    /**
     * 权重（排序用）
     */
    private Integer weight;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
    
    /**
     * 无参构造
     */
    public PoiInfo() {
        this.rating = BigDecimal.valueOf(4.0);
        this.reviewCount = 0;
        this.popularity = 0L;
        this.isOpen = true;
        this.auditStatus = 0;
        this.weight = 0;
    }
    
    /**
     * 获取标签列表
     */
    public List<String> getTagList() {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return List.of(tags.split(","));
    }
    
    /**
     * 获取图片URL列表
     */
    public List<String> getImageUrlList() {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return List.of();
        }
        return List.of(imageUrls.split(","));
    }
    
    /**
     * 是否高评分POI
     */
    public boolean isHighRated() {
        return rating != null && rating.compareTo(BigDecimal.valueOf(4.5)) >= 0;
    }
    
    /**
     * 是否热门POI
     */
    public boolean isPopular() {
        return popularity > 10000 || reviewCount > 1000;
    }
    
    /**
     * 计算综合得分
     */
    public double calculateScore() {
        double score = 0.0;
        
        // 评分权重40%
        if (rating != null) {
            score += rating.doubleValue() * 0.4;
        }
        
        // 人气权重30%
        long popularityScore = Math.min(popularity / 10000, 10);
        score += popularityScore * 0.3;
        
        // 评价数量权重20%
        int reviewScore = Math.min(reviewCount / 100, 10);
        score += reviewScore * 0.2;
        
        // 距离权重10%（距离越近分越高）
        if (distance != null) {
            double distanceScore = Math.max(0, 10 - distance / 1000);
            score += distanceScore * 0.1;
        }
        
        return score;
    }
    
    /**
     * 是否营业中
     */
    public boolean isBusinessOpen() {
        if (!isOpen) return false;
        
        // 简化判断，实际应根据businessHours解析
        // TODO: 实现完整营业时间判断
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoiInfo poiInfo = (PoiInfo) o;
        return poiId != null && poiId.equals(poiInfo.poiId);
    }
    
    @Override
    public int hashCode() {
        return poiId != null ? poiId.hashCode() : 0;
    }
}
