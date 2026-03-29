package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import java.util.List;

/**
 * 口碑榜单请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class ReputationRankingRequestDTO {
    
    /** 榜单类型: TOP_OVERALL/TOP_QUALITY/TOP_POPULAR/TOP_SERVICE/TOP_TASTE */
    private String listType;
    
    /** 商圈ID */
    private Long districtId;
    
    /** 类目ID */
    private Long categoryId;
    
    /** 城市编码 */
    private String cityCode;
    
    /** 榜单数量限制 */
    private Integer limit = 20;
    
    /** 排序维度 */
    private String sortBy;
}
