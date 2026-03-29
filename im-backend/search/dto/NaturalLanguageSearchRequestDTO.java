package com.im.search.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 自然语言搜索请求DTO
 * 支持口语化查询理解
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaturalLanguageSearchRequestDTO {

    /** 自然语言查询文本 */
    @NotBlank(message = "查询内容不能为空")
    private String query;

    /** 用户当前经度 */
    @NotNull(message = "经度不能为空")
    private Double longitude;

    /** 用户当前纬度 */
    @NotNull(message = "纬度不能为空")
    private Double latitude;

    /** 搜索半径（米），默认5000 */
    @Min(value = 100, message = "搜索半径最小100米")
    @Max(value = 50000, message = "搜索半径最大50公里")
    private Integer radius = 5000;

    /** 城市编码，用于限定搜索范围 */
    private String cityCode;

    /** 分类过滤 */
    private String category;

    /** 价格区间：最小值 */
    private Integer minPrice;

    /** 价格区间：最大值 */
    private Integer maxPrice;

    /** 最低评分 */
    @Min(1)
    @Max(5)
    private Float minRating;

    /** 特色标签过滤 */
    private List<String> tags;

    /** 排序方式：distance-距离 hot-热度 rating-评分 price-价格 */
    private String sortBy = "distance";

    /** 排序方向：asc-升序 desc-降序 */
    private String sortOrder = "asc";

    /** 页码 */
    @Min(0)
    private Integer page = 0;

    /** 每页大小 */
    @Min(1)
    @Max(50)
    private Integer size = 20;

    /** 用户ID，用于个性化推荐 */
    private Long userId;

    /** 会话ID，用于多轮对话上下文 */
    private String sessionId;

    /** 是否启用语义理解 */
    private Boolean enableSemantic = true;

    /** 是否启用个性化排序 */
    private Boolean enablePersonalization = true;

    /** 搜索场景：general-通用 dining-餐饮 entertainment-娱乐 shopping-购物 */
    private String scene = "general";
}
