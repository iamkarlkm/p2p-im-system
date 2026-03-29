package com.im.entity.discovery;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 探店榜单项实体类
 * 存储榜单中的单个店铺排名信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryRankingItem {
    
    /** 项ID */
    private Long id;
    
    /** 榜单ID */
    private Long rankingId;
    
    /** 排名 */
    private Integer rank;
    
    /** 排名变化：UP-上升, DOWN-下降, SAME-持平, NEW-新上榜 */
    private String rankChange;
    
    /** 排名变化数值 */
    private Integer rankChangeValue;
    
    /** POI ID */
    private Long poiId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 店铺名称 */
    private String storeName;
    
    /** 店铺图片 */
    private String storeImage;
    
    /** 分类名称 */
    private String categoryName;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 人均消费 */
    private BigDecimal avgPrice;
    
    /** 距离（米） */
    private Double distance;
    
    /** 热度分数 */
    private Double heatScore;
    
    /** 热度值 */
    private Integer heatValue;
    
    /** 推荐理由 */
    private String recommendReason;
    
    /** 标签列表 */
    private String tags;
    
    /** 商圈名称 */
    private String businessDistrict;
    
    /** 本月浏览数 */
    private Integer monthViewCount;
    
    /** 本月打卡数 */
    private Integer monthCheckInCount;
    
    /** 本月收藏数 */
    private Integer monthFavoriteCount;
    
    /** 综合得分 */
    private BigDecimal compositeScore;
    
    /** 排序权重 */
    private Integer sortOrder;
    
    /** 是否为赞助商 */
    private Boolean isSponsored;
    
    /** 赞助标签 */
    private String sponsorTag;
    
    /** 备注 */
    private String remark;
}
