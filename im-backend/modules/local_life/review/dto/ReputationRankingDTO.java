package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 口碑榜单DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class ReputationRankingDTO {
    
    /** 排名 */
    private Integer rank;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 商户名称 */
    private String merchantName;
    
    /** 商户Logo */
    private String merchantLogo;
    
    /** 主图 */
    private String mainImage;
    
    /** 类目名称 */
    private String categoryName;
    
    /** 商圈名称 */
    private String districtName;
    
    /** 平均评分 */
    private BigDecimal averageRating;
    
    /** 评价数 */
    private Integer reviewCount;
    
    /** 人均消费 */
    private BigDecimal perCapita;
    
    /** 口碑分 */
    private BigDecimal reputationScore;
    
    /** 距离 (米) */
    private Integer distance;
    
    /** 标签列表 */
    private String tags;
}
