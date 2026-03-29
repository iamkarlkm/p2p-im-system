package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 添加直播商品请求DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "添加直播商品请求")
public class AddLiveProductRequestDTO {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100个字符")
    @Schema(description = "商品名称", required = true)
    private String name;

    @Size(max = 200, message = "副标题长度不能超过200个字符")
    @Schema(description = "商品副标题")
    private String subtitle;

    @Schema(description = "商品主图URL")
    private String mainImage;

    @Schema(description = "商品图片列表")
    private List<String> images;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品分类ID")
    private Long categoryId;

    @NotNull(message = "原价不能为空")
    @Min(value = 0, message = "原价不能为负数")
    @Schema(description = "原价（元）", required = true)
    private BigDecimal originalPrice;

    @NotNull(message = "直播价不能为空")
    @Min(value = 0, message = "直播价不能为负数")
    @Schema(description = "直播价（元）", required = true)
    private BigDecimal livePrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    @Schema(description = "库存数量", required = true)
    private Integer stock;

    @Min(value = 0, message = "限购数量不能为负数")
    @Schema(description = "限购数量（0表示不限购）", example = "0")
    private Integer limitPerUser;

    @Schema(description = "排序权重", example = "0")
    private Integer sortOrder;

    @Schema(description = "商品详情页URL")
    private String detailUrl;

    @Schema(description = "小程序页面路径")
    private String mpPagePath;

    @Schema(description = "运费（元），0表示包邮", example = "0")
    private BigDecimal freight;

    @Schema(description = "重量（克）", example = "0")
    private Integer weight;

    @Schema(description = "商品规格")
    private List<ProductSpecDTO> specifications;

    @Schema(description = "商品属性")
    private List<ProductAttrDTO> attributes;

    /**
     * 商品规格
     */
    @Data
    @Schema(description = "商品规格")
    public static class ProductSpecDTO {

        @Schema(description = "规格名称", example = "颜色")
        private String name;

        @Schema(description = "规格值列表", example = "[\"红色\",\"蓝色\",\"黑色\"]")
        private List<String> values;
    }

    /**
     * 商品属性
     */
    @Data
    @Schema(description = "商品属性")
    public static class ProductAttrDTO {

        @Schema(description = "属性名称", example = "品牌")
        private String name;

        @Schema(description = "属性值", example = "Apple")
        private String value;
    }
}
