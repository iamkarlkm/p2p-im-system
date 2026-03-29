package com.im.entity.discovery;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 新店发现实体类
 * 存储新开店铺信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryNewStore {
    
    /** 发现ID */
    private Long id;
    
    /** POI ID */
    private Long poiId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 店铺名称 */
    private String storeName;
    
    /** 开业时间 */
    private LocalDateTime openingTime;
    
    /** 开业类型：NEW-全新开业, REBRAND-品牌升级, BRANCH-分店开业 */
    private String openingType;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 分类名称 */
    private String categoryName;
    
    /** 经度 */
    private Double longitude;
    
    /** 纬度 */
    private Double latitude;
    
    /** 详细地址 */
    private String address;
    
    /** 商圈名称 */
    private String businessDistrict;
    
    /** 城市代码 */
    private String cityCode;
    
    /** 城市名称 */
    private String cityName;
    
    /** 店铺图片列表 */
    private List<String> images;
    
    /** 封面图 */
    private String coverImage;
    
    /** 店铺简介 */
    private String introduction;
    
    /** 特色标签 */
    private List<String> featureTags;
    
    /** 开业活动 */
    private String openingPromotions;
    
    /** 优惠活动列表 */
    private List<Map<String, Object>> promotions;
    
    /** 人均消费 */
    private Double avgPrice;
    
    /** 营业时间 */
    private String businessHours;
    
    /** 联系电话 */
    private String phone;
    
    /** 发现热度分数 */
    private Double heatScore;
    
    /** 热度排名 */
    private Integer heatRank;
    
    /** 浏览数 */
    private Integer viewCount;
    
    /** 收藏数 */
    private Integer favoriteCount;
    
    /** 想去数 */
    private Integer wantToGoCount;
    
    /** 打卡数 */
    private Integer checkInCount;
    
    /** 是否精选新店 */
    private Boolean isFeatured;
    
    /** 精选排序 */
    private Integer featureOrder;
    
    /** 推荐用户画像标签 */
    private List<String> targetUserTags;
    
    /** 状态：ACTIVE-展示中, EXPIRED-已过期, OFFLINE-已下线 */
    private String status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 过期时间 */
    private LocalDateTime expireTime;
    
    /** 扩展字段 */
    private Map<String, Object> extra;
    
    /** 是否删除 */
    private Boolean deleted;
}
