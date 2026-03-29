package com.im.entity.discovery;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 探店路线POI节点实体类
 * 存储路线中的单个POI节点信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryRoutePoi {
    
    /** 节点ID */
    private Long id;
    
    /** 路线ID */
    private Long routeId;
    
    /** POI ID */
    private Long poiId;
    
    /** 节点顺序 */
    private Integer sequence;
    
    /** 节点名称 */
    private String poiName;
    
    /** 节点类型：FOOD-美食, DRINK-饮品, ENTERTAINMENT-娱乐, SHOPPING-购物, SIGHTSEEING-观光 */
    private String poiType;
    
    /** 经度 */
    private Double longitude;
    
    /** 纬度 */
    private Double latitude;
    
    /** 地址 */
    private String address;
    
    /** 距离上一节点的距离（米） */
    private Double distanceFromPrev;
    
    /** 预计从上一节点到达时间（分钟） */
    private Integer estimatedMinutesFromPrev;
    
    /** 建议停留时长（分钟） */
    private Integer suggestedStayMinutes;
    
    /** 预计消费金额 */
    private BigDecimal estimatedCost;
    
    /** 推荐语 */
    private String recommendText;
    
    /** 特色介绍 */
    private String featureIntro;
    
    /** 必点推荐 */
    private String mustTryItems;
    
    /** 图片URL */
    private String imageUrl;
    
    /** 营业时间 */
    private String businessHours;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 人均消费 */
    private BigDecimal avgPrice;
    
    /** 标签列表 */
    private String tags;
    
    /** 备注 */
    private String remark;
}
