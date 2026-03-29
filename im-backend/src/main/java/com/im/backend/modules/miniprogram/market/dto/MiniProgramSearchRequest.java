package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序搜索请求DTO
 */
@Data
public class MiniProgramSearchRequest {

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 场景类型
     */
    private Integer sceneType;

    /**
     * 用户当前位置GeoHash
     */
    private String locationHash;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 排序方式：1-综合 2-评分 3-下载量 4-距离
     */
    private Integer sortType = 1;

    /**
     * 筛选条件：是否仅看推荐
     */
    private Boolean onlyRecommended = false;

    /**
     * 筛选条件：最低评分
     */
    private BigDecimal minRating;

    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
