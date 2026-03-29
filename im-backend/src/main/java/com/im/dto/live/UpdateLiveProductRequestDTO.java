package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 更新直播商品请求DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "更新直播商品请求")
public class UpdateLiveProductRequestDTO {

    @Size(max = 100, message = "名称长度不能超过100个字符")
    @Schema(description = "商品名称")
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

    @Min(value = 0, message = "原价不能为负数")
    @Schema(description = "原价（元）")
    private BigDecimal originalPrice;

    @Min(value = 0, message = "直播价不能为负数")
    @Schema(description = "直播价（元）")
    private BigDecimal livePrice;

    @Min(value = 0, message = "库存不能为负数")
    @Schema(description = "库存数量")
    private Integer stock;

    @Min(value = 0, message = "限购数量不能为负数")
    @Schema(description = "限购数量（0表示不限购）")
    private Integer limitPerUser;

    @Schema(description = "排序权重")
    private Integer sortOrder;

    @Schema(description = "商品详情页URL")
    private String detailUrl;

    @Schema(description = "小程序页面路径")
    private String mpPagePath;

    @Schema(description = "运费（元），0表示包邮")
    private BigDecimal freight;

    @Schema(description = "重量（克）")
    private Integer weight;
}
